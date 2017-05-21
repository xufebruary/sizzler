package com.sizzler.domain.panel.dto;

import java.io.Serializable;

import com.sizzler.domain.panel.PtonePanelLayout;

/**
 * 用户信息扩展，包含PanelInfo中的所有字段
 * @author you.zou
 *
 */
public class PanelInfoExt implements Serializable {
  
  private static final long serialVersionUID = 4815385623684142259L;
  
  public static final String PANEL_MODEL_READ = "READ";

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
  private String shareSourceStatus;// 来源panel状态 0：已删除 1：已关闭分享 2: 正常
  private String shareSourceUsername;// 分享panel的用户名（不是email）
  private String model;
  private String spaceId;
  private String description;
  private String templetId;
  
  private String type; // panel || container
  
  private String[] pids; // 批量删除panel时使用的panel_id列表

  private boolean notUpdatePanelLayout; // 标记不需要更新panel的Layout信息，默认需要
  
  private PtonePanelLayout ptonePanelLayout;//

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public String getShareSourceId() {
    return shareSourceId;
  }

  public void setShareSourceId(String shareSourceId) {
    this.shareSourceId = shareSourceId;
  }

  public String getShareSourceStatus() {
    return shareSourceStatus;
  }

  public void setShareSourceStatus(String shareSourceStatus) {
    this.shareSourceStatus = shareSourceStatus;
  }

  public String getShareSourceUsername() {
    return shareSourceUsername;
  }

  public void setShareSourceUsername(String shareSourceUsername) {
    this.shareSourceUsername = shareSourceUsername;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }
  
  public String getTempletId() {
    return templetId;
  }

  public void setTempletId(String templetId) {
    this.templetId = templetId;
  }

  public String[] getPids() {
    return pids;
  }

  public void setPids(String[] pids) {
    this.pids = pids;
  }

  public PtonePanelLayout getPtonePanelLayout() {
    return ptonePanelLayout;
  }

  public void setPtonePanelLayout(PtonePanelLayout ptonePanelLayout) {
    this.ptonePanelLayout = ptonePanelLayout;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isNotUpdatePanelLayout() {
    return notUpdatePanelLayout;
  }

  public void setNotUpdatePanelLayout(boolean notUpdatePanelLayout) {
    this.notUpdatePanelLayout = notUpdatePanelLayout;
  }
  
}
