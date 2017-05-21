package com.sizzler.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.PanelGlobalComponentDao;
import com.sizzler.domain.panel.PanelGlobalComponent;
import com.sizzler.service.PanelGlobalComponentService;

@Service("panelGlobalComponentService")
public class PanelGlobalComponentServiceImpl extends
    ServiceBaseInterfaceImpl<PanelGlobalComponent, Long> implements PanelGlobalComponentService {

  @Autowired
  private PanelGlobalComponentDao panelGlobalComponentDao;

  @Override
  public void cancelPanelComponent(String panelId) {
    panelGlobalComponentDao.cancelPanelComponent(panelId);
  }
}
