package com.sizzler.domain.panel;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.Ignore;
import com.sizzler.dexcoder.annotation.PK;

public class PtonePanelInfo implements Serializable {

  private static final long serialVersionUID = 4435959335870049795L;

  public static final String PANEL_STATUS_VALIDATE = "validate";
  public static final String PANEL_STATUS_PANEL_DELETE = "panelDelete";
  public static final String PANEL_STATUS_SHARE_OFF = "panelShareOff";
  public static final String PANEL_STATUS_SHARE_PSWD_ERROR = "passwordError";
  public static final String PANEL_STATUS_SPACE_DELETE = "spaceDelete";

  public static final String PANEL_STATUS_EXISTS = "panelExists";
  public static final String PANEL_STATUS_NOT_EXIST = "panelNotExists"; // panel不存在
  public static final String PANEL_STATUS_AVAILABLE = "available"; // panel有效且有权限访问

  public static final String SHARE_SOURCE_STATUS_SPACE_DELETE = "3";

  @PK
  private String panelId;
  private String panelTitle;
  private String status;
  private String creatorId;
  private Long createTime;
  private String layout;
  private String panelWidth;
  private Integer orderNumber;
  private String shareTeam; // team分享状态
  private String shareUrl; // url分享状态
  private String sharePassword; // 分享密码
  private String shareDate; // 分享日期
  private String globalComponentStatus;
  private String access;// panel 操作权限
  private String shareSourceId;// 面板分享的来源panelid
  private String shareSourceStatus;// 来源panel状态 0：已删除 1：已关闭分享 2: 正常 3:空间已删除
  private String shareSourceUsername;// 分享panel的用户名（不是email）
  private String model;
  private String spaceId;
  private String description;
  private String templetId;
  private String isByTemplet; // 1: 根据模板创建， 0：用户创建
  private String isByDefault; // 1: 预制panel， 0： 非预制panel
  private String sourceType; // 来源类型：手动创建（USER_CREATED）、预制Panel模板（DEFAULT_TEMPLET）、Panel模板（PANEL_TEMPLET）

  @Ignore
  private String spaceName; // 冗余字段

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getShareSourceUsername() {
    return shareSourceUsername;
  }

  public void setShareSourceUsername(String shareSourceUsername) {
    this.shareSourceUsername = shareSourceUsername;
  }

  public String getShareSourceStatus() {
    return shareSourceStatus;
  }

  public void setShareSourceStatus(String shareSourceStatus) {
    this.shareSourceStatus = shareSourceStatus;
  }

  public String getShareSourceId() {
    return shareSourceId;
  }

  public void setShareSourceId(String shareSourceId) {
    this.shareSourceId = shareSourceId;
  }

  public String getGlobalComponentStatus() {
    return globalComponentStatus;
  }

  public void setGlobalComponentStatus(String globalComponentStatus) {
    this.globalComponentStatus = globalComponentStatus;
  }

  public String getAccess() {
    return access;
  }

  public void setAccess(String access) {
    this.access = access;
  }

  public String getShareTeam() {
    return shareTeam;
  }

  public void setShareTeam(String shareTeam) {
    this.shareTeam = shareTeam;
  }

  public String getShareUrl() {
    return shareUrl;
  }

  public void setShareUrl(String shareUrl) {
    this.shareUrl = shareUrl;
  }

  public String getSharePassword() {
    return sharePassword;
  }

  public void setSharePassword(String sharePassword) {
    this.sharePassword = sharePassword;
  }

  public String getShareDate() {
    return shareDate;
  }

  public void setShareDate(String shareDate) {
    this.shareDate = shareDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getPanelId() {
    return panelId;
  }

  public void setPanelId(String panelId) {
    this.panelId = panelId;
  }

  public String getPanelTitle() {
    return panelTitle;
  }

  public void setPanelTitle(String panelTitle) {
    this.panelTitle = panelTitle;
  }

  public String getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public String getLayout() {
    return layout;
  }

  public void setLayout(String layout) {
    this.layout = layout;
  }

  public String getPanelWidth() {
    return panelWidth;
  }

  public void setPanelWidth(String panelWidth) {
    this.panelWidth = panelWidth;
  }

  public Integer getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(Integer orderNumber) {
    this.orderNumber = orderNumber;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public String getSpaceName() {
    return spaceName;
  }

  public void setSpaceName(String spaceName) {
    this.spaceName = spaceName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTempletId() {
    return templetId;
  }

  public void setTempletId(String templetId) {
    this.templetId = templetId;
  }

  public String getIsByTemplet() {
    return isByTemplet;
  }

  public void setIsByTemplet(String isByTemplet) {
    this.isByTemplet = isByTemplet;
  }

  public String getIsByDefault() {
    return isByDefault;
  }

  public void setIsByDefault(String isByDefault) {
    this.isByDefault = isByDefault;
  }

  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

}
