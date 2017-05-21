package com.sizzler.common.cache.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CacheManager {

  @Autowired
  private CacheProvider cacheProvider;

  public CacheProvider getCacheProvider() {
    return cacheProvider;
  }

  public void setCacheProvider(CacheProvider cacheProvider) {
    this.cacheProvider = cacheProvider;
  }

}
