package com.sizzler.domain.ds.vo;

import java.io.Serializable;
import java.util.List;

import com.sizzler.system.Constants;

public class UserConnectionSourceVo implements Serializable {

  private static final long serialVersionUID = 3256408738962193740L;

  private List<UserConnectionSourceTableVo> table;
  private Long dsId;
  private String connectionId;
  private String sourceId;
  private Long uid;
  private String name;// fileName
  private String fileId;// fileId
  private String folderId;// folderId
  private Long lastModifiedDate;
  private Long updateTime;
  private Long createTime;
  private String operateType;
  private String remotePath;
  private String remoteStatus = Constants.validate;

  private String updateFrequency; // 更新频率， 值包括：
                                  // never（不更新）、hour（按小时更新）、day（按天更新）、monday（每周一更新，即按周更新）
  private String updateHour; // 更新的小时数，默认为每天5点更新，默认值为 5， 可选值（0-23）
  private String timezone; // 用户时区： +08:00 （中国）， +09:00（日本） ，
                           // +00:00（欧美），用户时区根据域名进行判断。
  private String lastUpdateTime; // 最近更新时间
  private String updateStatus; // 更新状态，包括：success、failed、waiting、updating

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

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getOperateType() {
    return operateType;
  }

  public void setOperateType(String operateType) {
    this.operateType = operateType;
  }

  public List<UserConnectionSourceTableVo> getTable() {
    return table;
  }

  public void setTable(List<UserConnectionSourceTableVo> table) {
    this.table = table;
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

  public Long getUid() {
    return uid;
  }

  public void setUid(Long uid) {
    this.uid = uid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public Long getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Long lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public Long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Long updateTime) {
    this.updateTime = updateTime;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
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

}
