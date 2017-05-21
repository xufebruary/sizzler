package com.sizzler.service;

import java.util.List;

import com.sizzler.domain.basic.dto.PtoneBasicChartInfoDto;

public interface PtoneBasicService {

  public List<PtoneBasicChartInfoDto> getPtoneBasicChartInfoListByType(String type);

  public List<PtoneBasicChartInfoDto> getPtoneBasicChartInfoList();

}
