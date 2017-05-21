package com.sizzler.service;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.panel.PanelGlobalComponent;

public interface PanelGlobalComponentService extends
    ServiceBaseInterface<PanelGlobalComponent, Long> {

  public abstract void cancelPanelComponent(String panelId);

}
