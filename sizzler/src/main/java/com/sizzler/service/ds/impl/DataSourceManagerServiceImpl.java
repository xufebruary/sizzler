package com.sizzler.service.ds.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.metamodel.schema.ColumnTypeImpl;
import org.apache.metamodel.schema.MutableColumn;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.schema.TableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.exception.BusinessErrorCode;
import com.sizzler.common.exception.BusinessException;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.log.LogMessageUtil;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.MetaContentNode;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.sizzler.UserConnectionConfig;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.dao.ds.UserConnectionSourceDao;
import com.sizzler.datasource.reflex.ClassReflex;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.UserConnectionSourceTable;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.DsContentView;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.ds.dto.UiAccountConnection;
import com.sizzler.domain.ds.dto.UserAccountSource;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableColumnDto;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableDto;
import com.sizzler.domain.ds.vo.UserConnectionSourceTableVo;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.domain.dto.UIEditorData;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.provider.common.MetaRequest;
import com.sizzler.provider.common.UpdateDataResponse;
import com.sizzler.provider.common.impl.DefaultMetaRequest;
import com.sizzler.service.DataSourceManagerService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

@Service("dataSourceManagerService")
public class DataSourceManagerServiceImpl implements DataSourceManagerService {

  private Logger logger = LoggerFactory.getLogger(DataSourceManagerServiceImpl.class);

  @Autowired
  private ServiceFactory serviceFactory;

  @Autowired
  private UserConnectionSourceDao userConnectionSourceDao;

  /**
   * @Description: 保存，更新，新增一个excel文件结构（数据保存到HDFS）.
   * @date: 2016/2/1
   * @author: zhangli
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserConnectionSourceVo saveOrUpdateEditorDataToFile(UserConnectionSourceVo sourceVo)
      throws BusinessException {
    LogMessage logMessage = new LogMessage();
    try {
      String dsCode = serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById(sourceVo.getDsId())
          .getCode();
      String operateType = sourceVo.getOperateType();
      boolean editSaveFlag = sourceVo.getOperateType().equalsIgnoreCase(
          Constants.UI_OPERATE_EDIT_SAVE);
      boolean saveFlag = sourceVo.getOperateType().equalsIgnoreCase(Constants.UI_OPERATE_SAVE);
      boolean updateFlag = sourceVo.getOperateType().equalsIgnoreCase(Constants.UI_OPERATE_UPDATE);
      LogMessageUtil.addOperateInfoOfFlag(logMessage, saveFlag, updateFlag, editSaveFlag);
      // boolean autoUpdateFlag =
      // sourceVo.getOperateType().equalsIgnoreCase(Constants.UI_OPERATE_AUTO_UPDATE);
      long currentTime = System.currentTimeMillis();
      // refresh --在执行update时，默认的会执行一次取得editData的操作
      String connectionId = sourceVo.getConnectionId();
      String sourceId = sourceVo.getSourceId();
      String logUid = String.valueOf(sourceVo.getUid());
      LogMessageUtil.addBasicInfo(logMessage, logUid, "saveOrUpdateEditorDataToFile");
      LogMessageUtil.addOperateInfoOfExcel(logMessage, connectionId, sourceId, dsCode);
      if (updateFlag) {
        sourceVo = getEditorDataByConnectionId(connectionId, sourceId, true, false, true);
        sourceVo.setOperateType(operateType);
      }
      UserConnection userConnection = new UserConnection();

      if (saveFlag && dsCode.equalsIgnoreCase(DsConstants.DS_CODE_UPLOAD)) {
        // 如果是Upload的保存，才需要更新Connection的状态
        userConnection.setConnectionId(connectionId);
        userConnection.setStatus(Constants.validate);
        serviceFactory.getPtoneUserConnectionService().update(userConnection);
      }
      userConnection = serviceFactory.getPtoneUserConnectionService().get(connectionId);
      UserConnectionSourceDto userConnectionSourceDto = new UserConnectionSourceDto();
      if (saveFlag) {
        BeanUtils.copyProperties(sourceVo, userConnectionSourceDto);
        userConnectionSourceDto.setRemotePath(sourceVo.getRemotePath());
        userConnectionSourceDto.setSourceId(sourceId);
        userConnectionSourceDto.setDsCode(dsCode);
        userConnectionSourceDto.setSpaceId(userConnection.getSpaceId());
      } else {
        userConnectionSourceDto = serviceFactory.getUserConnectionSourceService().getSourceDto(
            sourceId);
      }
      userConnectionSourceDto.setLastModifiedDate(sourceVo.getLastModifiedDate());
      userConnectionSourceDto.setUpdateTime(currentTime);

      serviceFactory.getDataSourceBuild().buildSourceDtoBySourceVo(sourceVo,
          userConnectionSourceDto);

      serviceFactory.getDataSourceBuild().saveTablesAndColumns(userConnectionSourceDto,
          editSaveFlag, saveFlag);

      UserConnectionSource userConnectionSource = userConnectionSourceDto.parseToSource();

      if (editSaveFlag) {
        serviceFactory.getUserConnectionSourceService().update(userConnectionSource);
      } else if (saveFlag) {
        // 设置创建时间
        userConnectionSource.setCreateTime(currentTime);
        sourceVo.setCreateTime(currentTime);

        if (userConnectionSource.getDsId() == DsConstants.DS_ID_GOOGLEDRIVE) {
          // 设置source文件自动更新的默认值
          this.setSourceDefaultUpdateSetting(userConnectionSource);

          // 根据用户设置，生成quartz cron表达式
          this.buildSourceCronExpr(userConnectionSource);
        }
        serviceFactory.getUserConnectionSourceService().save(userConnectionSource);
      } else {
        serviceFactory.getUserConnectionSourceService().updateUpdateTime(sourceId, currentTime);
        sourceVo.setCreateTime(userConnectionSource.getCreateTime());
      }
      sourceVo.setUpdateTime(currentTime);
      serviceFactory.getDataSourceBuild().saveFileDataToDB(userConnection, sourceVo,
          userConnectionSourceDto, false);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      LogMessageUtil.addErrorExceptionMessage(logMessage, e.getMessage());
      throw new BusinessException(BusinessErrorCode.Space.ADD_OR_UPDATE_SPACE_TABLES_ERROR,
          "save or update editor data to file.");
    } finally {
      logger.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return sourceVo;
  }

  /**
   * 设置source文件自动更新的默认值 （空间所有人的来源判断所在时区的每天的5点）
   * 
   * @param userConnectionSource
   * @date: 2016年11月9日
   * @author peng.xu
   */
  private void setSourceDefaultUpdateSetting(UserConnectionSource userConnectionSource) {
    String timezone = userConnectionSource.getTimezone();
    if (StringUtil.isBlank(timezone)) {
      // 判断时区信息（根据空间所有人的source进行判断）
      String userSource = null;
      PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(
          userConnectionSource.getSpaceId());
      if (spaceInfo != null) {
        PtoneUser spaceOwner = serviceFactory.getUserService().get(spaceInfo.getOwnerId());
        userSource = spaceOwner.getSource();
      }
      if (StringUtil.isBlank(userSource)) {
        timezone = "+09:00";
      } else if (userSource.contains("dd-jp-")) {
        timezone = "+09:00";
      } else if (userSource.contains("dd-en-")) {
        timezone = "+00:00";
      } else if (userSource.contains("dd-cn-")) {
        timezone = "+08:00";
      } else {
        timezone = "+09:00";
      }
      userConnectionSource.setTimezone(timezone); // 时区
    }

    if (StringUtil.isBlank(userConnectionSource.getUpdateFrequency())) {
      userConnectionSource.setUpdateFrequency(UserConnectionSource.UPDATE_FREQUENCY_HOUR); // 每小时
    }

    if (userConnectionSource.getUpdateHour() == null) {
      userConnectionSource.setUpdateHour("05:00");// 5点
    }

  }

