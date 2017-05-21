package com.sizzler.common.cache.provider;

import org.springframework.stereotype.Component;

@Component("cacheProvider")
public class RedisCacheProvider implements CacheProvider {

  // @Autowired
  // private RedisService redisService;

  @Override
  public Object get(String key) {
    // return redisService.getValueByKey(key);
    return null;
  }

  @Override
  public void put(String key, Object value) {
    this.put(key, value, -1);
  }

  @Override
  public void put(String key, Object value, long expire) {
    // redisService.setKey(key, (int) expire, JSON.toJSONString(value));
  }

  @Override
  public void remove(String key) {
    // redisService.remove(key);
  }

  @Override
  public void update(String key, Object value) {
    this.put(key, value);
  }
}
