package com.sizzler.domain.panel.dto;

import com.sizzler.domain.panel.PanelGlobalComponent;
import com.sizzler.domain.panel.PtonePanelInfo;

import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName: PanelInfoWithComponents
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2016/3/1
 * @author: zhangli
 */
public class PanelInfoWithComponents extends PtonePanelInfo implements Serializable {

  private Map<String, PanelGlobalComponent> components;

  public Map<String, PanelGlobalComponent> getComponents() {
    return components;
  }

  public void setComponents(Map<String, PanelGlobalComponent> components) {
    this.components = components;
  }
}
