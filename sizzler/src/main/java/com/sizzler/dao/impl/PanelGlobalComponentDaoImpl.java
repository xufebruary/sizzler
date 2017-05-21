package com.sizzler.dao.impl;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.dao.PanelGlobalComponentDao;
import com.sizzler.domain.panel.PanelGlobalComponent;
import com.sizzler.system.Constants;

@Repository("panelGlobalComponentDao")
public class PanelGlobalComponentDaoImpl extends DaoBaseInterfaceImpl<PanelGlobalComponent, Long>
    implements PanelGlobalComponentDao {

  @Override
  public void cancelPanelComponent(String panelId) {
    String sql = "UPDATE panel_global_component SET status = ? WHERE panel_id = ?";
    Object param[] = {Constants.inValidate,panelId};
    DataOperationUtils.insert(sql, param);
  }
}
