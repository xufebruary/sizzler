package com.sizzler.domain.session;

import com.sizzler.domain.user.PtoneUser;

import java.io.Serializable;

public class PTSession implements Serializable {

  private static final long serialVersionUID = -3077548977699774147L;

  public String pUserId;
  public PtoneUser user;
  public long pSessionId;
  public long pSessionTime;
  public boolean pNewUser = false;
  public String pIP = null;
  public String pCountry = null;
  public String pUA = null;
  public String currentPage = null;

  public PTSession() {}

  public String getpUserId() {
    return pUserId;
  }

  public void setpUserId(String pUserId) {
    this.pUserId = pUserId;
  }

  public PtoneUser getUser() {
    return user;
  }

  public void setUser(PtoneUser user) {
    this.user = user;
  }

  public long getpSessionId() {
    return pSessionId;
  }

  public void setpSessionId(long pSessionId) {
    this.pSessionId = pSessionId;
  }

  public long getpSessionTime() {
    return pSessionTime;
  }

  public void setpSessionTime(long pSessionTime) {
    this.pSessionTime = pSessionTime;
  }

  public boolean ispNewUser() {
    return pNewUser;
  }

  public void setpNewUser(boolean pNewUser) {
    this.pNewUser = pNewUser;
  }

  public String getpIP() {
    return pIP;
  }

  public void setpIP(String pIP) {
    this.pIP = pIP;
  }

  public String getpCountry() {
    return pCountry;
  }

  public void setpCountry(String pCountry) {
    this.pCountry = pCountry;
  }

  public String getpUA() {
    return pUA;
  }

  public void setpUA(String pUA) {
    this.pUA = pUA;
  }

  public String getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(String currentPage) {
    this.currentPage = currentPage;
  }
}
