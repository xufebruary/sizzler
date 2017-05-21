package com.sizzler.dao.basic;

import java.util.List;
import java.util.Map;

import com.sizzler.common.base.dao.DaoBaseInterface;
import com.sizzler.domain.basic.PtoneBasicChartInfo;

public interface PtoneBasicChartInfoDao extends DaoBaseInterface<PtoneBasicChartInfo, Long> {

  public List<Map<String, Object>> findChartInfoMap();

}
