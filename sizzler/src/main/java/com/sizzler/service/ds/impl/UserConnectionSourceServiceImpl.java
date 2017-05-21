package com.sizzler.service.ds.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.dao.ds.UserConnectionSourceDao;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.dto.UserAccountSource;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.service.ds.UserConnectionSourceService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

@Repository
public class UserConnectionSourceServiceImpl extends
    ServiceBaseInterfaceImpl<UserConnectionSource, String> implements UserConnectionSourceService {

  @Autowired
  private UserConnectionSourceDao userConnectionSourceDao;

  @Autowired
  private ServiceFactory serviceFactory;

  @Override
  public UserConnectionSource getSource(String sourceId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("sourceId", new Object[] {sourceId});
    paramMap.put("status", new Object[] {Constants.validate});
    return userConnectionSourceDao.getByWhere(paramMap);
  }

  @Override
  public UserConnectionSourceDto getSourceDtoIncludeTables(String sourceId) {
    UserConnectionSourceDto sourceDto = getSourceDto(sourceId);
    // 查询source下的table列表
    sourceDto.setTables(serviceFactory.getUserConnectionSourceTableService()
        .findBySourceIdOfDtoIncludeColumns(sourceId));
    return sourceDto;
  }
  @Override
  public UserConnectionSourceDto getSourceDtoIncludeTablesOutcludeColumns(String sourceId){
    UserConnectionSourceDto sourceDto = getSourceDto(sourceId);
    sourceDto.setTables(serviceFactory.getUserConnectionSourceTableService()
        .findBySourceIdOfDto(sourceId));
    return sourceDto;
  }

  @Override
  public List<UserConnectionSourceDto> findSourceDtoByWhereIncludeTables(
      Map<String, Object[]> paramMap) {
    List<UserConnectionSourceDto> sourceDtoList = new ArrayList<UserConnectionSourceDto>();
    List<UserConnectionSource> sourceList = findByWhere(paramMap);
    if (CollectionUtil.isEmpty(sourceList)) {
      return sourceDtoList;
    }
    for (UserConnectionSource source : sourceList) {
      if (source == null) {
        continue;
      }
      UserConnectionSourceDto sourceDto = new UserConnectionSourceDto(source);
      sourceDto.setTables(serviceFactory.getUserConnectionSourceTableService()
          .findBySourceIdOfDtoIncludeColumns(sourceDto.getSourceId()));
      sourceDtoList.add(sourceDto);
    }
    return sourceDtoList;
  }

  @Override
  public List<UserConnectionSourceDto> findSourceDtoByWhereIncludeTablesOutcludeColumns(
      Map<String, Object[]> paramMap, Map<String, String> orderMap) {
    List<UserConnectionSourceDto> sourceDtoList = new ArrayList<UserConnectionSourceDto>();
    List<UserConnectionSource> sourceList = findByWhere(paramMap, orderMap);
    if (CollectionUtil.isEmpty(sourceList)) {
      return sourceDtoList;
    }
    for (UserConnectionSource source : sourceList) {
      if (source == null) {
        continue;
      }
      UserConnectionSourceDto sourceDto = new UserConnectionSourceDto(source);
      sourceDto.setTables(serviceFactory.getUserConnectionSourceTableService()
          .findBySourceIdOfDto(sourceDto.getSourceId()));
      sourceDtoList.add(sourceDto);
    }
    return sourceDtoList;
  }

  @Override
  public UserConnectionSourceDto getSourceDto(String sourceId) {
    UserConnectionSource source = getSource(sourceId);
    return new UserConnectionSourceDto(source);
  }

  @Override
  public void updateLastModifiedDate(String sourceId, long lastModifiedDate) {
    userConnectionSourceDao.updateLastModifiedDate(sourceId, lastModifiedDate);
  }

  @Override
  public void updateUpdateTime(String sourceId, long updateTime) {
    userConnectionSourceDao.updateUpdateTime(sourceId, updateTime);
  }

  @Override
  public List<UserAccountSource> getUserAccountSource(String uid, String dsId) {
    return userConnectionSourceDao.getUserAccountSource(uid, dsId);
  }

  @Override
  public void updateConnectionSourceRemoteStatus(String status, String fileId) {
    userConnectionSourceDao.updateConnectionSourceRemoteStatus(status, fileId);
  }

  @Override
  public void updateConnectionSourceConfig(String config, String sourceId) {
    userConnectionSourceDao.updateConnectionSourceConfig(config, sourceId);
  }


  @Override
  public void updateConnectionSourcePath(String path, String connectionId) {
    userConnectionSourceDao.updateConnectionSourcePath(path, connectionId);
  }
  
  @Override
  public void updateConnectionSourceStatusByConnectionId(String status, String connectionId){
    userConnectionSourceDao.updateConnectionSourceStatusByConnectionId(status, connectionId);
  }

  @Override
  public void updateConnectionSourceRemoteStatusBySourceId(String sourceId, String status) {
    userConnectionSourceDao.updateConnectionSourceRemoteStatusBySourceId(sourceId, status);
  }

  @Override
  public List<UserAccountSource> getSpaceAccountSource(String spaceId, String dsId) {
    List<UserAccountSource> accountSourceList =
        userConnectionSourceDao.getSpaceAccountSource(spaceId, dsId);

    // 设置userName信息
    Set<String> uidList = new HashSet<String>();
    for (UserAccountSource source : accountSourceList) {
      uidList.add(source.getUid() + "");
    }
    if (uidList.size() > 0) {
      Map<String, Object[]> queryParamMap = new HashMap<String, Object[]>();
      queryParamMap.put("ptId", uidList.toArray());
      queryParamMap.put("status", new Object[] {Constants.validate});
      List<PtoneUser> userList = serviceFactory.getUserService().findByWhere(queryParamMap);
      Map<String, PtoneUser> userMap = new HashMap<String, PtoneUser>();
      if (userList != null && userList.size() > 0) {
        for (PtoneUser user : userList) {
          userMap.put(user.getPtId(), user);
        }
      }
      for (UserAccountSource source : accountSourceList) {
        PtoneUser user = userMap.get(source.getUid() + "");
        if (user != null) {
          source.setUserName(user.getUserName());
        }
      }
    }
    return accountSourceList;
  }

}
