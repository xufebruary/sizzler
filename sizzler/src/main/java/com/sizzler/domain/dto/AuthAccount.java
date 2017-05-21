package com.sizzler.domain.dto;

import java.io.Serializable;

public class AuthAccount implements Serializable {

  private static final long serialVersionUID = 8629049957086435649L;

  private String accountName;
  private String ptId;

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public String getPtId() {
    return ptId;
  }

  public void setPtId(String ptId) {
    this.ptId = ptId;
  }
}
