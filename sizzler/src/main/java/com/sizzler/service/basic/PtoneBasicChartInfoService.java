package com.sizzler.service.basic;

import java.util.List;
import java.util.Map;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.basic.PtoneBasicChartInfo;

public interface PtoneBasicChartInfoService extends ServiceBaseInterface<PtoneBasicChartInfo, Long> {

  public List<Map<String, Object>> findChartInfoMap();

}
