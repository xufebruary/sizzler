package com.sizzler.domain.panel.vo;

import java.io.Serializable;

/**
 * panel基本信息vo对象
 */
public class PanelVo implements Serializable {

  private static final long serialVersionUID = -6259857559243010913L;

  private String panelId;
  private String panelTitle;
  private String spaceId;

  public String getPanelId() {
    return panelId;
  }

  public void setPanelId(String panelId) {
    this.panelId = panelId;
  }

  public String getPanelTitle() {
    return panelTitle;
  }

  public void setPanelTitle(String panelTitle) {
    this.panelTitle = panelTitle;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

}
