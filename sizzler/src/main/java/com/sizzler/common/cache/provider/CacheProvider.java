package com.sizzler.common.cache.provider;

public interface CacheProvider {
  public Object get(String key);

  // 未指定过期时间时，为永久缓存
  public void put(String key, Object value);

  public void put(String key, Object value, long expire);

  public void remove(String key);

  public void update(String key, Object value);
}
