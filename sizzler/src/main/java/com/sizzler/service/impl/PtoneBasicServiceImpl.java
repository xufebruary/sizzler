package com.sizzler.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.cache.PtoneBasicChartInfoCache;
import com.sizzler.domain.basic.dto.PtoneBasicChartInfoDto;
import com.sizzler.service.PtoneBasicService;

@Service("ptoneBasicService")
public class PtoneBasicServiceImpl implements PtoneBasicService {

  @Autowired
  private PtoneBasicChartInfoCache ptoneBasicChartInfoCache;

  public List<PtoneBasicChartInfoDto> getPtoneBasicChartInfoListByType(String type) {
    return ptoneBasicChartInfoCache.getPtoneBasicChartInfoListByType(type);
  }

  public List<PtoneBasicChartInfoDto> getPtoneBasicChartInfoList() {
    return ptoneBasicChartInfoCache.getPtoneBasicChartInfoList();
  }

}
