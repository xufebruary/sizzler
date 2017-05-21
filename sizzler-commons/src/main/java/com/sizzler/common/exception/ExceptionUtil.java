package com.sizzler.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

  public static String getExceptionStackTraceStr(Exception e) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    try {
      e.printStackTrace(printWriter);
      return stringWriter.toString();
    } finally {
      printWriter.close();
    }
  }

}
