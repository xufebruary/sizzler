package com.sizzler.common.restful;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.Constants;

public class JsonView implements Serializable {

  private static final long serialVersionUID = -2151128710811225751L;

  private Logger logger = LoggerFactory.getLogger(JsonView.class);

  private String status;
  private String message;
  private Object content;
  private String dataVersion;
  private String code;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Object getContent() {
    return content;
  }

  public void setContent(Object content) {
    this.content = content;
  }

  public String getDataVersion() {
    return dataVersion;
  }

  public void setDataVersion(String dataVersion) {
    this.dataVersion = dataVersion;
  }

  public void successPack(Object result) {
    this.setMessage("");
    this.setContent(result);
    this.setStatus(Constants.JsonViewConstants.JSON_VIEW_STATUS_SUCCESS);
  }

  public void messagePack(String message) {
    this.setMessage(message);
    this.setContent("");
    this.setStatus(Constants.JsonViewConstants.JSON_VIEW_STATUS_SUCCESS);
    logger.info(message);
  }

  public void errorPack(String errorLog, Exception e) {
    setMessage(errorLog + " " + e.getLocalizedMessage());
    setContent("");
    setStatus(Constants.JsonViewConstants.JSON_VIEW_STATUS_ERROR);
    logger.error(errorLog + " | " + e, e);
  }

  public void errorPack(String errorLog) {
    setMessage(errorLog);
    setContent("");
    setStatus(Constants.JsonViewConstants.JSON_VIEW_STATUS_ERROR);
    logger.error(errorLog);
  }

  public void errorPackRequest(String errorLog, Exception e, HttpServletRequest request) {
    errorPack(errorLog, e);
    request.setAttribute("method_result", "failed");
  }

  public void failedPack(String errMsg) {
    setMessage(errMsg);
    setContent("");
    setStatus(Constants.JsonViewConstants.JSON_VIEW_STATUS_FAILED);
    logger.info(errMsg);
  }

  /**
   * 新增code参数
   */
  public void failedPack(String code, String errMsg) {
    setMessage(errMsg);
    setContent("");
    setStatus(Constants.JsonViewConstants.JSON_VIEW_STATUS_FAILED);
    setCode(code);
    logger.info(errMsg);
  }

  public void failedPackRequest(String errMsg, HttpServletRequest request) {
    failedPack(errMsg);
    request.setAttribute("method_result", "failed");
  }

  public void failedPack(String errMsg, Object result) {
    setMessage(errMsg);
    setContent(result);
    setStatus(Constants.JsonViewConstants.JSON_VIEW_STATUS_FAILED);
    logger.info(errMsg);
  }

  public void failedPack(Exception e) {
    failedPack(e.getMessage());
  }

}
