package com.sizzler.provider.common.query;

import java.io.Serializable;

/**
 * Created by ptmind on 2015/11/11.
 */
public class DataBaseDriverInfo implements Serializable {

  private static final long serialVersionUID = -2420721382042490780L;

  private String driver;
  private String urlPrefix;
  private String urlParam;
  private String defaultPort;

  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public String getUrlPrefix() {
    return urlPrefix;
  }

  public void setUrlPrefix(String urlPrefix) {
    this.urlPrefix = urlPrefix;
  }

  public String getDefaultPort() {
    return defaultPort;
  }

  public void setDefaultPort(String defaultPort) {
    this.defaultPort = defaultPort;
  }

  public String getUrlParam() {
    return urlParam;
  }

  public void setUrlParam(String urlParam) {
    this.urlParam = urlParam;
  }

}
