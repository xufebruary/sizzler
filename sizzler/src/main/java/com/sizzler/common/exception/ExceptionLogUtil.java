package com.sizzler.common.exception;

import org.apache.log4j.Logger;

import com.sizzler.common.exception.BaseException;
import com.sizzler.common.exception.ExceptionLevelEnum;

public class ExceptionLogUtil {

  public static void log(BaseException exception, Logger log) {
    ExceptionLevelEnum exceptionLevel = exception.getExceptionLevel();
    if (exceptionLevel == ExceptionLevelEnum.ERROR
        || exceptionLevel == ExceptionLevelEnum.ERROR_NOTIFY) {
      log.error(exception.generateLogMessage(), exception);
    } else {
      log.warn(exception.generateLogMessage(), exception);
    }
  }

}
