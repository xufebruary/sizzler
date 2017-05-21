package com.sizzler.common.exception;

public class BusinessErrorCode {

  public static class Space {
    public final static String GET_SPACE_PANEL_WITH_COMPONENTS_LIST_ERROR =
        "GET_SPACE_PANEL_WITH_COMPONENTS_LIST_ERROR";
    public final static String ADD_OR_UPDATE_SPACE_TABLES_ERROR =
        "ADD_OR_UPDATE_SPACE_TABLES_ERROR";
    public final static String GET_SPACE_TABLES_ERROR =
        "GET_SPACE_TABLES_ERROR";
    public final static String GET_SPACE_DATASOURCE_ACCOUNT_SCHEMA_ERROR =
        "GET_SPACE_DATASOURCE_ACCOUNT_SCHEMA_ERROR";
    public final static String GET_SPACE_TABLES_REMOTE_DATA_ERROR =
        "GET_SPACE_TABLES_REMOTE_DATA_ERROR";
    public final static String BUILD_SPACE_DOMAIN_ERROR =
            "BUILD_SPACE_DOMAIN_ERROR";
  }

  public static class Panel {
    public final static String ADD_PANEL_ERROR = "ADD_PANEL_ERROR";
    public final static String COPY_PANEL_ERROR = "COPY_PANEL_ERROR";
    public final static String ADD_PANEL_BY_TEMPLET_ERROR = "ADD_PANEL_BY_TEMPLET_ERROR";
    public static final String ADD_SHARE_PANEL_ERROR = "ADD_SHARE_PANEL_ERROR";
    public static final String UPDATE_PANEL_ERROR = "UPDATE_PANEL_ERROR";
    public static final String UPDATE_PANEL_LAYOUT_ERROR = "UPDATE_PANEL_LAYOUT_ERROR";
    public static final String DELETE_PANEL_ERROR = "DELETE_PANEL_ERROR";
    public final static String GET_PANEL_WITH_COMPONENTS_ERROR = "GET_PANEL_WITH_COMPONENTS_ERROR";
    public static final String SHARE_PANEL_VERIFY_PASSWORD_ERROR =
        "SHARE_PANEL_VERIFY_PASSWORD_ERROR";
    public static final String APPLY_PANEL_COMPONENT_ERROR = "APPLY_PANEL_COMPONENT_ERROR";
    public static final String INIT_DEFAULT_PANEL_FOR_USER_FIRST_SPACE_ERROR =
        "INIT_DEFAULT_PANEL_FOR_USER_FIRST_SPACE_ERROR";

    // 业务上的失败提示
    public static final String PANEL_SHARE_DELETED_FAILED = "panel_share_deleted";
    public static final String PANEL_SHARE_CLOSED_FAILED = "panel_share_closed";
    public static final String PANEL_SHARE_SPACE_DELETED_FAILED = "panel_share_space_deleted";
    public static final String PANEL_SHARE_PASSWORD_ERROR_FAILED = "panel_share_password_error";
    
  }
  
  public static class PanelTemplet {
    public static final String UPDATE_PANEL_TEMPLET_ERROR = "UPDATE_PANEL_TEMPLET_ERROR";
    public static final String GET_ALL_PANEL_TEMPLET_LIST_ERROR = "GET_ALL_PANEL_TEMPLET_LIST_ERROR";
    public static final String GET_PANEL_TEMPLET_TAGS_ERROR = "GET_PANEL_TEMPLET_TAGS_ERROR";
    public static final String PUBLISH_PANEL_TEMPLET_ERROR = "PUBLISH_PANEL_TEMPLET_ERROR";
    public static final String GET_PUBLISHED_PANEL_TEMPLET_LIST_ERROR = "GET_PUBLISHED_PANEL_TEMPLET_LIST_ERROR";
  }

  public static class Widget {
    public final static String UPDATE_METRICS_DIMENSION_ALIAS_ERROR =
        "UPDATE_METRICS_DIMENSION_ALIAS_ERROR";
  }

  public static class User {
    public final static String USER_EMAIL_NOT_EXISTS =
            "USER_EMAIL_NOT_EXISTS";
    public final static String USER_ACTIVE_REDIS_KEY_VALIDATE =
            "USER_ACTIVE_REDIS_KEY_VALIDATE";
    public final static String USER_NOT_ACTIVE_ERROR =
            "USER_NOT_ACTIVE_ERROR";
  }

}
