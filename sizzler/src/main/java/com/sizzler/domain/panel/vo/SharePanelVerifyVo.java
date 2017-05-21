package com.sizzler.domain.panel.vo;

import java.io.Serializable;

public class SharePanelVerifyVo implements Serializable {

  private static final long serialVersionUID = 488471289842219816L;

  private String dashboardId;
  private String password;


  public String getDashboardId() {
    return dashboardId;
  }

  public void setDashboardId(String dashboardId) {
    this.dashboardId = dashboardId;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

}
