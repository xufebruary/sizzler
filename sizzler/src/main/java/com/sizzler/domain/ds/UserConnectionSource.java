package com.sizzler.domain.ds;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class UserConnectionSource implements Serializable {

  private static final long serialVersionUID = -8337533133571336491L;

  public static final String REMOTE_STATUS_DB_UNKNOWN_DATABASE = "DB_UNKNOWN_DATABASE";
  public static final String REMOTE_STATUS_DB_UNKNOWN_TABLE = "DB_UNKNOWN_TABLE";
  public static final String REMOTE_STATUS_DB_LINK_FAILURE = "DB_LINK_FAILURE";
  public static final String REMOTE_STATUS_DB_ACCESS_DENIED = "DB_ACCESS_DENIED";

  public static final String UPDATE_FREQUENCY_NEVER = "never";
  public static final String UPDATE_FREQUENCY_HOUR = "hour";
  public static final String UPDATE_FREQUENCY_DAY = "day";
  public static final String UPDATE_FREQUENCY_MONDAY = "monday";

  public static final String UPDATE_STATUS_SUCCESS = "success";
  public static final String UPDATE_STATUS_FAILED = "failed";
  public static final String UPDATE_STATUS_WAITING = "waiting";
  public static final String UPDATE_STATUS_UPDATING = "updating";

  @PK
  private String sourceId;//唯一ID
  private String fileId;//文件ID
  private String folderId;//目录ID，仅用于有目录的数据源
  private String name;//文件名、表名
  private Long uid;//所属用户ID
  private Long dsId;//所属数据源ID
  private String connectionId;//所属的ConnectionID
  private String dsCode;//所属数据源Code
  private String config;//JSON串形式，存储的是表列表的结构，包括：表名、忽略列、忽略行、表ID、列总数、行总数信息，重构后该字段废弃
  private String status;//状态（0：删除，1：正常）
  private Long createTime;//创建时间，存储的是时间戳
  private Long updateTime;//修改时间，存储的是时间戳
  private Long lastModifiedDate;//远程文件最后修改时间，存储的是时间戳
  private String remotePath;//远程文件的地址
  private String remoteStatus;//远程文件的状态（0：删除，1：正常，Other：具体错误的字符串）
  private String spaceId;//所属的空间ID

  private String updateFrequency; // 更新频率， 值包括： never（不更新）、hour（按小时更新）、day（按天更新）、monday（每周一更新，即按周更新）
  private String updateHour; // 更新的小时数，默认为每天5点更新，默认值为 05:00， 可选值（00:00 - 23:00 ）
  private String timezone; // 用户时区： +08:00 （中国）， +09:00（日本） ， +00:00（欧美），用户时区根据域名进行判断。
  private String cronExpr; // spring quartz 定时更新任务对应的cron表达式
  private String lastUpdateTime; // 最近更新时间
  private String updateStatus; // 更新状态，包括：success、failed、waiting、updating

  private String userName;// 冗余字段

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

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  public String getRemoteStatus() {
    return remoteStatus;
  }

  public void setRemoteStatus(String remoteStatus) {
    this.remoteStatus = remoteStatus;
  }

  public String getRemotePath() {
    return remotePath;
  }

  public void setRemotePath(String remotePath) {
    this.remotePath = remotePath;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public Long getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Long lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public Long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Long updateTime) {
    this.updateTime = updateTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getUid() {
    return uid;
  }

  public void setUid(Long uid) {
    this.uid = uid;
  }

  public Long getDsId() {
    return dsId;
  }

  public void setDsId(Long dsId) {
    this.dsId = dsId;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getDsCode() {
    return dsCode;
  }

  public void setDsCode(String dsCode) {
    this.dsCode = dsCode;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUpdateFrequency() {
    return updateFrequency;
  }

  public void setUpdateFrequency(String updateFrequency) {
    this.updateFrequency = updateFrequency;
  }

  public String getUpdateHour() {
    return updateHour;
  }

  public void setUpdateHour(String updateHour) {
    this.updateHour = updateHour;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public String getCronExpr() {
    return cronExpr;
  }

  public void setCronExpr(String cronExpr) {
    this.cronExpr = cronExpr;
  }

  public String getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(String lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public String getUpdateStatus() {
    return updateStatus;
  }

  public void setUpdateStatus(String updateStatus) {
    this.updateStatus = updateStatus;
  }

  @Override
  public boolean equals(Object obj) {
    UserConnectionSource source = (UserConnectionSource) obj;
    return sourceId.equals(source.sourceId);
  }

  @Override
  public int hashCode() {
    String in = sourceId;
    return in.hashCode();
  }
}
