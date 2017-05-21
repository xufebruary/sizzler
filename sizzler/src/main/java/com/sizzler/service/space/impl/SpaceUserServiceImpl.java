package com.sizzler.service.space.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.space.SpaceUserDao;
import com.sizzler.domain.space.PtoneSpaceUser;
import com.sizzler.service.space.SpaceUserService;
import com.sizzler.system.Constants;

@Service("spaceUserService")
public class SpaceUserServiceImpl extends ServiceBaseInterfaceImpl<PtoneSpaceUser, Long> implements
    SpaceUserService {

  @Autowired
  private SpaceUserDao spaceUserDao;

  @Override
  public PtoneSpaceUser getSpaceUserByUid(String spaceId, String uid) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("spaceId", new Object[] {spaceId});
    paramMap.put("uid", new Object[] {uid});
    paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
    return spaceUserDao.getByWhere(paramMap);
  }

  @Override
  public PtoneSpaceUser getSpaceUserByEmail(String spaceId, String email) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("spaceId", new Object[] {spaceId});
    paramMap.put("userEmail", new Object[] {email});
    paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
    return spaceUserDao.getByWhere(paramMap);
  }

  @Override
  public List<PtoneSpaceUser> getUserSpaceUserList(String uid) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("uid", new Object[] {uid});
    paramMap.put("status", new Object[] {PtoneSpaceUser.STATUS_ACCEPTED});
    paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
    return spaceUserDao.findByWhere(paramMap);
  }

  @Override
  public List<PtoneSpaceUser> getSpaceFollowerSpaceUserList(String spaceId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("spaceId", new Object[] {spaceId});
    paramMap.put("isDelete", new Object[] {Constants.inValidateInt});

    Map<String, String> orderMap = new LinkedHashMap<String, String>();
    orderMap.put("type", "desc");
    orderMap.put("userEmail", "asc");

    return spaceUserDao.findByWhere(paramMap, orderMap);
  }
}
