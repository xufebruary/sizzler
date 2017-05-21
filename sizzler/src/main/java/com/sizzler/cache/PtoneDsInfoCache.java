package com.sizzler.cache;

import java.util.*;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.service.ds.PtoneDsInfoService;
import com.sizzler.system.Constants;

/**
 * 缓存数据源信息
 * @author peng.xu
 *
 */
@Component("ptoneDsInfoCache")
public class PtoneDsInfoCache {

  @Autowired
  private PtoneDsInfoService ptoneDsInfoService;

  private static Map<String, PtoneDsInfo> cacheMap;

  private static List<PtoneDsInfo> cacheList;

  @PostConstruct
  public void init() {
    Map<String, PtoneDsInfo> newCacheMap = new LinkedHashMap<String, PtoneDsInfo>();
    List<PtoneDsInfo> newCacheList = new ArrayList<PtoneDsInfo>();

    newCacheList = ptoneDsInfoService.findAll();
    for (PtoneDsInfo dsInfo : newCacheList) {
      newCacheMap.put(dsInfo.getCode(), dsInfo);
    }

    cacheMap = newCacheMap;
    cacheList = newCacheList;
  }

  public List<PtoneDsInfo> getPtoneDsInfoList() {
    return cacheList;
  }

  public PtoneDsInfo getPtoneDsInfoByCode(String dsCode) {
    return cacheMap.get(dsCode);
  }

  public PtoneDsInfo getPtoneDsInfoById(long id) {
    for (PtoneDsInfo dsInfo : cacheList) {
      if (dsInfo.getId() == id) {
        return dsInfo;
      }
    }
    return null;
  }

}
