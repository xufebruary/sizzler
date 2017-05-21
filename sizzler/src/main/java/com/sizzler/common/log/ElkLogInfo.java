package com.sizzler.common.log;

import java.io.Serializable;

public class ElkLogInfo implements Serializable {

  private static final long serialVersionUID = -2962453468205491540L;

  public static final String STATUS_SUCCESS = "success";
  public static final String STATUS_ERROR = "error";
  public static final String STATUS_TIMEOUT = "timeout";
  public static final String STATUS_TRIED_FAILED = "triedFailed";

  private String ptoneOperate;
  private String ptoneQueryStartTime;
  private String ptoneQueryTime;
  private String ptoneMsg;
  private String ptoneStatus;
  private String ptoneUid;
  private String ptoneEmail;

  public String getPtoneOperate() {
    return ptoneOperate;
  }

  public void setPtoneOperate(String ptoneOperate) {
    this.ptoneOperate = ptoneOperate;
  }

  public String getPtoneQueryStartTime() {
    return ptoneQueryStartTime;
  }

  public void setPtoneQueryStartTime(String ptoneQueryStartTime) {
    this.ptoneQueryStartTime = ptoneQueryStartTime;
  }

  public String getPtoneQueryTime() {
    return ptoneQueryTime;
  }

  public void setPtoneQueryTime(String ptoneQueryTime) {
    this.ptoneQueryTime = ptoneQueryTime;
  }

  public String getPtoneMsg() {
    return ptoneMsg;
  }

  public void setPtoneMsg(String ptoneMsg) {
    this.ptoneMsg = ptoneMsg;
  }

  public String getPtoneStatus() {
    return ptoneStatus;
  }

  public void setPtoneStatus(String ptoneStatus) {
    this.ptoneStatus = ptoneStatus;
  }

  public String getPtoneUid() {
    return ptoneUid;
  }

  public void setPtoneUid(String ptoneUid) {
    this.ptoneUid = ptoneUid;
  }

  public String getPtoneEmail() {
    return ptoneEmail;
  }

  public void setPtoneEmail(String ptoneEmail) {
    this.ptoneEmail = ptoneEmail;
  }

}
