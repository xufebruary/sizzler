package com.sizzler.system.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sizzler.system.api.exception.ResponseErrorEnum;

/**
 * @Description:统一请求返回结果.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseResult<T> {

  private boolean success;

  private String message;

  private T data;

  private String errorCode;

  private ResponseResult() {}

  public static <T> ResponseResult<T> newInstance() {
    return new ResponseResult<>();
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getErrorCode() {
    return errorCode;
  }


  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Object getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  // 设置错误信息
  public void setErrorInfo(ResponseErrorEnum responseErrorEnum) {
    this.errorCode = responseErrorEnum.getCode();
    this.message = responseErrorEnum.getMessage();
  }
}
