package com.sizzler.provider.common.query;

/**
 * Created by ptmind on 2015/11/11.
 */
public class ConnectionInfo {
  private String driverClass;
  private String url;
  private String user;
  private String password;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDriverClass() {
    return driverClass;
  }

  public void setDriverClass(String driverClass) {
    this.driverClass = driverClass;
  }
}
