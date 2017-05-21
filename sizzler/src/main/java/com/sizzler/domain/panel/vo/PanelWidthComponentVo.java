package com.sizzler.domain.panel.vo;

import com.sizzler.domain.panel.PanelGlobalComponent;

/**
 * panel基本信息vo对象
 * 
 */
public class PanelWidthComponentVo extends PanelVo {

  private static final long serialVersionUID = -7969873869559618545L;

  private PanelGlobalComponent component;

  public PanelGlobalComponent getComponent() {
    return component;
  }

  public void setComponent(PanelGlobalComponent component) {
    this.component = component;
  }

}
