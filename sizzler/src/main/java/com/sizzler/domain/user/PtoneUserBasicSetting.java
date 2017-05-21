package com.sizzler.domain.user;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneUserBasicSetting implements Serializable {

  private static final long serialVersionUID = 971206443195223968L;

  private String id;
  @PK
  private String ptId;
  private String weekStart;
  private String locale;
  private String profileSelected; // （用户）选择profile信息的配置
  private String userSelected; // （用户）选择信息的配置
  private String showTips;
  private String demoSwitch;
  private String hideOnboarding;// 前端停用
  private String viewOnboarding;// 是否走新流程 0：不走 1：走
  private String spaceSelected;// 用户当前所选择的空间的domain

  public String getSpaceSelected() {
    return spaceSelected;
  }

  public void setSpaceSelected(String spaceSelected) {
    this.spaceSelected = spaceSelected;
  }

  public String getViewOnboarding() {
    return viewOnboarding;
  }

  public void setViewOnboarding(String viewOnboarding) {
    this.viewOnboarding = viewOnboarding;
  }

  public String getDemoSwitch() {
    return demoSwitch;
  }

  public void setDemoSwitch(String demoSwitch) {
    this.demoSwitch = demoSwitch;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPtId() {
    return ptId;
  }

  public void setPtId(String ptId) {
    this.ptId = ptId;
  }

  public String getWeekStart() {
    return weekStart;
  }

  public void setWeekStart(String weekStart) {
    this.weekStart = weekStart;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getProfileSelected() {
    return profileSelected;
  }

  public void setProfileSelected(String profileSelected) {
    this.profileSelected = profileSelected;
  }

  public String getUserSelected() {
    return userSelected;
  }

  public void setUserSelected(String userSelected) {
    this.userSelected = userSelected;
  }

  public String getShowTips() {
    return showTips;
  }

  public void setShowTips(String showTips) {
    this.showTips = showTips;
  }

  public String getHideOnboarding() {
    return hideOnboarding;
  }

  public void setHideOnboarding(String hideOnboarding) {
    this.hideOnboarding = hideOnboarding;
  }

}
