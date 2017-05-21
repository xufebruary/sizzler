package com.sizzler.system.api.exception;

public enum ResponseErrorEnum {

  // server
  ILLEGAL_PARAMS("ILLEGAL_PARAMS", "请求参数不合法!"), INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR",
      "服务器内部异常!"), INTERNAL_INTERFACE_ERROR("INTERNAL_INTERFACE_ERROR", "接口内部异常!"),

  // space
  BUSINESS_SPACE_DEL_ERROR("space_del", "空间已删除"),

  // panel
  BUSINESS_PANEL_DEL_ERROR("panel_del", "面板已删除"), BUSINESS_PANEL_CLOSE_ERROR("panel_close",
      "面板已关闭分享"), BUSINESS_PANEL_PASSWORD_ERROR("password_error", "面板分享密码不匹配");

  ResponseErrorEnum(String code, String message) {
    this.code = code;
    this.message = message;
  }

  private String code;

  private String message;

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
