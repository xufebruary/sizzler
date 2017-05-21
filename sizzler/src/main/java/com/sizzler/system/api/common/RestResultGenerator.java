package com.sizzler.system.api.common;

import com.sizzler.common.exception.BusinessException;
import com.sizzler.system.api.exception.ResponseErrorEnum;

public class RestResultGenerator {

  /**
   * 生成响应成功的(不带正文)的结果
   * 
   * @param message
   *          成功提示信息
   * @return ResponseResult
   */
  @SuppressWarnings("rawtypes")
  public static ResponseResult genMsgResult(String message) {
    ResponseResult responseResult = ResponseResult.newInstance();
    responseResult.setSuccess(true);
    responseResult.setMessage(message);
    return responseResult;
  }

  /**
   * 生成响应成功(带正文)的结果
   * 
   * @param data
   *          结果正文
   * @param message
   *          成功提示信息
   * @return ResponseResult<T>
   */
  public static <T> ResponseResult<T> genResult(T data, String message) {
    ResponseResult<T> result = ResponseResult.newInstance();
    result.setSuccess(true);
    result.setData(data);
    result.setMessage(message);
    return result;
  }

  /**
   * 生成响应成功(带正文没有描述)的结果
   * 
   * @param data
   *          结果正文
   * @return ResponseResult<T>
   */
  public static <T> ResponseResult<T> genResult(T data) {
    ResponseResult<T> result = ResponseResult.newInstance();
    result.setSuccess(true);
    result.setData(data);
    return result;
  }

  /**
   * 生成响应失败的结果
   * 
   * @param message
   *          自定义错误信息
   * @return ResponseResult
   */
  @SuppressWarnings("rawtypes")
  public static ResponseResult genErrorResult(String message) {
    ResponseResult result = ResponseResult.newInstance();
    result.setSuccess(false);
    result.setMessage(message);
    return result;
  }

  /**
   * 生成响应失败的结果
   * 
   * @param e
   *          自定义错误信息
   * @return ResponseResult
   */
  @SuppressWarnings("rawtypes")
  public static ResponseResult genErrorResult(BusinessException e) {
    ResponseResult result = ResponseResult.newInstance();
    result.setSuccess(false);
    result.setErrorCode(e.getErrorCode());
    result.setMessage(e.getErrorMsg());
    return result;
  }

  /**
   * 生成响应失败(带errorCode)的结果
   * 
   * @param responseErrorEnum
   *          失败信息
   * @return ResponseResult
   */
  @SuppressWarnings("rawtypes")
  public static ResponseResult genErrorResult(ResponseErrorEnum responseErrorEnum) {
    ResponseResult result = ResponseResult.newInstance();
    result.setSuccess(false);
    result.setErrorInfo(responseErrorEnum);
    return result;
  }

}
