package com.sizzler.dao.space;

import java.util.List;

import com.sizzler.common.base.dao.DaoBaseInterface;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.space.PtoneRetainDomain;
import com.sizzler.domain.space.PtoneSpaceInfo;

public interface SpaceInfoDao extends DaoBaseInterface<PtoneSpaceInfo, String> {

  /**
   * 统计某个空间下有多少个panel
   * @param spaceId
   * @author li.zhang
   */
  public int countSpacePanel(String spaceId);

  public List<PtoneRetainDomain> getRetainDomainList();

  public List<PtonePanelInfo> getPanelBySpaceId(String spaceId);

}
