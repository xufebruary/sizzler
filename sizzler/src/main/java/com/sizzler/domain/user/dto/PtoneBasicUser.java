package com.sizzler.domain.user.dto;

import java.io.Serializable;

public class PtoneBasicUser implements Serializable {

  private static final long serialVersionUID = -7377265222512884438L;
  
  private String ptId;
  private String userName;
  private String userEmail;
  private String userPassword;
  private String apiKey;
  private int access;
  private String source;

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

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public int getAccess() {
    return access;
  }

  public void setAccess(int access) {
    this.access = access;
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
}
