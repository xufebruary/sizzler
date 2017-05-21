package com.sizzler.domain.user.vo;

import com.sizzler.domain.user.PtoneUser;

public class PtoneUserVo extends PtoneUser {

  private static final long serialVersionUID = -1875693246418813726L;

  private String userOldPassword;

  public String getUserOldPassword() {
    return userOldPassword;
  }

  public void setUserOldPassword(String userOldPassword) {
    this.userOldPassword = userOldPassword;
  }

}
