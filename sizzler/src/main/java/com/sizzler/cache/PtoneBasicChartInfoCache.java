package com.sizzler.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sizzler.domain.basic.PtoneBasicChartInfo;
import com.sizzler.domain.basic.dto.PtoneBasicChartInfoDto;
import com.sizzler.service.basic.PtoneBasicChartInfoService;

/**
 * 缓存图表类型
 * 
 * @author peng.xu
 * 
 */
@Component("ptoneBasicChartInfoCache")
public class PtoneBasicChartInfoCache {

  @Autowired
  private PtoneBasicChartInfoService ptoneBasicChartInfoService;

  private static Map<String, PtoneBasicChartInfoDto> cacheMap;

  private static List<PtoneBasicChartInfoDto> cacheList;

  private static Map<String, List<PtoneBasicChartInfoDto>> cacheListByType;

  @PostConstruct
  public void init() {
    Map<String, PtoneBasicChartInfoDto> newCacheMap =
        new LinkedHashMap<String, PtoneBasicChartInfoDto>();
    List<PtoneBasicChartInfoDto> newCacheList = new ArrayList<PtoneBasicChartInfoDto>();
    Map<String, List<PtoneBasicChartInfoDto>> newCacheListByType =
        new LinkedHashMap<String, List<PtoneBasicChartInfoDto>>();

    List<PtoneBasicChartInfo> chartInfoList = ptoneBasicChartInfoService.findAll();
    for (PtoneBasicChartInfo chartInfo : chartInfoList) {
      PtoneBasicChartInfoDto chartInfoDto = new PtoneBasicChartInfoDto(chartInfo);
      newCacheList.add(chartInfoDto);
      newCacheMap.put(chartInfo.getCode().toLowerCase(), chartInfoDto);
      String type = chartInfo.getType().toLowerCase();
      if (newCacheListByType.containsKey(type)) {
        newCacheListByType.get(type).add(chartInfoDto);
      } else {
        List<PtoneBasicChartInfoDto> list = new ArrayList<PtoneBasicChartInfoDto>();
        list.add(chartInfoDto);
        newCacheListByType.put(type, list);
      }
    }

    cacheMap = newCacheMap;
    cacheList = newCacheList;
    cacheListByType = newCacheListByType;
  }

  public List<PtoneBasicChartInfoDto> getPtoneBasicChartInfoList() {
    return cacheList;
  }

  public List<PtoneBasicChartInfoDto> getPtoneBasicChartInfoListByType(String type) {
    return cacheListByType.get(type.toLowerCase());
  }

  public PtoneBasicChartInfoDto getPtoneBasicChartInfoByCode(String chartCode) {
    return cacheMap.get(chartCode.toLowerCase());
  }

  public PtoneBasicChartInfoDto getPtoneBasicChartInfoById(long id) {
    for (PtoneBasicChartInfoDto chartInfoDto : cacheList) {
      if (chartInfoDto.getId() == id) {
        return chartInfoDto;
      }
    }
    return null;
  }

  public String getNameByChartCode(String chartCode) {
    String name = "";
    PtoneBasicChartInfoDto chartInfoDto =
        this.getPtoneBasicChartInfoByCode(chartCode.toLowerCase());
    if (chartInfoDto != null) {
      name = chartInfoDto.getName();
    }
    return name;
  }

  public String getDataTypeByChartCode(String chartCode) {
    String dataType = "";
    PtoneBasicChartInfoDto chartInfoDto =
        this.getPtoneBasicChartInfoByCode(chartCode.toLowerCase());
    if (chartInfoDto != null) {
      dataType = chartInfoDto.getDataType();
    }
    return dataType;
  }

}
