package com.sizzler.common.exception;

import java.io.Serializable;

public class ErrorCode implements Serializable {

  private static final long serialVersionUID = -1232279464571242356L;

  // 获取数据成功
  public static final String CODE_SUCCESS = "MSG_SUCCESS";
  public static final String MSG_SUCCESS = "Get data successs !";

  // 获取数据失败
  public static final String CODE_FAILED = "MSG_FAILED";
  public static final String MSG_FAILED = "Get data failed !";

  // 无数据
  public static final String CODE_NO_DATA = "MSG_NO_DATA";
  public static final String MSG_NO_DATA = "No data to show !";

  // 访问数据源时，提示无权限、或授权已过期
  public static final String CODE_NO_AUTH = "MSG_NO_AUTH";
  public static final String MSG_NO_AUTH = "Auth token is invalid or has expired !";

  public static final String CODE_BAD_REQUEST = "MSG_BAD_REQUEST";
  public static final String MSG_BAD_REQUEST = "Bad Request !";

  // 使用账号未授权或已解绑（connection）
  public static final String CODE_NO_ACCOUNT_AUTH = "MSG_NO_ACCOUNT_AUTH";
  public static final String MSG_NO_ACCOUNT_AUTH = "This account has no auth !";

  // 使用的源未授权（如：source 的表已解绑）
  public static final String CODE_NO_SOURCE_AUTH = "MSG_NO_SOURCE_AUTH";
  public static final String MSG_NO_SOURCE_AUTH = "This source has no auth !";
  
  public static final String CODE_NO_SOURCE_EXISTS =  "MSG_NO_SOURCE_EXISTS";
  public static final String MSG_NO_SOURCE_EXISTS = "This source not exists !";;

  /**
   * Database
   */
  // 没有权限、用户名、密码错误
  public static final String CODE_DB_ACCESS_DENIED = "MSG_DB_ACCESS_DENIED";
  public static final String MSG_DB_ACCESS_DENIED = "Access denied for this user !";

  // 连接不到数据库、ip、端口错误
  public static final String CODE_DB_LINK_FAILURE = "MSG_DB_LINK_FAILURE";
  public static final String MSG_DB_LINK_FAILURE = "Communications link failure !";

  // 所连接数据库不存在
  public static final String CODE_DB_UNKNOWN_DATABASE = "MSG_DB_UNKNOWN_DATABASE";
  public static final String MSG_DB_UNKNOWN_DATABASE = "Unknown database !";

  // 表不存在
  public static final String CODE_DB_UNKNOWN_TABLE = "MSG_DB_UNKNOWN_TABLE";
  public static final String MSG_DB_UNKNOWN_TABLE = "Unknown table !";

  // 列不存在
  public static final String CODE_DB_UNKNOWN_COLUMN = "MSG_DB_UNKNOWN_COLUMN";
  public static final String MSG_DB_UNKNOWN_COLUMN = "Unknown column !";

  // 数据量超过限制
  public static final String CODE_DB_ROW_COUNT_BEYOND_LIMIT = "MSG_DB_ROW_COUNT_BEYOND_LIMIT";
  public static final String MSG_DB_ROW_COUNT_BEYOND_LIMIT = "Row count beyond the limit !";

  // 接口请求类型未知，无法找到对应接口
  public static final String COCE_API_UNKNOWN_INTERFACE = "MSG_API_UNKNOWN_INTERFACE";
  public static final String MSG_API_UNKNOWN_INTERFACE = "Unknown request type!";

  /**
   * 数据源管理
   */
  // 链接名已存在
  public static final String CODE_CONNECTION_NAME_EXISTS = "CONNECTION_NAME_EXISTS";
  public static final String MSG_CONNECTION_NAME_EXISTS = "connection name has already exists.";

  // 请求超时
  public static final String CODE_REQUEST_OUT_TIME = "MSG_REQUEST_OUT_TIME";
  public static final String MSG_REQUEST_OUT_TIME = "Request time out!";

}
