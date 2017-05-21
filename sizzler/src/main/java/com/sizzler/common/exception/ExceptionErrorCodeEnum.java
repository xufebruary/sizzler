package com.sizzler.common.exception;

public enum ExceptionErrorCodeEnum {

  DATASOURCE_QUERY_FAILED("DATASOURCE_QUERY_FAILED");
  private String errorCode;

  ExceptionErrorCodeEnum(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }

}
