package com.sizzler.domain.sys;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.Ignore;
import com.sizzler.dexcoder.annotation.PK;

public class SysMetaLog implements Serializable {

  private static final long serialVersionUID = 6818709760903287929L;

  @PK
  private String id;
  private String uid;
  private Long time;
  private Long serverTime;
  private String position;
  private String operate;
  private String operateId;
  private String content;
  @Ignore
  private String serverDate;
  @Ignore
  private String extend;

  public String getExtend() {
    return extend;
  }

  public void setExtend(String extend) {
    this.extend = extend;
  }

  public String getServerDate() {
    return serverDate;
  }

  public void setServerDate(String serverDate) {
    this.serverDate = serverDate;
  }

  public Long getServerTime() {
    return serverTime;
  }

  public void setServerTime(Long serverTime) {
    this.serverTime = serverTime;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getOperateId() {
    return operateId;
  }

  public void setOperateId(String operateId) {
    this.operateId = operateId;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getOperate() {
    return operate;
  }

  public void setOperate(String operate) {
    this.operate = operate;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  @Override
  public String toString() {
    return "SysMetaLog{" + "id='" + id + '\'' + ", uid='" + uid + '\'' + ", time=" + time
        + ", position='" + position + '\'' + ", operate='" + operate + '\'' + ", operateId='"
        + operateId + '\'' + ", content='" + content + '\'' + '}';
  }
}
