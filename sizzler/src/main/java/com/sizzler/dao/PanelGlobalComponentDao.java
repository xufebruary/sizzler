package com.sizzler.dao;

import com.sizzler.common.base.dao.DaoBaseInterface;
import com.sizzler.domain.panel.PanelGlobalComponent;

public interface PanelGlobalComponentDao extends DaoBaseInterface<PanelGlobalComponent, Long> {
  public abstract void cancelPanelComponent(String panelId);
}
