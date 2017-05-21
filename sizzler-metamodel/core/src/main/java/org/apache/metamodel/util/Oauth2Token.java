package org.apache.metamodel.util;

import java.io.Serializable;

/**
 * Created by ptmind on 2015/12/3.
 */
public class Oauth2Token implements Serializable {
  private String refreshToken;
  private String accessToken;
  private String clientId;
  private String clientSecret;

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }
}
