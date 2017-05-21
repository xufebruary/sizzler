package com.sizzler.provider.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ptmind on 2015/11/6.
 */
public abstract class MetricsCache {
  protected static Map<String, MetricsInfo> metricsInfoCache = new LinkedHashMap<>();

  public MetricsCache() {
    initCache();
  }

  public abstract void initCache();

  public MetricsInfo getMetricsInfo(String metricsName) {
    if (!metricsInfoCache.containsKey(metricsName)) {
      throw new RuntimeException(metricsName + " not in the cache,you should init it");
    }
    return metricsInfoCache.get(metricsName);
  }
}
