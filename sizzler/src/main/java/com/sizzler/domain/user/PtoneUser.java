package com.sizzler.domain.user;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneUser implements Serializable {

  private static final long serialVersionUID = -190953642571121517L;

  @PK
  private String ptId;
  private String userName;
  private String userEmail;
  private String userPassword;
  private String apiKey;
  private String createDate;
  private String access;
  private Integer loginActive;
  private String source;
  private String isPreRegistration;
  private Integer loginCount;
  private Integer facebookCount;
  private Integer twitterCount;
  private Integer totalCount;
  private String deleteDate;
  private String deleteUid;
  private String status;
  private String utmSource;
  private String utmCampaign;
  private String utmMedium;
  private String salesManager;
  private String accountManager;
  private String name;
  private String company;
  private String department;
  private String phone;
  private String phoneCountry;
  private String title;
  private String lastLoginDate;// 最近登录时间
  private String isFromSpaceInvitation;// 是不是通过邀请注册的 0:不是 1：是
  private Integer totalPasswordChanges;// 修改密码的次数
  private String isActivited;// 是否激活 0:未激活 1：已激活

  public String getIsActivited() {
    return isActivited;
  }

  public void setIsActivited(String isActivited) {
    this.isActivited = isActivited;
  }

  public String getLastLoginDate() {
    return lastLoginDate;
  }

  public void setLastLoginDate(String lastLoginDate) {
    this.lastLoginDate = lastLoginDate;
  }

  public String getIsFromSpaceInvitation() {
    return isFromSpaceInvitation;
  }

  public void setIsFromSpaceInvitation(String isFromSpaceInvitation) {
    this.isFromSpaceInvitation = isFromSpaceInvitation;
  }

  public Integer getTotalPasswordChanges() {
    return totalPasswordChanges;
  }

  public void setTotalPasswordChanges(Integer totalPasswordChanges) {
    this.totalPasswordChanges = totalPasswordChanges;
  }

  public String getPhoneCountry() {
    return phoneCountry;
  }

  public void setPhoneCountry(String phoneCountry) {
    this.phoneCountry = phoneCountry;
  }

  public String getUtmSource() {
    return utmSource;
  }

  public void setUtmSource(String utmSource) {
    this.utmSource = utmSource;
  }

  public String getUtmCampaign() {
    return utmCampaign;
  }

  public void setUtmCampaign(String utmCampaign) {
    this.utmCampaign = utmCampaign;
  }

  public String getUtmMedium() {
    return utmMedium;
  }

  public void setUtmMedium(String utmMedium) {
    this.utmMedium = utmMedium;
  }

  public String getSalesManager() {
    return salesManager;
  }

  public void setSalesManager(String salesManager) {
    this.salesManager = salesManager;
  }

  public String getAccountManager() {
    return accountManager;
  }

  public void setAccountManager(String accountManager) {
    this.accountManager = accountManager;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  private String activiteDate;

  public Integer getLoginCount() {
    return loginCount;
  }

  public void setLoginCount(Integer loginCount) {
    this.loginCount = loginCount;
  }

  public Integer getFacebookCount() {
    return facebookCount;
  }

  public void setFacebookCount(Integer facebookCount) {
    this.facebookCount = facebookCount;
  }

  public Integer getTwitterCount() {
    return twitterCount;
  }

  public void setTwitterCount(Integer twitterCount) {
    this.twitterCount = twitterCount;
  }

  public Integer getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  public String getIsPreRegistration() {
    return isPreRegistration;
  }

  public void setIsPreRegistration(String isPreRegistration) {
    this.isPreRegistration = isPreRegistration;
  }

  public String getAccess() {
    return access;
  }

  public void setAccess(String access) {
    this.access = access;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getCreateDate() {
    return createDate;
  }

  public void setCreateDate(String createDate) {
    this.createDate = createDate;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getPtId() {
    return ptId;
  }

  public void setPtId(String ptId) {
    this.ptId = ptId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public Integer getLoginActive() {
    return loginActive;
  }

  public void setLoginActive(Integer loginActive) {
    this.loginActive = loginActive;
  }

  public String getActiviteDate() {
    return activiteDate;
  }

  public void setActiviteDate(String activiteDate) {
    this.activiteDate = activiteDate;
  }

  public String getDeleteDate() {
    return deleteDate;
  }

  public void setDeleteDate(String deleteDate) {
    this.deleteDate = deleteDate;
  }

  public String getDeleteUid() {
    return deleteUid;
  }

  public void setDeleteUid(String deleteUid) {
    this.deleteUid = deleteUid;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
