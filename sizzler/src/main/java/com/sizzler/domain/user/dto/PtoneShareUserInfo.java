package com.sizzler.domain.user.dto;

import java.io.Serializable;

public class PtoneShareUserInfo implements Serializable {

  private static final long serialVersionUID = 5574864286637744984L;

  private String ptId;
  private String userName;
  private String weekStart;
  private String locale;

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

}
