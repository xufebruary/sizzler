package com.sizzler.domain.panel.dto;

import java.io.Serializable;

import com.sizzler.domain.panel.PtonePanelInfo;

public class UIPanel extends PtonePanelInfo implements Serializable {

  private static final long serialVersionUID = -1192537887607899023L;

  private Boolean isExistsPush = false;

  public Boolean getIsExistsPush() {
    return isExistsPush;
  }

  public void setIsExistsPush(Boolean isExistsPush) {
    this.isExistsPush = isExistsPush;
  }
}
