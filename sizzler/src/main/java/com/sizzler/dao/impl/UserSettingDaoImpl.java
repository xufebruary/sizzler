package com.sizzler.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.UserSettingDao;
import com.sizzler.domain.user.PtoneUserBasicSetting;

@Repository("userSettingDao")
public class UserSettingDaoImpl extends DaoBaseInterfaceImpl<PtoneUserBasicSetting, String>
    implements UserSettingDao {

  @Override
  public PtoneUserBasicSetting get(String id) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("ptId", new Object[] {id});
    return this.getByWhere(paramMap);
  }
}
