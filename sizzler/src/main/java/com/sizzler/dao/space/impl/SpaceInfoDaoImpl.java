package com.sizzler.dao.space.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.dao.space.SpaceInfoDao;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.space.PtoneRetainDomain;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.system.Constants;

@Repository("spaceInfoDao")
public class SpaceInfoDaoImpl extends DaoBaseInterfaceImpl<PtoneSpaceInfo, String> implements
    SpaceInfoDao {

  @Override
  public PtoneSpaceInfo get(String spaceId) {
    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("spaceId", new Object[] {spaceId});
    paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
    return getByWhere(paramMap);
  }

  @Override
  public int countSpacePanel(String spaceId) {
    String sql = "SELECT COUNT(*) FROM ptone_panel_info WHERE space_id = ? AND STATUS = ?";
    Object param[] = {spaceId, Constants.validate};
    int panelNum = DataOperationUtils.queryForObject(sql, param, Integer.class);
    return panelNum;
  }

  @Override
  public List<PtoneRetainDomain> getRetainDomainList() {
    String sql = "SELECT * FROM ptone_retain_domain WHERE status = :status ";
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("status", Constants.validate);
    return (List<PtoneRetainDomain>) DataOperationUtils.queryForList(sql, paramMap,
        PtoneRetainDomain.class);
  }

  @Override
  public List<PtonePanelInfo> getPanelBySpaceId(String spaceId) {
    String sql =
        "SELECT p.* FROM ptone_panel_info p WHERE p.space_id = :spaceId";
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("spaceId", spaceId);
    return (List<PtonePanelInfo>) DataOperationUtils.queryForList(sql, paramMap,PtonePanelInfo.class);
  }
}
