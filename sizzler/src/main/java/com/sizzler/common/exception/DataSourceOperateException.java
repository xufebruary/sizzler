package com.sizzler.common.exception;

import com.sizzler.common.exception.BaseException;

/**
 * 所有数据源通用的异常类，需要在创建异常时指定dsCode
 */
public class DataSourceOperateException extends BaseException {

  private static final long serialVersionUID = -4794669004192432584L;

  private String dsCode;

  public DataSourceOperateException(String dsCode, String message) {
    super(message);
    this.dsCode = dsCode;
  }

  public DataSourceOperateException(String dsCode, String message, Exception exception) {
    super(message, exception);
    this.dsCode = dsCode;
  }

  @Override
  public String getBusinessDomain() {
    return dsCode;
  }

  /**
   * 创建DataSourceOperateException的builder方法，该方法同时接收一个Object类型的参数。
   * 
   * @param dsCode
   * @param message
   * @param exception
   * @param param
   * @return
   */
  public static DataSourceOperateException builder(String dsCode, String message,
      Exception exception, Object param) {
    DataSourceOperateException dataSourceOperateException = new DataSourceOperateException(dsCode,
        message, exception);
    DataSourceExceptionUtil.addRequestParamToException(dataSourceOperateException, param);
    DataSourceExceptionUtil.addErrorCodeToException(dataSourceOperateException, exception);
    return dataSourceOperateException;
  }
}
