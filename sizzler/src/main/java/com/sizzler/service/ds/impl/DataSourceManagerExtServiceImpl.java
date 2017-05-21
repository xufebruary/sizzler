package com.sizzler.service.ds.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sizzler.dao.ds.UserConnectionSourceDao;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.service.DataSourceManagerExtService;
import com.sizzler.system.ServiceFactory;


/**
 * DataSourceManagerExtService实现类
 */
@Service("dataSourceManagerExtService")
public class DataSourceManagerExtServiceImpl implements DataSourceManagerExtService {

  private Logger logger = LoggerFactory.getLogger(DataSourceManagerExtServiceImpl.class);

  @Autowired
  private ServiceFactory serviceFactory;

  @Autowired
  private UserConnectionSourceDao userConnectionSourceDao;

  @Override
  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  @Deprecated
  public Map<String, Object> beforeSaveOrUpdateEditorDataToFile(UserConnectionSourceVo sourceVo)
      throws Exception {
    Map<String, Object> returnObjectMap = new HashMap<String, Object>();
    /*String dsCode =
        serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById(acceptTable.getDsId()).getCode();
    String operateType = acceptTable.getOperateType();
    boolean editSaveFlag =
        acceptTable.getOperateType().equalsIgnoreCase(Constants.UI_OPERATE_EDIT_SAVE);
    boolean saveFlag = acceptTable.getOperateType().equalsIgnoreCase(Constants.UI_OPERATE_SAVE);
    boolean updateFlag = acceptTable.getOperateType().equalsIgnoreCase(Constants.UI_OPERATE_UPDATE);
    // boolean autoUpdateFlag =
    // acceptTable.getOperateType().equalsIgnoreCase(Constants.UI_OPERATE_AUTO_UPDATE);
    long currentTime = System.currentTimeMillis();
    // refresh --在执行update时，默认的会执行一次取得editData的操作
    if (updateFlag) {
      acceptTable =
          serviceFactory.getDataSourceManagerService().
          getEditorDataByConnectionId(acceptTable.getConnectionId(), acceptTable.getSourceId(),
              true,false);
      acceptTable.setOperateType(operateType);
    }
    UserConnection userConnection = new UserConnection();
    userConnection.setConnectionId(acceptTable.getConnectionId());
    userConnection.setStatus(Constants.validate);
    if (saveFlag) {
      // update status to 1
      serviceFactory.getPtoneUserConnectionService().update(userConnection);
    }
    userConnection =
        serviceFactory.getPtoneUserConnectionService().get(acceptTable.getConnectionId());
    UserConnectionSource userConnectionSource = new UserConnectionSource();
    BeanUtils.copyProperties(acceptTable, userConnectionSource);
    userConnectionSource.setRemotePath(acceptTable.getRemotePath());
    userConnectionSource.setSourceId(acceptTable.getSourceId());
    userConnectionSource.setDsCode(dsCode);
    userConnectionSource.setUpdateTime(currentTime);
    userConnectionSource.setLastModifiedDate(acceptTable.getLastModifiedDate());
    userConnectionSource.setSpaceId(userConnection.getSpaceId());
    Map<String, Object> schemaMap =
        serviceFactory.getDataSourceBuild().createTableSchema(acceptTable, dsCode);
    serviceFactory.getDataSourceBuild().saveTableSchema(schemaMap, editSaveFlag, saveFlag);
    List<TreeMap> sourceConfigList = (List<TreeMap>) schemaMap.get("configList");
    // set connection source config
    userConnectionSource.setConfig(JSON.toJSONString(sourceConfigList));
    // save connection source config
    if (editSaveFlag) {
      serviceFactory.getUserConnectionSourceService().update(userConnectionSource);
    } else if (saveFlag) {
      // 设置创建时间
      userConnectionSource.setCreateTime(currentTime);
      acceptTable.setCreateTime(currentTime);
      serviceFactory.getUserConnectionSourceService().save(userConnectionSource);
    } else {
      serviceFactory.getUserConnectionSourceService().updateUpdateTime(acceptTable.getSourceId(),
          currentTime);
      long createTime =
          serviceFactory.getUserConnectionSourceService().getSource(acceptTable.getSourceId())
              .getCreateTime();
      acceptTable.setCreateTime(createTime);
    }
    acceptTable.setUpdateTime(currentTime);

    returnObjectMap.put(Constants.ACCEPT_TABLE_KEY, acceptTable);
    returnObjectMap.put(Constants.USER_CONNECTION_KEY, userConnection);
    returnObjectMap.put(Constants.USER_CONNECTION_SOURCE_KEY, userConnectionSource);
    returnObjectMap.put(Constants.SCHEMA_MAP_KEY, schemaMap);*/
    return returnObjectMap;
  }

}
