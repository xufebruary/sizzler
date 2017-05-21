package com.sizzler.domain.space.vo;

import java.io.Serializable;

public class SpacePanelVo implements Serializable {

  private static final long serialVersionUID = -889138415146766157L;

  private String domain; // 空间domain
  private String panelId;

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getPanelId() {
    return panelId;
  }

  public void setPanelId(String panelId) {
    this.panelId = panelId;
  }



}
