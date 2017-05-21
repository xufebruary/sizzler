package com.sizzler.common;

import java.io.Serializable;

/**
 * 专门用于存放数据类型的常量类
 */
public class DataTypeConstants implements Serializable {

  private static final long serialVersionUID = -4457703705683686642L;

  /**
   * @desc 日期
   * @demo 2016-05-02
   */
  public static final String DATA_TYPE_DATE = "DATE";

  /**
   * @desc 日期时间
   * @demo 2016-05-02 15:33:00
   */
  public static final String DATA_TYPE_DATETIME = "DATETIME";

  /**
   * @desc 时间戳
   * @demo 1471429219
   */
  public static final String DATA_TYPE_TIMESTAMP = "TIMESTAMP";

  /**
   * @desc 时间
   * @demo 15:33:00
   */
  public static final String DATA_TYPE_TIME = "TIME";

  /**
   * @desc 持续时间
   * @demo 视频播放持续60s
   */
  public static final String DATA_TYPE_DURATION = "DURATION";

  /**
   * @desc 数值
   * @demo 12.5
   */
  public static final String DATA_TYPE_NUMBER = "NUMBER";

  /**
   * @desc 单精度浮点
   * @demo 12.11
   */
  public static final String DATA_TYPE_FLOAT = "FLOAT";

  /**
   * @desc 双精度浮点
   * @demo 12.11
   */
  public static final String DATA_TYPE_DOUBLE = "DOUBLE";

  /**
   * @desc 整数
   * @demo 100
   */
  public static final String DATA_TYPE_INTEGER = "INTEGER";

  /**
   * @desc 长整数
   * @demo 1000000
   */
  public static final String DATA_TYPE_LONG = "LONG";

  /**
   * @desc 百分比
   * @demo 10%
   */
  public static final String DATA_TYPE_PERCENT = "PERCENT";

  /**
   * @desc 货币
   * @demo $60
   */
  public static final String DATA_TYPE_CURRENCY = "CURRENCY";

  /**
   * @desc 字符串
   * @demo Hello World!
   */
  public static final String DATA_TYPE_STRING = "STRING";

}
