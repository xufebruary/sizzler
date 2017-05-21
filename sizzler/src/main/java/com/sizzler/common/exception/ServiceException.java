package com.sizzler.common.exception;

public class ServiceException extends RuntimeException {

  private static final long serialVersionUID = 700941685485971201L;
  
  private String errorCode = ErrorCode.CODE_FAILED;
  private String errorMsg = ErrorCode.MSG_FAILED;

  public ServiceException(Exception e) {
    super(e);
  }

  public ServiceException(String msg, Exception e) {
    super(msg, e);
  }

  public ServiceException(String msg) {
    super(msg);
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


  /**
   * 将BaseException转成ServiceException，并设置code 和 msg 值
   * @author shaoqiang.guo
   * @param BaseException
   * @return
   */
  public static ServiceException buildServiceException(BaseException e) {
    ServiceException se = new ServiceException(e.getMessage(), e);
    se.setErrorCode(e.getErrorCode());
    se.setErrorMsg(e.getMessage());
    return se;
  }

  /**
   * 带有异常信息的
   * @param errorMsg
   * @param e
   * @return
   */
  public static ServiceException buildServiceException(String errorMsg, Exception e) {
    ServiceException se = new ServiceException(errorMsg, e);
    return se;
  }

  /**
   * 不带异常信息的，错误提示自定义的
   * @param errorMsg
   * @param errorCode
   * @return
   * @modify by you.zou
   * @modifyDate 2016-08-04
   * @modifyDesc errorMsg设置到ServiceException中
   */
  public static ServiceException buildServiceException(String errorMsg, String errorCode) {
    ServiceException se = new ServiceException(errorMsg);
    se.setErrorCode(errorCode);
    se.setErrorMsg(errorMsg);
    return se;
  }

}
