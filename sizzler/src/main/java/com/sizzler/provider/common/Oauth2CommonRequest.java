package com.sizzler.provider.common;

import org.apache.metamodel.util.Oauth2Token;

public class Oauth2CommonRequest extends CommonRequest {

  private static final long serialVersionUID = -2078275569497363512L;

  protected Oauth2Token oauth2Token;

  public Oauth2Token getOauth2Token() {
    return oauth2Token;
  }

  public void setOauth2Token(Oauth2Token oauth2Token) {
    this.oauth2Token = oauth2Token;
  }

}
