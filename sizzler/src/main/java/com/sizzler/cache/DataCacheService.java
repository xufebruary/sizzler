package com.sizzler.cache;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 数据缓存服务
 * 
 * @author peng.xu
 * 
 */
@Component("dataCacheService")
public class DataCacheService {

  private static String LOG_PREFIX = "[DataCacheService] ";

  private Logger log = LoggerFactory.getLogger(DataCacheService.class);

  public static final String KEY_PREFIX_WIDGET_DATA = "WidgetDataKey::"; // 从数据源返回的对应widget的数据key

  public static final String KEY_PREFIX_PTONE_WIDGET_DATA = "PtoneWidgetDataKey::";// 根据widget设置处理后的widget数据key

//  @Autowired
//  private RedisService redisService;


  /**
   * 创建缓存的key
   * @param cacheKeyPrefix
   * @param cacheKeyMap
   * @return
   */
  public String buildCacheKey(String cacheKeyPrefix, Map<String, String> cacheKeyMap) {
    StringBuilder cacheKey = new StringBuilder(cacheKeyPrefix == null ? "" : cacheKeyPrefix);
    if (cacheKeyMap != null && !cacheKeyMap.isEmpty()) {
      for (Map.Entry<String, String> entry : cacheKeyMap.entrySet()) {
        String value = entry.getValue();
        cacheKey.append((value == null ? "" : value)).append("|");
      }
    }
    return cacheKey.toString();
  }

  /**
   * 缓存数据
   * 
   * @param cacheKeyPrefix
   * @param cacheKeyMap
   * @param cacheData
   * @return
   */
  public boolean cacheData(String cacheKeyPrefix, Map<String, String> cacheKeyMap,
      Object cacheData, int seconds) {
    return this.cacheData(this.buildCacheKey(cacheKeyPrefix, cacheKeyMap), cacheData, seconds);
  }

  /**
   * 缓存数据
   * 
   * @param cacheKey
   * @param cacheData
   * @param seconds
   * 
   * @modifyBy you.zou
   * @modifyDate 2016-09-13 18:27
   * @modifyDesc 修改调用的存储接口，不使用对象存储，使用字符串存储，对象转字符串的时候不允许循环引用
   * @return
   */
  public boolean cacheData(String cacheKey, Object cacheData, int seconds) {
    boolean result = false;
    try {
      String dataJsonStr =
          JSON.toJSONString(cacheData, SerializerFeature.DisableCircularReferenceDetect);
//      redisService.setKey(cacheKey, seconds, dataJsonStr);
      log.info(LOG_PREFIX + "cache data < " + seconds + "s > success cacheKey:: " + cacheKey);
      result = true;
    } catch (Exception e) {
      log.error(LOG_PREFIX + "cache data error cacheKey:: " + cacheKey + " :" + e.getMessage(), e);
    }
    return result;
  }

  /**
   * 检查是否存在缓存数据
   * 
   * @param cacheKey
   * @return
   */
  public boolean existsKey(String cacheKey) {
    boolean result = false;
    try {
//      result = redisService.existsKey(cacheKey);
    } catch (Exception e) {
      log.error(
          LOG_PREFIX + "check existsKey in cache error cacheKey:: " + cacheKey + " :"
              + e.getMessage(), e);
    }
    return result;
  }

  /**
   * 获取缓存数据
   * 
   * @param cacheKey
   * @return
   */
  public String getDataFromCache(String cacheKey) {
    String result = null;
    try {
//      if (redisService.existsKey(cacheKey)) {
//        result = redisService.getValueByKey(cacheKey);
//        log.info(LOG_PREFIX + "get data from cache success cacheKey:: " + cacheKey);
//      }
    } catch (Exception e) {
      log.error(
          LOG_PREFIX + " get data from cache error cacheKey:: " + cacheKey + " :" + e.getMessage(),
          e);
    }
    return result;
  }
}
