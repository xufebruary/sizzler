package com.sizzler.common;

public final class Constants {

  public final class Env {

    private Env() {}

    public static final String BASE_HOME = "configs/";

    public static final String BASE_CONF = "base.conf";
  }

  public final class RestPathPrefix {
    public static final String CONSOLE = "console/";
    public static final String GATHER = "gather/";
    public static final String DATABASE = "database/";

  }

  /**
   * Restful 对外的静态变量
   */
  public final class JsonViewConstants {

    public static final String PARAM_DATA_VERSION = "dataVersion";

    public static final String STATUS_SUCCESS = "success";

    public static final String STATUS_FAIL = "fail";

    public static final String JSON_VIEW_STATUS_SUCCESS = "success";
    public static final String JSON_VIEW_STATUS_FAILED = "failed";
    public static final String JSON_VIEW_STATUS_ERROR = "error";
    /* 错误信息列表 */
    public static final String ERRMSG_ID = "主键ID为空";
    public static final String ERRMSG_OBJ = "对象为空";
  }

}
