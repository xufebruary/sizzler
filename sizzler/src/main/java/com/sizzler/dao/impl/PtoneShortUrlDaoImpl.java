package com.sizzler.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.PtoneShortUrlDao;
import com.sizzler.domain.common.PtoneShortUrl;
import com.sizzler.system.Constants;

@Repository("ptoneShortUrlDao")
public class PtoneShortUrlDaoImpl extends DaoBaseInterfaceImpl<PtoneShortUrl, String> implements
    PtoneShortUrlDao {

  @Override
  public PtoneShortUrl get(String shortKey) {
    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("shortKey", new Object[] {shortKey});
    paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
    return getByWhere(paramMap);
  }
}
