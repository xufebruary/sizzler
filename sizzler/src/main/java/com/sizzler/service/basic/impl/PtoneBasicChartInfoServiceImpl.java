package com.sizzler.service.basic.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.basic.PtoneBasicChartInfoDao;
import com.sizzler.domain.basic.PtoneBasicChartInfo;
import com.sizzler.service.basic.PtoneBasicChartInfoService;

@Service("ptoneBasicChartInfoService")
public class PtoneBasicChartInfoServiceImpl extends
    ServiceBaseInterfaceImpl<PtoneBasicChartInfo, Long> implements PtoneBasicChartInfoService {

  @Autowired
  private PtoneBasicChartInfoDao ptoneBasicChartInfoDao;

  @Override
  public List<Map<String, Object>> findChartInfoMap() {
    return ptoneBasicChartInfoDao.findChartInfoMap();
  }
}
