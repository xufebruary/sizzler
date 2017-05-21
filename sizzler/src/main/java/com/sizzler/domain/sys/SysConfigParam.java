package com.sizzler.domain.sys;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class SysConfigParam implements Serializable {

  private static final long serialVersionUID = 4826151829089638886L;

  // widget data <history> use cache : true || false
  public static final String WIDGET_HISTORY_DATA_USE_REDIS_CACHE_DEFAULT = "WIDGET_HISTORY_DATA_USE_REDIS_CACHE_DEFAULT";
  public static final String WIDGET_HISTORY_DATA_USE_REDIS_CACHE_PREFIX = "WIDGET_HISTORY_DATA_USE_REDIS_CACHE_"; // +dsCode

  // widget data <history> cache time : seconds
  public static final String WIDGET_HISTORY_DATA_REDIS_CACHE_TIME_DEFAULT = "WIDGET_HISTORY_DATA_REDIS_CACHE_TIME_DEFAULT";
  public static final String WIDGET_HISTORY_DATA_REDIS_CACHE_TIME_PREFIX = "WIDGET_HISTORY_DATA_REDIS_CACHE_TIME_"; // +dsCode

  // widget data <realtime> use cache : true || false
  public static final String WIDGET_REALTIME_DATA_USE_REDIS_CACHE_DEFAULT = "WIDGET_REALTIME_DATA_USE_REDIS_CACHE_DEFAULT";
  public static final String WIDGET_REALTIME_DATA_USE_REDIS_CACHE_PREFIX = "WIDGET_REALTIME_DATA_USE_REDIS_CACHE_"; // +dsCode

  // widget data <realtime> cache time : seconds
  public static final String WIDGET_REALTIME_DATA_REDIS_CACHE_TIME_DEFAULT = "WIDGET_REALTIME_DATA_REDIS_CACHE_TIME_DEFAULT";
  public static final String WIDGET_REALTIME_DATA_REDIS_CACHE_TIME_PREFIX = "WIDGET_REALTIME_DATA_REDIS_CACHE_TIME_"; // +dsCode

  // datasosurce data <history> use cache : true || false
  public static final String DS_HISTORY_DATA_USE_REDIS_CACHE_DEFAULT = "DS_HISTORY_DATA_USE_REDIS_CACHE_DEFAULT";
  public static final String DS_HISTORY_DATA_USE_REDIS_CACHE_PREFIX = "DS_HISTORY_DATA_USE_REDIS_CACHE_"; // +dsCode

  // datasosurce data <history> cache time : seconds
  public static final String DS_HISTORY_DATA_REDIS_CACHE_TIME_DEFAULT = "DS_HISTORY_DATA_REDIS_CACHE_TIME_DEFAULT";
  public static final String DS_HISTORY_DATA_REDIS_CACHE_TIME_PREFIX = "DS_HISTORY_DATA_REDIS_CACHE_TIME_"; // +dsCode

  // datasosurce data <realtime> use cache : true || false
  public static final String DS_REALTIME_DATA_USE_REDIS_CACHE_DEFAULT = "DS_REALTIME_DATA_USE_REDIS_CACHE_DEFAULT";
  public static final String DS_REALTIME_DATA_USE_REDIS_CACHE_PREFIX = "DS_REALTIME_DATA_USE_REDIS_CACHE_"; // +dsCode

  // datasosurce data <realtime> cache time : seconds
  public static final String DS_REALTIME_DATA_REDIS_CACHE_TIME_DEFAULT = "DS_REALTIME_DATA_REDIS_CACHE_TIME_DEFAULT";
  public static final String DS_REALTIME_DATA_REDIS_CACHE_TIME_PREFIX = "DS_REALTIME_DATA_REDIS_CACHE_TIME_"; // dsCode

  // profile data use cache : true || false
  public static final String PROFILE_DATA_USE_REDIS_CACHE_DEFAULT = "PROFILE_DATA_USE_REDIS_CACHE_DEFAULT";
  public static final String PROFILE_DATA_USE_REDIS_CACHE_PREFIX = "PROFILE_DATA_USE_REDIS_CACHE_"; // +dsCode

  // profile data cache time : seconds
  public static final String PROFILE_DATA_REDIS_CACHE_TIME_DEFAULT = "PROFILE_DATA_REDIS_CACHE_TIME_DEFAULT";
  public static final String PROFILE_DATA_REDIS_CACHE_TIME_PREFIX = "PROFILE_DATA_REDIS_CACHE_TIME_"; // +dsCode

  // metrics and dimension use cache : true || false
  public static final String DATA_DIMENSION_METRICS_CACHE_PREFIX = "DATA_DIMENSION_METRICS_CACHE_"; // +dsCode

  /**
   * 图例的排序顺序： 优先按照指标排序，然后按照设置规则排序，如果不设置默认按照valueDesc排序，设置为其他值则不排序
   * valueDesc(值总量倒序)||stringAsc(字符串升序)||default(不排序)
   */
  public static final String WIDGET_LEGEND_SORT_TYPE = "WIDGET_LEGEND_SORT_TYPE";

  @PK
  private long id;
  private String name;
  private String code;
  private String value;
  private String defaultValue;
  private String description;
  private String status;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
