package com.sizzler.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.sys.SysConfigParam;
import com.sizzler.service.sys.SysConfigParamService;

/**
 * 缓存系统参数
 * 
 * @author peng.xu
 * 
 */
@Component("sysConfigParamCache")
public class SysConfigParamCache {

  @Autowired
  private SysConfigParamService sysConfigParamService;

  private static Map<String, SysConfigParam> cacheMap; // <code, config>

  @PostConstruct
  public void init() {
    Map<String, SysConfigParam> newCacheMap = new HashMap<String, SysConfigParam>();
    List<SysConfigParam> paramList = sysConfigParamService.findAll();
    if (paramList != null) {
      for (SysConfigParam param : paramList) {
        newCacheMap.put(param.getCode().toUpperCase(), param);
      }
    }

    cacheMap = newCacheMap;
  }

  public String getValue(String code) {
    String value = null;
    SysConfigParam param = cacheMap.get(code.toUpperCase());
    if (param != null) {
      value = param.getValue();
      if (StringUtil.isBlank(value)) {
        value = param.getDefaultValue();
      }
    }
    return value;
  }

  public long getLongValue(String code, long defaultValue) {
    long value = defaultValue;
    String valueStr = this.getValue(code);
    if (StringUtil.isNotBlank(valueStr)) {
      value = Long.valueOf(valueStr);
    }
    return value;
  }

  public int getIntValue(String code, int defaultValue) {
    int value = defaultValue;
    String valueStr = this.getValue(code);
    if (StringUtil.isNotBlank(valueStr)) {
      value = Integer.valueOf(valueStr);
    }
    return value;
  }

  public double getDoubleValue(String code, double defaultValue) {
    double value = defaultValue;
    String valueStr = this.getValue(code);
    if (StringUtil.isNotBlank(valueStr)) {
      value = Double.valueOf(valueStr);
    }
    return value;
  }

  public boolean getBooleanValue(String code, boolean defaultValue) {
    boolean result = defaultValue;
    String valueStr = this.getValue(code);
    if (StringUtil.isNotBlank(valueStr)) {
      result = "true".equalsIgnoreCase(valueStr);
    }
    return result;
  }

  // //////////////////////////////////////////////////////

  /**
   * 是否使用数据源档案列表缓存
   * 
   * @param dsCode
   * @return
   * @date: 2016年8月27日
   * @author peng.xu
   */
  public boolean isUseProfileDataCache(String dsCode) {
    boolean defaultValue =
        this.getBooleanValue(SysConfigParam.PROFILE_DATA_USE_REDIS_CACHE_DEFAULT, true);
    return this.getBooleanValue(SysConfigParam.PROFILE_DATA_USE_REDIS_CACHE_PREFIX + dsCode,
        defaultValue);
  }

  /**
   * 数据源档案列表缓存时间（秒数）
   * 
   * @param dsCode
   * @return
   * @date: 2016年8月27日
   * @author peng.xu
   */
  public int getProfileDataCacheTime(String dsCode) {
    int defaultValue =
        this.getIntValue(SysConfigParam.PROFILE_DATA_REDIS_CACHE_TIME_DEFAULT, 24 * 60 * 60); // 默认一天
    return this.getIntValue(SysConfigParam.PROFILE_DATA_REDIS_CACHE_TIME_PREFIX + dsCode,
        defaultValue);
  }

  /**
   * 是否使用数据源历史数据缓存
   * 
   * @param dsCode
   * @return
   * @date: 2016年8月27日
   * @author peng.xu
   */
  public boolean isUseDsHistoryDataCache(String dsCode) {
    boolean defaultValue =
        this.getBooleanValue(SysConfigParam.DS_HISTORY_DATA_USE_REDIS_CACHE_DEFAULT, true);
    return this.getBooleanValue(SysConfigParam.DS_HISTORY_DATA_USE_REDIS_CACHE_PREFIX + dsCode,
        defaultValue);
  }

