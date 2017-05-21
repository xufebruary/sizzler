package com.sizzler.domain.user.dto;

import com.sizzler.domain.user.PtoneUser;

public class SignInUser extends PtoneUser {

  private static final long serialVersionUID = 8752152905491014525L;

  private Boolean rememberMe = false;

  public Boolean getRememberMe() {
    return rememberMe;
  }

  public void setRememberMe(Boolean rememberMe) {
    this.rememberMe = rememberMe;
  }
}
