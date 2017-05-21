package com.sizzler.common.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.utils.UuidUtil;

public abstract class BaseException extends RuntimeException {

  private static final long serialVersionUID = 5018680909180202804L;

  private String message;
  private Exception exception;
  private ExceptionLevelEnum exceptionLevel;
  private Map<String, Object> paramMap;
  private String errorCode;
  private final static String MSG_SPLITER = ",";
  private String uuid;

  public BaseException(String message) {
    super(message);
    this.message = message;
    init();
  }

  public BaseException(String message, Exception exception) {
    super(message, exception);
    this.message = message;
    this.exception = exception;
    init();
  }

  private void init() {
    paramMap = new LinkedHashMap<String, Object>();
    exceptionLevel = initLevel();
    uuid = UuidUtil.generateUuid();
  }

  public BaseException level(ExceptionLevelEnum exceptionLevel) {
    this.exceptionLevel = exceptionLevel;
    return this;
  }

  public BaseException errorCode(String errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  public BaseException setParam(String key, Object value) {
    paramMap.put(key, value);
    return this;
  }

  public Exception getException() {
    return exception;
  }

  public ExceptionLevelEnum getExceptionLevel() {
    return exceptionLevel;
  }

  public Map<String, Object> getParamMap() {
    return paramMap;
  }

  public String getErrorCode() {
    return errorCode;
  }

  /*
   * 生成发送给模块负责人的异常信息 信息的格式为-->BusinessDomain:,Exception:,Message:
   */
  public String generateNotifyMessage() {
    StringBuilder notifyMessageBuilder = new StringBuilder();
    notifyMessageBuilder.append("ExceptionId:").append(uuid).append(MSG_SPLITER);
    notifyMessageBuilder.append("BusinessDomain:").append(getBusinessDomain()).append(MSG_SPLITER);
    StringBuilder exceptionName = new StringBuilder(this.getClass().getName());
    if (exception != null) {
      exceptionName.append(" Caused by:").append(exception.getClass().getName());
    }
    notifyMessageBuilder.append("Exception:").append(exceptionName).append(MSG_SPLITER);
    notifyMessageBuilder.append("Message:").append(message);
    return notifyMessageBuilder.toString();
  }

  /*
   * 生成用于记录日志的异常信息
   * 信息的格式为-->BusinessDomain:,Exception:,Message:,Params:,StackTrace:
   */
  public String generateLogMessage() {
    StringBuilder logMessageBuilder = new StringBuilder();
    logMessageBuilder.append(generateNotifyMessage());
    logMessageBuilder.append(MSG_SPLITER).append("Params:").append(generateParamsString())
        .append(MSG_SPLITER);
    logMessageBuilder.append("StackTrace:").append(generateStackTraceString());

    return logMessageBuilder.toString();
  }

  public String buildEmailTitle() {
    StringBuilder result = new StringBuilder();
    result.append("[").append(String.valueOf(this.exceptionLevel)).append("] ");
    result.append(this.getBusinessDomain()).append(" ");
    result.append(this.message);
    return result.toString();
  }

  /**
   * 生成邮件内容
   */
  public String buildEmailContent() {
    StringBuilder result = new StringBuilder();
    result
        .append("<html><head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>");
    result.append("<p><element style='font-size:18px; color:rgb(187,23,16); line-height:26px'>"
        + parseToHtml(this.generateNotifyMessage()) + "</element><br/><br/>");
    result
        .append("<p><element style='font-size:22px; color:rgb(32,136,178); line-height:32px'>Business Domain</element><br/>"
            + parseToHtml(this.getBusinessDomain()) + "</p>");
    result
        .append("<p><element style='font-size:22px; color:rgb(32,136,178); line-height:32px'>Exception Level</element><br/>"
            + parseToHtml(String.valueOf(this.exceptionLevel)) + "</p>");
    result
        .append("<p><element style='font-size:22px; color:rgb(32,136,178); line-height:32px'>Message</element><br/>"
            + parseToHtml(this.message) + "</p>");
    result
        .append("<p><element style=' font-size:22px; color:rgb(32,136,178); line-height:32px'>Exception</element><br/>"
            + parseToHtml(this.getClass().getName()) + "</p>");
    if (this.exception != null) {
      result
          .append("<p><element style='font-size:22px; color:rgb(32,136,178); line-height:32px'>Caused By</element><br/>"
              + parseToHtml(this.exception.getClass().getName()) + "</p>");
    }
    result
        .append("<p><element style='font-size:22px; color:rgb(32,136,178); line-height:32px'>Stack Trace</element><br/>"
            + parseToHtml(this.generateStackTraceString()) + "</p>");
    result.append("</body></html>");

    return result.toString();
  }

  /**
   * 转义html字符串中的常见特殊字符
   */
  private static String parseToHtml(String str) {
    if (str == null) {
      return "";
    }
    String htmlStr = str;
    htmlStr = htmlStr.replace("'", "&apos;");
    htmlStr = htmlStr.replaceAll("&", "&amp;");
    htmlStr = htmlStr.replace("\"", "&quot;"); // "
    htmlStr = htmlStr.replace("\t", "&nbsp;&nbsp;");// 替换跳格
    htmlStr = htmlStr.replace(" ", "&nbsp;");// 替换空格
    htmlStr = htmlStr.replace("<", "&lt;");
    htmlStr = htmlStr.replaceAll(">", "&gt;");
    htmlStr = htmlStr.replace("\n", "<br/>");// 替换回车， 注意需要在最后，避免<br/>被转义

    return htmlStr;
  }

  /*
   * 生成用户所设置的参数信息
   */
  private String generateParamsString() {
    StringBuilder paramStringBuilder = new StringBuilder("[");
    if (paramMap != null && !paramMap.isEmpty()) {
      for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
        paramStringBuilder.append(entry.getKey()).append("==");
        try {
          paramStringBuilder.append(JSON.toJSONString(entry.getValue()));
        } catch (Exception e) {
          paramStringBuilder.append(entry.getValue().toString());
        }
        paramStringBuilder.append(",");
      }
    }
    paramStringBuilder.append("]");
    return paramStringBuilder.toString();
  }

  public String generateStackTraceString() {
    // return
    // exception==null?ExceptionUtil.getExceptionStackTraceStr(this):ExceptionUtil.getExceptionStackTraceStr(exception);
    return ExceptionUtil.getExceptionStackTraceStr(this);
  }

  /*
   * 默认的异常级别为：ERROR_NOTIFY
   */
  public ExceptionLevelEnum initLevel() {
    return ExceptionLevelEnum.ERROR_NOTIFY;
  }

  public abstract String getBusinessDomain();

}
