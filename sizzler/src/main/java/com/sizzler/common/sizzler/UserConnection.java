package com.sizzler.common.sizzler;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.Ignore;
import com.sizzler.dexcoder.annotation.PK;

public class UserConnection implements Serializable {

  private static final long serialVersionUID = 1938254397353332426L;

  @PK
  private String connectionId;// 唯一ID
  private String name;// 链接名（1：授权模式获得的是接口返回的用户名，2：数据库模式获得的是用户自己填写的链接名，3：Yahoo、S3等获取的是自己填写的链接名，4：Upload使用的是文件名+UUID作为链接名）
  private String uid;// 链接所属的用户ID
  private Long dsId;// 数据源ID
  private String dsCode;// 数据源Code
  private String status;// 状态（0：删除，1：正常）
  private String config;// 配置信息（1：授权模式存储的是授权相关信息，2：数据库类型存储的是数据库的账号密码IP等链接信息，3：Upload存储的是文件的存储位置等信息）
  private Long updateTime;// 修改时间，存储的是时间戳
  private String spaceId;// 链接所属的空间ID
  private String sourceType;// 来源类型：手动创建（USER_CREATED）、系统默认预制（SYSTEM_DEFAULT）
  private String isDefaultTimezone;//取数默认时区
  private String dataTimezone;//取数的时区设置

  public String getIsDefaultTimezone() {
    return isDefaultTimezone;
  }

  public void setIsDefaultTimezone(String isDefaultTimezone) {
    this.isDefaultTimezone = isDefaultTimezone;
  }

  public String getDataTimezone() {
    return dataTimezone;
  }

  public void setDataTimezone(String dataTimezone) {
    this.dataTimezone = dataTimezone;
  }

  @Ignore
  private String userName;// 冗余字段

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public Long getDsId() {
    return dsId;
  }

  public void setDsId(Long dsId) {
    this.dsId = dsId;
  }

  public String getDsCode() {
    return dsCode;
  }

  public void setDsCode(String dsCode) {
    this.dsCode = dsCode;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public Long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Long updateTime) {
    this.updateTime = updateTime;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

}
