package com.sizzler.service.ds.impl;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.SourceType;
import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.dao.ds.PtoneUserConnectionDao;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.DsContentView;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableDto;
import com.sizzler.domain.ds.vo.ConnectionTimezoneVo;
import com.sizzler.domain.ds.vo.TimezoneVo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.service.PtoneUserConnectionService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("ptoneUserConnectionService")
public class PtoneUserConnectionServiceImpl extends ServiceBaseInterfaceImpl<UserConnection, Long>
    implements PtoneUserConnectionService {

  @Autowired
  private PtoneUserConnectionDao ptoneUserConnectionDao;

  @Autowired
  private ServiceFactory serviceFactory;

  @Override
  public void updateConfig(String name, String dsCode, String config, String noConnectionId) {
    ptoneUserConnectionDao.updateConfig(name, dsCode, config, noConnectionId);
  }

  @Override
  @Transactional
  public void saveAndUpdate(UserConnection userConnection) {
    ptoneUserConnectionDao.insertConnections(userConnection);
  }

  @Override
  @Transactional(propagation = Propagation.SUPPORTS)
  public void saveAndUpdateOthersConfig(UserConnection userConnection) {
    // ptoneUserConnectionDao.insertConnections(userConnection);
    UserConnection dbConnection = ptoneUserConnectionDao.get(userConnection.getName(),
        userConnection.getUid(), userConnection.getDsId(), userConnection.getSpaceId());
    if (null == dbConnection) {
      userConnection.setSourceType(SourceType.UserConnection.USER_CREATED);
      ptoneUserConnectionDao.save(userConnection);
    }
    ptoneUserConnectionDao.updateConfig(userConnection.getName(), userConnection.getDsCode(),
        userConnection.getConfig(), userConnection.getConnectionId());
  }

  /**
   * 获取当前用户下授权的数据源Connection.
   */
  @Override
  public UserConnection getPtoneUserConnection(String uid, long dsId, String name) {
    Map<String, Object[]> param = new HashMap<>();
    param.put("uid", new Object[] { uid });
    param.put("dsId", new Object[] { dsId });
    param.put("name", new Object[] { name });
    param.put("status", new Object[] { Constants.validate });
    return ptoneUserConnectionDao.getByWhere(param);
  }

  /**
   * 获取当前用户下授权的某个数据源的ConnectionList.
   */
  @Override
  public List<UserConnection> findPtoneUserConnectionList(String uid, long dsId) {
    Map<String, Object[]> param = new HashMap<>();
    param.put("uid", new Object[] { uid });
    param.put("dsId", new Object[] { dsId });
    return ptoneUserConnectionDao.findByWhere(param);
  }

  @Override
  public List<DsContentView> getUserDsContentView(String uid) {
    return ptoneUserConnectionDao.getUserDsContentView(uid);
  }

  @Override
  public UserConnection get(String connectionId) {
    return ptoneUserConnectionDao.get(connectionId);
  }

  @Override
  public UserConnection getSpaceUserConnection(String spaceId, long dsId, String connectionId,
      String name, boolean useConnectionId) {
    Map<String, Object[]> param = new HashMap<>();
    //param.put("spaceId", new Object[] { spaceId });
    param.put("dsId", new Object[] { dsId });
    useConnectionId = true;
    if (useConnectionId) {
      // 同一空间下，不同用户连接相同文件导致获去connection错误问题（导致解绑后重新授权相同账号，无法修复历史widget，需要重新关联数据源）,如gd、mysql
      // isApiDs(dsCode) = true, 则根据name来判断，跳过connectionId ， 如 ga、adwords
      param.put("connectionId", new Object[] { connectionId });
    } else {
      param.put("name", new Object[] { name });
    }
    param.put("status", new Object[] { Constants.validate });
    return ptoneUserConnectionDao.getByWhere(param);
  }

  @Override
  public List<UserConnection> findSpaceUserConnectionList(String spaceId, long dsId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("dsId", new Object[] { dsId });
    paramMap.put("spaceId", new Object[] { spaceId });
    paramMap.put("status", new Object[] { Constants.validate });

    Map<String, String> orderMap = new HashMap<>();
    orderMap.put("updateTime", "desc");
    List<UserConnection> connectionList = ptoneUserConnectionDao.findByWhere(paramMap, orderMap);

    // 设置userName信息
    Set<String> uidList = new HashSet<String>();
    for (UserConnection conn : connectionList) {
      uidList.add(conn.getUid());
    }
    if (uidList.size() > 0) {
      paramMap = new HashMap<>();
      paramMap.put("ptId", uidList.toArray());
      paramMap.put("status", new Object[] { Constants.validate });
      List<PtoneUser> userList = serviceFactory.getUserService().findByWhere(paramMap);
      Map<String, PtoneUser> userMap = new HashMap<String, PtoneUser>();
      if (userList != null && userList.size() > 0) {
        for (PtoneUser user : userList) {
          userMap.put(user.getPtId(), user);
        }
      }
      for (UserConnection conn : connectionList) {
        PtoneUser user = userMap.get(conn.getUid());
        if (user != null) {
          conn.setUserName(user.getUserName());
        }
      }
    }

    return connectionList;
  }

  @Override
  public List<DsContentView> getSpaceDsContentView(String spaceId, String userSource) {
    List<DsContentView> dsContentViewList = ptoneUserConnectionDao.getSpaceDsContentView(spaceId,
        userSource);
    // 修正upload数据源的文件统计数
    int listSize = dsContentViewList.size();
    for (int i = 0; i < listSize; i++) {
      DsContentView view = dsContentViewList.get(i);
      if (DsConstants.DS_CODE_UPLOAD.equals(view.getDsCode())
          && (view.getNameNum() == null || view.getNameNum() == 0)) {
        dsContentViewList.remove(i);
        break;
      }
    }
    return dsContentViewList;
  }

  @Override
  public UserConnection get(String name, String uid, long dsId, String spaceId) {
    return ptoneUserConnectionDao.get(name, uid, dsId, spaceId);
  }

  @Override
  public ConnectionTimezoneVo getConnectionTimezone(long dsId, String connectionId, String sourceId) {
    ConnectionTimezoneVo vo = new ConnectionTimezoneVo();
    PtoneDsInfo dsInfo = serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById(dsId);
    vo.setSupportTimezone(dsInfo.getSupportTimezone());
    if (DsConstants.isApiDs(dsInfo.getCode())) {
      UserConnection connection = serviceFactory.getPtoneUserConnectionService().get(connectionId);
      String dataTimezone = connection.getDataTimezone();
      if (StringUtil.isNotBlank(dataTimezone)) {
        vo.setDataTimezone(JSON.parseObject(dataTimezone, TimezoneVo.class));
      }
      vo.setIsDefaultTimezone(connection.getIsDefaultTimezone());
      vo.setHasTimezoneFiled(dsInfo.getSupportTimezone());
    } else {
      UserConnectionSource source = serviceFactory.getUserConnectionSourceService().getSource(
          sourceId);
      String dataTimezone = source.getDataTimezone();
      if (StringUtil.isNotBlank(dataTimezone)) {
        vo.setDataTimezone(JSON.parseObject(dataTimezone, TimezoneVo.class));
      }
      // 对于非api类型的数据源需要查询每个字段是否存在timestamp类型
      List<UserConnectionSourceTableColumn> columns = serviceFactory
          .getUserConnectionSourceTableColumnService().findBySourceId(sourceId);
      String hasTimezoneFiled = Constants.inValidate;
      if (dsInfo.getSupportTimezone().equals(Constants.validate)
          && CollectionUtil.isNotEmpty(columns)) {
        for (UserConnectionSourceTableColumn column : columns) {
          if (StringUtil.isNotBlank(column.getDataType())
              && column.getDataType().equals("TIMESTAMP")) {
            hasTimezoneFiled = Constants.validate;
            break;
          }
        }
      }
      vo.setHasTimezoneFiled(hasTimezoneFiled);
      vo.setIsDefaultTimezone(source.getIsDefaultTimezone());
    }
    return vo;
  }

  @Override
  public void updateConnectionTimezone(long dsId, String connectionId, String sourceId,
      ConnectionTimezoneVo vo) {
    PtoneDsInfo dsInfo = serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById(dsId);
    if (DsConstants.isApiDs(dsInfo.getCode())) {
      UserConnection connection = new UserConnection();
      connection.setConnectionId(connectionId);
      connection.setIsDefaultTimezone(vo.getIsDefaultTimezone());
      connection.setDataTimezone(JSON.toJSONString(vo.getDataTimezone()));
      serviceFactory.getPtoneUserConnectionService().update(connection);
    } else {
      UserConnectionSource source = new UserConnectionSource();
      source.setSourceId(sourceId);
      source.setIsDefaultTimezone(vo.getIsDefaultTimezone());
      source.setDataTimezone(JSON.toJSONString(vo.getDataTimezone()));
      serviceFactory.getUserConnectionSourceService().update(source);
    }
  }

  @Override
  public String getSourceIdByTableId(String tableId) {
    UserConnectionSourceTableDto tableDto = serviceFactory.getUserConnectionSourceTableService()
        .getByTableIdOfDto(tableId);
    if (tableDto == null) {
      return null;
    }
    return tableDto.getSourceId();
  }
}
