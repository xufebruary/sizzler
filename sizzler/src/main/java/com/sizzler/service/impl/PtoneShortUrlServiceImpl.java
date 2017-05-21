package com.sizzler.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.PtoneShortUrlDao;
import com.sizzler.domain.common.PtoneShortUrl;
import com.sizzler.service.PtoneShortUrlService;
import com.sizzler.system.Constants;

@Service("ptoneShortUrlService")
public class PtoneShortUrlServiceImpl extends ServiceBaseInterfaceImpl<PtoneShortUrl, String>
    implements PtoneShortUrlService {

  @Autowired
  private PtoneShortUrlDao ptoneShortUrlDao;

  @Override
  public PtoneShortUrl getByUrl(String url) {
    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("url", new Object[] {url});
    paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
    return getByWhere(paramMap);
  }

}
