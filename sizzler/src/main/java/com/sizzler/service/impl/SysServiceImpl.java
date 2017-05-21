package com.sizzler.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.cache.PtoneCacheManager;
import com.sizzler.domain.sys.SysDataVersion;
import com.sizzler.service.SysService;
import com.sizzler.service.sys.SysDataVersionService;
import com.sizzler.system.ServiceFactory;

@Service("sysService")
public class SysServiceImpl implements SysService {

  @Autowired
  private SysDataVersionService sysDataVersionService;

  @Autowired
  private PtoneCacheManager ptoneCacheManager;

  @Autowired
  private ServiceFactory serviceFactory;

  @Override
  public Map<String, String> getAllDataVersion() {
    Map<String, String> result = new HashMap<String, String>();
    List<SysDataVersion> dataVersionList = sysDataVersionService.findAll();
    if (dataVersionList != null) {
      for (SysDataVersion version : dataVersionList) {
        result.put(version.getCode(), version.getVersion());
      }
    }
    return result;
  }

  @Override
  public String refreshMemeryCache() {
    return ptoneCacheManager.refreshMemeryCache();
  }

  @Override
  public boolean validateAccessTokenKey(String key) {
    return ptoneCacheManager.getSysConfigParamCache().getBooleanValue(key, false);
  }

}
