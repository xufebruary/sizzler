package com.sizzler.common.exception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {

  private static final long serialVersionUID = -5565189427865086856L;

  private String errorCode;
  private String errorMsg;

  public BusinessException() {
  }

  public BusinessException(String errorCode) {
    this.errorCode = errorCode;
  }

  public BusinessException(String errorCode, String errorMsg) {
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  @Override
  public String getMessage() {
    return this.errorMsg;
  }

}