  /**
   * 根据用户设置，生成source更新的quartz cron 表达式
   * 
   * @param userConnectionSource
   * @date: 2016年11月9日
   * @author peng.xu
   */
  private void buildSourceCronExpr(UserConnectionSource userConnectionSource) {
    String updateFrequency = userConnectionSource.getUpdateFrequency();
    String updateHour = userConnectionSource.getUpdateHour();
    String timezone = userConnectionSource.getTimezone();
  }

  /**
   * @Description: 编辑文件时，先更新文件的结构和数据.
   * @date: 2016/2/1
   * @author: zhangli
   */
  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public UserConnectionSourceVo getEditorDataByConnectionId(String connectionId, String sourceId,
      boolean innerFunction, boolean isAutoUpdate, boolean isUpdate) throws BusinessException {
    UserConnectionSourceVo sourceVo = new UserConnectionSourceVo();
    LogMessage logMessage = new LogMessage();
    try {
      LogMessageUtil.addOperateInfoOfFlag(logMessage, innerFunction, isAutoUpdate);
      List<UserConnectionSourceTableVo> tables = new ArrayList<>();
      UserConnectionSourceDto sourceDto = serviceFactory.getUserConnectionSourceService()
          .getSourceDtoIncludeTables(sourceId);

      Long uid = sourceDto.getUid();
      Long dsId = sourceDto.getDsId();
      String fileId = sourceDto.getFileId();
      sourceVo.setConnectionId(sourceDto.getConnectionId());
      sourceVo.setSourceId(sourceId);
      sourceVo.setRemotePath(sourceDto.getRemotePath());
      sourceVo.setUid(uid);
      sourceVo.setDsId(dsId);
      sourceVo.setName(sourceDto.getName());
      sourceVo.setFileId(fileId);
      sourceVo.setLastModifiedDate(sourceDto.getLastModifiedDate());
      sourceVo.setTable(tables);
      UserConnection userConnection = serviceFactory.getPtoneUserConnectionService().get(
          connectionId);
      if (userConnection == null) {
        String msg = "getEditorDataByConnectionId:::" + " UserConnection is null (connectionId="
            + connectionId + ", sourceId=" + sourceId + ", innerFunction=" + innerFunction
            + ", isAutoUpdate=" + isAutoUpdate + ")";
        logger.warn(msg);
        // 将userConnection对应的dataSource状态全部修改为已删除
        serviceFactory.getUserConnectionSourceService().updateConnectionSourceStatusByConnectionId(
            Constants.inValidate, connectionId);
        throw new ServiceException(msg);
      }
      String dsCode = userConnection.getDsCode();

      LogMessageUtil.addOperateInfoOfExcel(logMessage, connectionId, sourceId, dsCode);
      LogMessageUtil.addBasicInfo(logMessage, String.valueOf(uid), "getEditorDataByConnectionId");
      UIEditorData uiEditorData = serviceFactory.getDataSourceBuild().getEditData(userConnection,
          sourceDto, isAutoUpdate, isUpdate);
      // 文件被删除，文件没更新，文件已断开连接
      Boolean hasDeleted = uiEditorData.getHasDeleted(), hasChanged = uiEditorData.getHasChanged(), hasDisconnected = uiEditorData
          .getHasDisconnected();
      if (isAutoUpdate
          && (hasDeleted != null && hasDeleted || hasChanged != null && !hasChanged || hasDisconnected != null
              && hasDisconnected)) {
        logger.info("GD auto update : getEditorDataByConnectionId:::" + "status(hasDeleted="
            + hasDeleted + ", hasChanged=" + hasChanged + ", hasDisconnected=" + hasDisconnected
            + ")," + " info(sourceId=" + sourceId + ", uid=" + uid + ", fileId=" + fileId
            + ", name=" + sourceDto.getName() + ")");
        return null;
      }
      LinkedHashMap<String, List<List>> editorData = uiEditorData.getEditorData();
      List<UserConnectionSourceDto> sourceDtos = uiEditorData.getSourceDtos();
      if (CollectionUtil.isNotEmpty(sourceDtos)) {
        // 更新内存中的source对象，返回给前端
        for (UserConnectionSourceDto userSourceDto : sourceDtos) {
          if (userSourceDto.getSourceId().equals(sourceId)) {
            sourceDto = userSourceDto;
            break;
          }
        }
      }
      // 文件删除
      if (hasDeleted != null && hasDeleted) {
        sourceVo.setRemoteStatus(Constants.inValidate);
      } else {
        sourceVo.setRemoteStatus(Constants.validate);
      }
      Map<String, Object> rowSumMap = uiEditorData.getRowSumMap();
      serviceFactory.getDataSourceBuild().buildSourceVoBySourceDto(sourceDto, sourceVo, rowSumMap,
          editorData);
      // upload不需要在查看数据的时候再去保存一遍文件
      if (!innerFunction && !dsCode.equalsIgnoreCase(DsConstants.DS_CODE_UPLOAD)) {
        sourceVo.setOperateType(Constants.UI_OPERATE_UPDATE);
        serviceFactory.getDataSourceBuild().saveFileDataToDB(userConnection, sourceVo, sourceDto,
            isAutoUpdate);
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      LogMessageUtil.addErrorExceptionMessage(logMessage, e.getMessage());
      throw new BusinessException(BusinessErrorCode.Space.GET_SPACE_TABLES_ERROR,
          "get editor data by connectionId error.");
    } finally {
      logger.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return sourceVo;
  }

  @Override
  @Transactional
  public void deleteConnectionInfo(UserConnection userConnection) {
    Map<String, Object[]> paramMap = new HashMap<>();
    String connectionId = userConnection.getConnectionId();
    paramMap.put("connectionId", new Object[] { connectionId });
    serviceFactory.getUserConnectionSourceTableService().updateStatusByConnectionId(
        Constants.inValidate, connectionId);
    serviceFactory.getUserConnectionSourceTableColumnService().updateStatusByConnectionId(
        Constants.inValidate, connectionId);
    // serviceFactory.getPtoneUserConnectionService().delete(paramMap);
    // serviceFactory.getUserConnectionSourceService().delete(paramMap);

    UserConnection connection = new UserConnection();
    connection.setConnectionId(userConnection.getConnectionId());
    connection.setStatus(Constants.inValidate);
    serviceFactory.getPtoneUserConnectionService().update(connection);

    userConnectionSourceDao.deleteSourceByConnectionId(userConnection.getConnectionId());
  }

  @Override
  public String getDataSourceAccountSchema(String connectionId, String folderId, boolean refresh)
      throws BusinessException {
    String shcema = "";
    String key = connectionId + ":" + folderId;
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("connectionId", new Object[] { connectionId });
    paramMap.put("status", new Object[] { Constants.validate });
    UserConnection userConnection = serviceFactory.getPtoneUserConnectionService().getByWhere(
        paramMap);
    try {
      shcema = ClassReflex.invokeValue(userConnection.getDsCode(), "getAccountSchema", new Class[] {
          UserConnection.class, String.class }, userConnection, folderId);
    } catch (Exception e) {
      logger.error("get data source account schema error.", e);
      throw new BusinessException(
          BusinessErrorCode.Space.GET_SPACE_DATASOURCE_ACCOUNT_SCHEMA_ERROR,
          "get data source account schema error");
    }
    return shcema;
  }

  @Override
  public UserConnectionSourceVo pullRemoteData(String connectionId, String folderId, String fileId)
      throws BusinessException {
    UserConnection userConnection = serviceFactory.getPtoneUserConnectionService()
        .get(connectionId);
    UserConnectionSourceVo vo = new UserConnectionSourceVo();
    try {
      vo = ClassReflex.invokeValue(userConnection.getDsCode(), "pullRemoteData", new Class[] {
          UserConnection.class, String.class, String.class }, userConnection, folderId, fileId);
    } catch (Exception e) {
      logger.error("pull remote data error", e);
      throw new BusinessException(BusinessErrorCode.Space.GET_SPACE_TABLES_REMOTE_DATA_ERROR,
          "pull remote data error");
    }
    return vo;
  }

  @Override
  @Deprecated
  public List<UserAccountSource> getAuthAccount(String connectionId, String uid, String dsId) {
    return serviceFactory.getUserConnectionSourceService().getUserAccountSource(uid, dsId);
  }

  /**
   * 填充MetaContentNode列表
   * 
   * @author you.zou
   * @date 2016年11月23日 下午4:16:50
   * @param nodes
   * @param sourceDtos
   * @return
   */
  public List<MetaContentNode> fillNode(List<MetaContentNode> nodes,
      List<UserConnectionSourceDto> sourceDtos) {
    for (int i = 0; i < nodes.size(); i++) {
      MetaContentNode node = nodes.get(i);
      if (!node.getChild().isEmpty()) {
        fillNode(node.getChild(), sourceDtos);
      } else {// 叶子节点
        String nodePath = node.getId();
        List<MetaContentNode> child = new ArrayList<>();
        for (UserConnectionSourceDto sourceDto : sourceDtos) {
          if (nodePath.equals(sourceDto.getRemotePath())) {
            for (UserConnectionSourceTableDto tableDto : sourceDto.getTables()) {
              MetaContentNode configNode = new MetaContentNode();
              configNode.setName(tableDto.getName());
              configNode.setId(tableDto.getTableId());
              Map<String, Object> extraMap = new HashMap<>();
              extraMap.put("sourceId", sourceDto.getSourceId());
              configNode.setExtra(extraMap);
              child.add(configNode);
            }
          }
        }
        node.setChild(child);
      }
    }
    return nodes;
  }

  /*
   * @Override
   * 
   * @Deprecated public List<MetaContentNode> getWidgetAuthAccount(String
   * connectionId, String uid, String dsId) { String queryType =
   * serviceFactory.getPtoneDsInfoCache
   * ().getPtoneDsInfoById(Long.parseLong(dsId)) .getQueryType(); Map<String,
   * Object[]> paramMap = new HashMap<>(); paramMap.put("uid", new Object[]
   * {uid}); paramMap.put("dsId", new Object[] {dsId});
   * paramMap.put("connectionId", new Object[] {connectionId});
   * paramMap.put("status", new Object[] {Constants.validate}); Map<String,
   * String> orderMap = new HashMap<>(); orderMap.put("createTime", "desc");
   * PtoneDsInfo dsInfo =
   * serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById
   * (Long.parseLong(dsId)); List<UserConnectionSource> userConnectionSourceList
   * = serviceFactory.getUserConnectionSourceService().findByWhere(paramMap,
   * orderMap); List<MetaContentNode> resultContentNodeList = new ArrayList<>();
   * // Map<String, Object> pathMap = new HashMap<>();
   * 
   * if (dsInfo.getCode().equals(DsConstants.DS_CODE_GOOGLEDRIVE) ||
   * dsInfo.getCode().equals(DsConstants.DS_CODE_UPLOAD) ||
   * dsInfo.getCode().equals(DsConstants.DS_CODE_S3)) { List<MetaContentNode>
   * metaContentNodeList = new CopyOnWriteArrayList<>(); for
   * (UserConnectionSource source : userConnectionSourceList) {
   * 
   * List<MetaContentNode> metaContentNodeSecondList = new ArrayList<>();
   * 
   * MetaContentNode metaContentNode = new MetaContentNode();
   * metaContentNode.setId(source.getSourceId());
   * metaContentNode.setChild(metaContentNodeSecondList); if
   * (StringUtil.hasText(source.getFolderId())) {
   * metaContentNode.setName(source.getFolderId()); } else {
   * metaContentNode.setName(source.getName()); }
   * 
   * List<Map> configList = JSON.parseArray(source.getConfig(), Map.class); for
   * (Map map : configList) { MetaContentNode metaContentNodeSecond = new
   * MetaContentNode();
   * metaContentNodeSecond.setName(map.get("name").toString());
   * metaContentNodeSecond.setId(map.get("tableId").toString());
   * metaContentNodeSecondList.add(metaContentNodeSecond); }
   * metaContentNodeList.add(metaContentNode); }
   * resultContentNodeList.addAll(metaContentNodeList); } else { List<String>
   * pathList = new ArrayList<>(); for (UserConnectionSource source :
   * userConnectionSourceList) { String remotePath[] =
   * source.getRemotePath().split("@#\\*"); List<String> remoteList = new
   * ArrayList<>(); remoteList.addAll(Arrays.asList(remotePath));
   * pathList.add(source.getRemotePath());
   * 
   * List<Map> configList = JSON.parseArray(source.getConfig(), Map.class);
   * List<MetaContentNode> configNodeList = new ArrayList<>(); for (Map map :
   * configList) { MetaContentNode configNode = new MetaContentNode();
   * configNode.setName(map.get("name").toString());
   * configNode.setId(map.get("tableId").toString());
   * configNodeList.add(configNode); }
   * 
   * } MetaContentNode node = generateTreePath(pathList);
   * 
   * List<MetaContentNode> nodeList = new ArrayList<>(); nodeList.add(node);
   * resultContentNodeList .addAll(fillConfig(nodeList,
   * userConnectionSourceList).get(0).getChild()); }
   * 
   * 
   * for (UserConnectionSource source : userConnectionSourceList) {
   * List<MetaContentNode> contentNodeList = new ArrayList<>(); String
   * remotePath[] = source.getRemotePath().split("@#\\*"); List<String>
   * remoteList = new ArrayList<>();
   * remoteList.addAll(Arrays.asList(remotePath)); List<Map> configList =
   * JSON.parseArray(source.getConfig(), Map.class); List<MetaContentNode>
   * configNodeList = new ArrayList<>(); for (Map map : configList) {
   * MetaContentNode configNode = new MetaContentNode();
   * configNode.setName(map.get("name").toString());
   * configNode.setId(map.get("tableId").toString());
   * configNodeList.add(configNode); }
   * 
   * if (pathMap.containsKey(source.getRemotePath())) { contentNodeList =
   * (List<MetaContentNode>) pathMap.get(source.getRemotePath());
   * contentNodeList = addMetaContentNode(remoteList.size() - 1,
   * 1,configNodeList, contentNodeList);
   * 
   * } else { contentNodeList = createMetaContentNode(configNodeList,
   * remoteList.subList(1, remoteList.size()));
   * resultContentNodeList.addAll(contentNodeList); }
   * pathMap.put(source.getRemotePath(), contentNodeList);
   * 
   * }
   * 
   * 
   * // 分组table 按相同数据库名
   * 
   * if (!metaContentNodeList.isEmpty() &&
   * dsInfo.getCategory().equalsIgnoreCase("rdatabase")) { Map<String, Object>
   * map = new HashMap<>(); for (int i = 0; i < metaContentNodeList.size(); i++)
   * { if (!map.containsKey(metaContentNodeList.get(i).getName())) {
   * resultContentNodeList.add(metaContentNodeList.get(i)); for (int j = i + 1;
   * j < metaContentNodeList.size(); j++) { if
   * (!metaContentNodeList.get(i).getId
   * ().equals(metaContentNodeList.get(j).getId()) &&
   * metaContentNodeList.get(i).
   * getName().equals(metaContentNodeList.get(j).getName())) {
   * metaContentNodeList
   * .get(i).getChild().addAll(metaContentNodeList.get(j).getChild()); } }
   * map.put(metaContentNodeList.get(i).getName(), metaContentNodeList.get(i));
   * } } } else { resultContentNodeList.addAll(metaContentNodeList); }
   * 
   * return resultContentNodeList; }
   */
  @Override
  public List<MetaContentNode> getSpaceWidgetAuthAccount(String spaceId, String connectionId,
      String dsId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("spaceId", new Object[] { spaceId });
    paramMap.put("dsId", new Object[] { dsId });
    if (connectionId != null && !connectionId.equalsIgnoreCase("all")) {
      paramMap.put("connectionId", new Object[] { connectionId });
    }
    paramMap.put("status", new Object[] { Constants.validate });
    Map<String, String> orderMap = new HashMap<>();
    orderMap.put("createTime", "desc");
    PtoneDsInfo dsInfo = serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById(
        Long.parseLong(dsId));
    List<UserConnectionSourceDto> sourceDtos = serviceFactory.getUserConnectionSourceService()
        .findSourceDtoByWhereIncludeTablesOutcludeColumns(paramMap, orderMap);
    List<MetaContentNode> resultContentNodeList = new ArrayList<>();
    if (CollectionUtil.isEmpty(sourceDtos)) {
      return resultContentNodeList;
    }
    if (dsInfo.getCode().equals(DsConstants.DS_CODE_GOOGLEDRIVE)
        || dsInfo.getCode().equals(DsConstants.DS_CODE_UPLOAD)
        || dsInfo.getCode().equals(DsConstants.DS_CODE_S3)) {
      List<MetaContentNode> metaContentNodeList = new CopyOnWriteArrayList<>();
      for (UserConnectionSourceDto sourceDto : sourceDtos) {
        List<MetaContentNode> metaContentNodeSecondList = new ArrayList<>();
        MetaContentNode metaContentNode = new MetaContentNode();
        metaContentNode.setId(sourceDto.getSourceId());
        metaContentNode.setChild(metaContentNodeSecondList);
        if (StringUtil.hasText(sourceDto.getFolderId())) {
          metaContentNode.setName(sourceDto.getFolderId());
        } else {
          metaContentNode.setName(sourceDto.getName());
        }
        for (UserConnectionSourceTableDto tableDto : sourceDto.getTables()) {
          Map<String, Object> extra = new HashMap<String, Object>();
          extra.put("connectionId", sourceDto.getConnectionId());
          extra.put("sourceId", sourceDto.getSourceId());
          MetaContentNode metaContentNodeSecond = new MetaContentNode();
          metaContentNodeSecond.setName(tableDto.getName());
          metaContentNodeSecond.setId(tableDto.getTableId());
          metaContentNodeSecond.setExtra(extra);
          metaContentNodeSecond.setLeaf(true);
          metaContentNodeSecondList.add(metaContentNodeSecond);
        }
        metaContentNodeList.add(metaContentNode);
      }
      resultContentNodeList.addAll(metaContentNodeList);
    } else {
      List<String> pathList = new ArrayList<>();
      for (UserConnectionSourceDto sourceDto : sourceDtos) {
        pathList.add(sourceDto.getRemotePath());
      }
      MetaContentNode node = generateTreePath(pathList);
      List<MetaContentNode> nodeList = new ArrayList<>();
      nodeList.add(node);
      resultContentNodeList.addAll(fillNode(nodeList, sourceDtos).get(0).getChild());
    }
    return resultContentNodeList;
  }

  /**
   * 将pathList转换成MetaContentNode对象<br>
   * pathList == null || pathList.size == null --> return rootMetaContentNode
   * have not childs<br>
   * pathList != null && pathList.size > 0 && path not contains '@#*' --> return
   * rootMetaContentNode have not childs<br>
   * pathList != null && pathList.size > 0 && path contains '@#*' --> return
   * rootMetaContentNode have childs
   * 
   * @author peng.xu
   * @date 2016年11月28日 下午5:41:21
   * @param pathList
   * @return
   */
  public MetaContentNode generateTreePath(List<String> pathList) {
    MetaContentNode rootMetaContentNode = new MetaContentNode();
    rootMetaContentNode.setName("root");
    if (CollectionUtil.isEmpty(pathList)) {
      return rootMetaContentNode;
    }

    for (String pathStr : pathList) {
      MetaContentNode parentNode = rootMetaContentNode;
      String[] pathArray = pathStr.split("@#\\*");
      for (int i = 1; i < pathArray.length; i++) {
        String path = pathArray[i];
        List<MetaContentNode> childList = parentNode.getChild();
        MetaContentNode childNode = createMetaContentNode(parentNode, path);

        if (childList.size() == 0) {
          parentNode.getChild().add(childNode);
        } else {
          // 检查当前的path是否已存在于childs中
          // 存在：不加入到childs中
          // 不存在：加入到childs中
          List<MetaContentNode> newChildList = new ArrayList<MetaContentNode>();
          newChildList.addAll(childList);
          boolean childExsit = false;
          for (MetaContentNode childMetaContentNode : childList) {
            if (childMetaContentNode.getName().equals(path)) {
              childExsit = true;
              childNode = childMetaContentNode;
              break;
            }
          }
          if (!childExsit) {
            newChildList.add(childNode);
          }

          parentNode.setChild(newChildList);
        }

        parentNode = childNode;
      }

      parentNode.setId(pathStr);

    }
    return rootMetaContentNode;
  }

  public static MetaContentNode createMetaContentNode(MetaContentNode parentNode, String nodeName) {
    MetaContentNode childNode = new MetaContentNode();
    childNode.setName(nodeName);
    // childNode.setParent(parentNode);
    return childNode;
  }

  @Override
  public Long getAccountWidgetCount(String connectionId, String uid, String dsId) {
    return serviceFactory.getGaWidgetService().queryWidgetCountOfAccount(connectionId);
  }

  @Override
  public Long getSourceWidgetCount(String sourceId, String uid) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("sourceId", new Object[] { sourceId });
    // paramMap.put("status", new Object[]{Constants.validate});
    /*
     * UserConnectionSource source =
     * serviceFactory.getUserConnectionSourceService().getByWhere(paramMap);
     */
    List<String> tableList = new ArrayList<String>();
    UserConnectionSourceDto sourceDto = serviceFactory.getUserConnectionSourceService()
        .getSourceDtoIncludeTablesOutcludeColumns(sourceId);
    for (UserConnectionSourceTableDto tableDto : sourceDto.getTables()) {
      tableList.add(tableDto.getTableId());
    }
    return serviceFactory.getGaWidgetService().queryWidgetCountOfSource(tableList);
  }

  @Override
  public List<PtoneMetricsDimension> getUserMetricsDimensionList(long dsId, String tableId,
      String[] typeArray) {
    List<PtoneMetricsDimension> result = new ArrayList<PtoneMetricsDimension>();
    List<UserConnectionSourceTableColumn> list = serviceFactory
        .getUserConnectionSourceTableColumnService().findByTableId(tableId, typeArray);
    if (list != null) {
      for (UserConnectionSourceTableColumn md : list) {
        PtoneMetricsDimension item = new PtoneMetricsDimension();
        BeanUtils.copyProperties(md, item);
        item.setId(md.getColId());
        result.add(item);
      }
    }
    return result;
  }

  @Override
  @Deprecated
  public List<DsContentView> getUserDsContentView(String uid) {
    List<DsContentView> dsList = serviceFactory.getPtoneUserConnectionService()
        .getUserDsContentView(uid);
    return dsList;
  }

  @Override
  @Transactional
  public void delSavedFile(String sourceId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("sourceId", new Object[] { sourceId });
    // serviceFactory.getUserConnectionSourceService().delete(paramMap);
    UserConnectionSource source = new UserConnectionSource();
    source.setSourceId(sourceId);
    source.setStatus(Constants.inValidate);
    serviceFactory.getUserConnectionSourceService().update(source);
    serviceFactory.getUserConnectionSourceTableService().updateStatusBySourceId(
        Constants.inValidate, sourceId);
    serviceFactory.getUserConnectionSourceTableColumnService().updateStatusBySourceId(
        Constants.inValidate, sourceId);
  }

  @Override
  public UpdateDataResponse refreshFileFromRemote(UserConnectionSourceVo sourceVo) throws Exception {
    boolean updateFlag = sourceVo.getOperateType().equalsIgnoreCase("update");
    UpdateDataResponse updateDataResponse = null;
    if (updateFlag) {
      UserConnection userConnection = serviceFactory.getPtoneUserConnectionService().get(
          sourceVo.getConnectionId());
      updateDataResponse = ClassReflex.invokeValue(userConnection.getDsCode(), "saveFileToHDFS",
          new Class[] { UserConnection.class, UserConnectionSourceVo.class, Map.class, Map.class },
          userConnection, sourceVo, new HashMap<String, Integer[]>(),
          new HashMap<String, Integer[]>());
    }
    return updateDataResponse;
  }

  @Override
  public UserConnectionSource getUserConnectionSourceByTableId(String tableId) {
    UserConnectionSource source = null;
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("tableId", new Object[] { tableId });
    paramMap.put("status", new Object[] { Constants.validate });
    UserConnectionSourceTable table = serviceFactory.getUserConnectionSourceTableService()
        .getByWhere(paramMap);
    if (table != null) {
      source = serviceFactory.getUserConnectionSourceService().getSource(table.getSourceId());
    }
    return source;
  }

  @Override
  public UserConnectionSourceDto getSourceDtoByTableIdIncludeTable(String tableId) {
    UserConnectionSourceDto sourceDto = null;
    UserConnectionSourceTableDto tableDto = serviceFactory.getUserConnectionSourceTableService()
        .getByTableIdOfDto(tableId);
    if (tableDto != null) {
      sourceDto = serviceFactory.getUserConnectionSourceService().getSourceDto(
          tableDto.getSourceId());
      List<UserConnectionSourceTableDto> tableDtos = new ArrayList<UserConnectionSourceTableDto>();
      tableDtos.add(tableDto);
      sourceDto.setTables(tableDtos);
    }
    return sourceDto;
  }

  /**
   * 原方法名：fillMutableSchema<br>
   * 用于将source转换为MutableSchema对象
   * 
   * @author you.zou
   * @date 2016年11月18日 下午2:52:06
   * @param source
   * @param columnList
   * @return
   */
  @Override
  public MutableSchema sourceDtoToSchema(UserConnectionSourceDto source) {
    if (source == null) {
      return null;
    }
    MutableSchema mutableSchema = new MutableSchema();
    // Source对象转换
    mutableSchema.setName(source.getName());
    mutableSchema.setId(source.getSourceId());

    List<UserConnectionSourceTableDto> tables = source.getTables();
    if (CollectionUtil.isEmpty(tables)) {
      return mutableSchema;
    }

    for (UserConnectionSourceTableDto table : tables) {
      if (table == null) {
        continue;
      }
      // 表对象转换
      MutableTable mutableTable = new MutableTable();
      mutableTable.setId(table.getTableId());
      mutableTable.setName(table.getName());
      mutableTable.setType(TableType.getTableType(table.getType()));
      mutableTable.setRowCount(Long.valueOf(table.getRowSum()));
      List<UserConnectionSourceTableColumnDto> columns = table.getColumns();
      if (CollectionUtil.isEmpty(columns)) {
        continue;
      }

      for (UserConnectionSourceTableColumnDto column : columns) {
        if (column == null) {
          continue;
        }
        // 列对象转换
        MutableColumn mutableColumn = new MutableColumn();
        mutableColumn.setName(column.getName());
        mutableColumn.setId(column.getColId());
        mutableColumn.setType(ColumnTypeImpl.valueOf(column.getDataType()));
        mutableColumn.setColumnNumber(column.getColIndex().intValue());
        mutableTable.addColumn(mutableColumn);
      }
      mutableSchema.addTable(mutableTable);
    }
    return mutableSchema;
  }

  @Override
  public MutableSchema getMutableSchemaByTableId(String tableId) {
    UserConnectionSourceTableDto tableDto = serviceFactory.getUserConnectionSourceTableService()
        .getByTableIdOfDtoIncludeClumns(tableId);
    String sourceId = tableDto.getSourceId();
    UserConnectionSourceDto sourceDto = serviceFactory.getUserConnectionSourceService()
        .getSourceDto(sourceId);
    MutableSchema mutableSchema = new MutableSchema();
    mutableSchema.setId(sourceId);
    mutableSchema.setName(sourceDto.getName());
    MutableTable table = new MutableTable();
    table.setId(tableId);
    table.setName(tableDto.getName());
    table.setType(TableType.getTableType(tableDto.getType()));
    table.setRowCount(Long.valueOf(tableDto.getRowSum()));
    List<UserConnectionSourceTableColumnDto> columnDtos = tableDto.getColumns();
    for (UserConnectionSourceTableColumnDto columnDto : columnDtos) {
      MutableColumn column = new MutableColumn();
      column.setId(columnDto.getColId());
      column.setName(columnDto.getName());
      column.setType(ColumnTypeImpl.valueOf(columnDto.getDataType()));
      column.setColumnNumber(columnDto.getColIndex().intValue());
      table.addColumn(column);
    }
    mutableSchema.addTable(table);
    return mutableSchema;
  }

  /*
   * @Override
   * 
   * @SuppressWarnings("rawtypes") public MutableSchema
   * getMutableSchemaByTableId(String tableId) { List<UserConnectionTableSchema>
   * schemaList =
   * serviceFactory.getUserConnectionTableSchemaService().getTableSchemaByTableId
   * (tableId); UserConnectionSource source = null;
   * 
   * MutableSchema mutableSchema = new MutableSchema(); MutableTable table = new
   * MutableTable(); if (schemaList != null) { for (UserConnectionTableSchema
   * schema : schemaList) { if (source == null) { source =
   * serviceFactory.getUserConnectionSourceService
   * ().getSource(schema.getSourceId()); if (source != null &&
   * StringUtil.isNotBlank(source.getConfig())) {
   * mutableSchema.setId(source.getSourceId());
   * mutableSchema.setName(source.getName()); List<Map> configList =
   * JSON.parseArray(source.getConfig(), Map.class); for (Map map : configList)
   * { if (tableId.equals(map.get("tableId").toString())) {
   * table.setId(tableId); table.setName(map.get("name").toString());
   * table.setType(TableType.getTableType(map.get("type").toString()));
   * table.setRowCount(Long.valueOf(map.get("rowSum").toString())); } } } }
   * MutableColumn column = new MutableColumn();
   * column.setId(schema.getColId()); column.setName(schema.getName());
   * column.setType(ColumnTypeImpl.valueOf(schema.getDataType()));
   * column.setColumnNumber(schema.getColIndex().intValue());
   * table.addColumn(column); } } mutableSchema.addTable(table);
   * 
   * return mutableSchema; }
   */

  @Override
  public void testRemoteConnection(UserConnection userConnection) throws ServiceException {
    MetaRequest metaRequest = new DefaultMetaRequest(userConnection);
    serviceFactory.getMetaProvider().getMeta(metaRequest);
  }

  @Override
  public void updateConnectionSourcePath(String path, String connectionId) {
    serviceFactory.getUserConnectionSourceService().updateConnectionSourcePath(path, connectionId);
  }

  @Override
  public List<UserConnectionSource> getConnectionSourceList(String connectionId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("connectionId", new Object[] { connectionId });
    paramMap.put("status", new Object[] { Constants.validate });
    return serviceFactory.getUserConnectionSourceService().findByWhere(paramMap);
  }

  @Override
  public List<DsContentView> getSpaceDsContentView(String spaceId, String userSource) {
    return serviceFactory.getPtoneUserConnectionService()
        .getSpaceDsContentView(spaceId, userSource);
  }

  /**
   * 获取当前空间下当前dsId下的所有保存到ptone的文件列表.
   */
  @Override
  public List<UserAccountSource> getSpaceAuthAccount(String spaceId, String connectionId,
      String dsId) {
    return serviceFactory.getUserConnectionSourceService().getSpaceAccountSource(spaceId, dsId);
  }

  @Override
  @Transactional
  public UserConnectionSource updateConnectionSource(UserConnectionSource connectionSource) {
    serviceFactory.getUserConnectionSourceService().update(connectionSource);
    String sourceId = connectionSource.getSourceId();
    UserConnectionSource dbConnectionSource = serviceFactory.getDataSourceManagerService()
        .getConnectionSourceById(sourceId);

    // upload数据源更新文件名称的时候更新userConnection中的fileName
    if (dbConnectionSource.getDsId() == DsConstants.DS_ID_UPLOAD) {
      String connectionId = dbConnectionSource.getConnectionId();
      UserConnection userConnection = serviceFactory.getPtoneUserConnectionService().get(
          connectionId);
      Map<String, Object> config = (Map<String, Object>) JSON.parse(userConnection.getConfig());
      config.put(UserConnectionConfig.UploadConfig.FILE_NAME, dbConnectionSource.getName());
      userConnection.setConfig(JSON.toJSONString(config));
      serviceFactory.getPtoneUserConnectionService().update(userConnection);

      // 如果是Upload，并且文件类型是CSV，那么需要修改Table的Name字段
      String type = String.valueOf(config.get(UserConnectionConfig.UploadConfig.TYPE));
      if (StringUtil.isNotBlank(type) && type.equalsIgnoreCase("CSV")) {
        // 修改table的Name字段
        List<UserConnectionSourceTable> tables = serviceFactory
            .getUserConnectionSourceTableService().findBySourceId(sourceId);
        if (CollectionUtil.isEmpty(tables) || tables.size() > 1) {
          // table不存在，或者table大于两个，则记录错误日志
          logger.error("table size is 0 or more than the 1 in sourceId(" + sourceId + ")");
        } else {
          UserConnectionSourceTable table = tables.get(0);
          table.setName(connectionSource.getName());
          serviceFactory.getUserConnectionSourceTableService().update(table);
        }
      }

    }

    // 更新文件定时更新调度任务
    if (dbConnectionSource.getDsId() == DsConstants.DS_ID_GOOGLEDRIVE) {
      // 根据用户设置，生成quartz cron表达式
      this.buildSourceCronExpr(dbConnectionSource);
      userConnectionSourceDao.update(dbConnectionSource);
    }

    return dbConnectionSource;
  }

  @Override
  public UserConnectionSource getConnectionSourceById(String sourceId) {
    return serviceFactory.getUserConnectionSourceService().getSource(sourceId);
  }

  @Override
  public UserConnectionSourceVo updateRemoteSourceData(UserConnectionSource source)
      throws BusinessException {
    if (source == null) {
      return null;
    }

    UserConnectionSourceVo sourceVo = new UserConnectionSourceVo();
    sourceVo.setConnectionId(source.getConnectionId());
    sourceVo.setCreateTime(source.getCreateTime());
    sourceVo.setDsId(source.getDsId());
    sourceVo.setFileId(source.getFileId());
    sourceVo.setLastModifiedDate(source.getLastModifiedDate());
    sourceVo.setName(source.getName());
    sourceVo.setOperateType(Constants.UI_OPERATE_UPDATE);
    sourceVo.setRemotePath(source.getRemotePath());
    sourceVo.setRemotePath(source.getRemoteStatus());
    sourceVo.setSourceId(source.getSourceId());
    sourceVo.setUid(source.getUid());
    sourceVo.setUpdateTime(source.getUpdateTime());
    return this.saveOrUpdateEditorDataToFile(sourceVo);
  }

  @Override
  public JsonView addAccountConnection(UiAccountConnection uiAccountConnection, String sign) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      String email = uiAccountConnection.getName();
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("name", new Object[] { email });
      paramMap.put("dsCode", new Object[] { uiAccountConnection.getDsCode() });
      paramMap.put("uid", new Object[] { uiAccountConnection.getUid() });
      paramMap.put("spaceId", new Object[] { uiAccountConnection.getSpaceId() });
      UserConnection userConnection = serviceFactory.getPtoneUserConnectionService().getByWhere(
          paramMap);

      // 保存connection, 如果不存在则新增，如果存在则执行update
      if (userConnection == null) {
        userConnection = new UserConnection();
        BeanUtils.copyProperties(uiAccountConnection, userConnection);
        userConnection.setConnectionId(UUID.randomUUID().toString());
        userConnection.setStatus(Constants.validate);
        userConnection.setConfig(JSON.toJSONString(uiAccountConnection.getConfigObject()));
        userConnection.setUpdateTime(System.currentTimeMillis());
        serviceFactory.getPtoneUserConnectionService().save(userConnection);
      } else {
        userConnection.setStatus(Constants.validate);
        userConnection.setConfig(JSON.toJSONString(uiAccountConnection.getConfigObject()));
        userConnection.setUserName(uiAccountConnection.getUserName());
        userConnection.setUpdateTime(System.currentTimeMillis());
        serviceFactory.getPtoneUserConnectionService().update(userConnection);
      }

      Map<String, Object> resultMap = new HashMap<>();
      resultMap.put("email", email);
      resultMap.put("account", email);
      resultMap.put("connectionId", userConnection.getConnectionId());
      resultMap.put("connectionInfo", userConnection);
      jsonView.successPack(resultMap);

//      // 发送socket请求
//      serviceFactory.getDynamicDubboConsumer().tellMessage(sign, jsonView);
      logger.info("add account auth connection success: " + JSON.toJSONString(userConnection));
    } catch (ServiceException e) {
      jsonView.failedPack(e.getErrorCode() + " | " + e.getErrorMsg());
      logger.info(
          "add account auth connection error: " + e.getErrorCode() + " | " + e.getErrorMsg()
              + JSON.toJSONString(uiAccountConnection), e);
    } catch (Exception e) {
      jsonView.errorPack("add account auth connection error.", e);
      logger
          .info("add account auth connection error: " + JSON.toJSONString(uiAccountConnection), e);
    }

    return jsonView;
  }

}
