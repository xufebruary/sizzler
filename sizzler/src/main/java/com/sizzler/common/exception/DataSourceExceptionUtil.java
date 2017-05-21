package com.sizzler.common.exception;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.exception.BaseException;
import com.sizzler.common.sizzler.UserConnection;

public class DataSourceExceptionUtil {

  public static final String ERROR = "error!";

  public static void addUserConnectionToException(BaseException exception,
      UserConnection userConnection) {
    if (userConnection != null) {
      exception.setParam(ExceptionParamKey.UID, userConnection.getUid())
          .setParam(ExceptionParamKey.USER_CONNECTION_ID, userConnection.getConnectionId())
          .setParam(ExceptionParamKey.USER_CONNECTION_CONFIG, userConnection.getConfig());
    }
  }

  /**
   * 
   * @description 返回操作描述信息
   * @param operate
   * @return operate + error!
   */
  public static String buildExceptionOperate(String operate) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(operate);
    stringBuilder.append(" ");
    stringBuilder.append(ERROR);
    return stringBuilder.toString();
  }

  /**
   * Add error code to exception.
   * 
   * @param baseException
   *          the base exception
   * @param exception
   *          the exception
   */
  public static void addErrorCodeToException(BaseException baseException, Exception exception) {
    if (exception instanceof ServiceException) {
      baseException.errorCode(((ServiceException) exception).getErrorCode());
    }
  }

  /**
   * 新增将Object对象转为Map，存储到exception的paramMap中<br>
   * 先加上该方法
   * 
   * @param exception
   * @param params
   */
  @SuppressWarnings("unchecked")
  public static void addRequestParamToException(BaseException exception, Object... params) {
    if (exception == null || params == null || params.length == 0) {
      return;
    }
    for (Object param : params) {
      if (param == null) {
        continue;
      }
      String paramClassName = param.getClass().getSimpleName();
      String paramJson = JSON.toJSONString(param);
      Map<String, Object> _paramMap = JSON.parseObject(paramJson, Map.class);
      exception.setParam(paramClassName, _paramMap);
    }
  }
}
