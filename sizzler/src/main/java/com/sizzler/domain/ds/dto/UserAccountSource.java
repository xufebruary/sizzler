package com.sizzler.domain.ds.dto;

import java.io.Serializable;

import com.sizzler.domain.ds.UserConnectionSource;

public class UserAccountSource extends UserConnectionSource implements Serializable {

  private static final long serialVersionUID = 4421634021935627416L;

  private String accountName;

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

}
