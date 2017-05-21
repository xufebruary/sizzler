package com.sizzler.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.dao.PanelDao;
import com.sizzler.domain.panel.PtonePanelInfo;

@Repository("panelDao")
public class PanelDaoImpl extends DaoBaseInterfaceImpl<PtonePanelInfo, String> implements PanelDao {

  @Override
  public PtonePanelInfo get(String panelId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("panelId", new Object[] {panelId});
    return this.getByWhere(paramMap);
  }

  @Override
  public void updatePanelShareSourceStatus(String sharePanelId, String status) {
    String sql = "UPDATE ptone_panel_info SET share_source_status = ? WHERE share_source_id = ?";
    Object param[] = {status, sharePanelId};
    DataOperationUtils.insert(sql, param);
  }
}
