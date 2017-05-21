package com.sizzler.dao;

import com.sizzler.common.base.dao.DaoBaseInterface;
import com.sizzler.domain.panel.PtonePanelInfo;

public interface PanelDao extends DaoBaseInterface<PtonePanelInfo, String> {

  public abstract void updatePanelShareSourceStatus(String sharePanelId, String status);

}