  /**
   * 数据源历史数据缓存时间（秒数）
   * 
   * @param dsCode
   * @return
   * @date: 2016年8月27日
   * @author peng.xu
   */
  public int getDsHistoryDataCacheTime(String dsCode) {
    int defaultValue =
        this.getIntValue(SysConfigParam.DS_HISTORY_DATA_REDIS_CACHE_TIME_DEFAULT, 7 * 24 * 60 * 60); // 默认缓存一周
    return this.getIntValue(SysConfigParam.DS_HISTORY_DATA_REDIS_CACHE_TIME_PREFIX + dsCode,
        defaultValue);
  }

  /**
   * 是否使用数据源实时数据缓存
   * 
   * @param dsCode
   * @return
   * @date: 2016年8月27日
   * @author peng.xu
   */
  public boolean isUseDsRealtimeDataCache(String dsCode) {
    boolean defaultValue =
        this.getBooleanValue(SysConfigParam.DS_REALTIME_DATA_USE_REDIS_CACHE_DEFAULT, false);
    return this.getBooleanValue(SysConfigParam.DS_REALTIME_DATA_USE_REDIS_CACHE_PREFIX + dsCode,
        defaultValue);
  }

  /**
   * 数据源实时数据缓存时间（秒数）
   * 
   * @param dsCode
   * @return
   * @date: 2016年8月27日
   * @author peng.xu
   */
  public int getDsRealtimeDataCacheTime(String dsCode) {
    int defaultValue =
        this.getIntValue(SysConfigParam.DS_REALTIME_DATA_REDIS_CACHE_TIME_DEFAULT, 0);// 默认不缓存
    return this.getIntValue(SysConfigParam.DS_REALTIME_DATA_REDIS_CACHE_TIME_PREFIX + dsCode,
        defaultValue);
  }

  /**
   * 是否使用Widget历史数据缓存
   * 
   * @param dsCode
   * @return
   * @date: 2016年9月12日
   * @author peng.xu
   */
  public boolean isUseWidgetHistoryDataCache(String dsCode) {
    boolean defaultValue =
        this.getBooleanValue(SysConfigParam.WIDGET_HISTORY_DATA_USE_REDIS_CACHE_DEFAULT, true);
    return this.getBooleanValue(SysConfigParam.WIDGET_HISTORY_DATA_USE_REDIS_CACHE_PREFIX + dsCode,
        defaultValue);
  }

  /**
   * Widget历史数据缓存时间（秒数）
   * 
   * @param dsCode
   * @return
   * @date: 2016年9月12日
   * @author peng.xu
   */
  public int getWidgetHistoryDataCacheTime(String dsCode) {
    int defaultValue =
        this.getIntValue(SysConfigParam.WIDGET_HISTORY_DATA_REDIS_CACHE_TIME_DEFAULT,
            7 * 24 * 60 * 60); // 默认缓存一周
    return this.getIntValue(SysConfigParam.WIDGET_HISTORY_DATA_REDIS_CACHE_TIME_PREFIX + dsCode,
        defaultValue);
  }

  /**
   * 是否使用Widget实时数据缓存
   * 
   * @param dsCode
   * @return
   * @date: 2016年9月12日
   * @author peng.xu
   */
  public boolean isUseWidgetRealtimeDataCache(String dsCode) {
    boolean defaultValue =
        this.getBooleanValue(SysConfigParam.WIDGET_REALTIME_DATA_USE_REDIS_CACHE_DEFAULT, false);
    return this.getBooleanValue(
        SysConfigParam.WIDGET_REALTIME_DATA_USE_REDIS_CACHE_PREFIX + dsCode, defaultValue);
  }

  /**
   * Widget实时数据缓存时间（秒数）
   * 
   * @param dsCode
   * @return
   * @date: 2016年9月12日
   * @author peng.xu
   */
  public int getWidgetRealtimeDataCacheTime(String dsCode) {
    int defaultValue =
        this.getIntValue(SysConfigParam.WIDGET_REALTIME_DATA_REDIS_CACHE_TIME_DEFAULT, 0);// 默认不缓存
    return this.getIntValue(SysConfigParam.WIDGET_REALTIME_DATA_REDIS_CACHE_TIME_PREFIX + dsCode,
        defaultValue);
  }

}
