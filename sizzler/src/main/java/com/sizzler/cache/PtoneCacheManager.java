package com.sizzler.cache;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sizzler.service.space.impl.SpaceInfoServiceImpl;

/**
 * 管理当前内存中的cache
 * 
 * @author peng.xu
 * 
 */
@Component("ptoneCacheManager")
public class PtoneCacheManager {

  @Autowired
  private SysConfigParamCache sysConfigParamCache;

  @Autowired
  private PtoneBasicDictCache ptoneBasicDictCache;

  @Autowired
  private PtoneBasicChartInfoCache ptoneBasicChartInfoCache;

  @Autowired
  private PtoneDsInfoCache ptoneDsInfoCache;


  // ///////////////////////////////////////////////////////

  public String refreshMemeryCache() {
    this.sysConfigParamCache.init();
    this.ptoneBasicDictCache.init();
    this.ptoneBasicChartInfoCache.init();
    this.ptoneDsInfoCache.init();
    SpaceInfoServiceImpl.setRetainDomainRefresh(true);

    String refreshHost = "";
    try {
      // 获取本机IP
      refreshHost = InetAddress.getLocalHost().getHostAddress().toString();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    return refreshHost;
  }

  // ///////////////////////////////////////////////////////

  public SysConfigParamCache getSysConfigParamCache() {
    return sysConfigParamCache;
  }

  public void setSysConfigParamCache(SysConfigParamCache sysConfigParamCache) {
    this.sysConfigParamCache = sysConfigParamCache;
  }

  public PtoneBasicDictCache getPtoneBasicDictCache() {
    return ptoneBasicDictCache;
  }

  public void setPtoneBasicDictCache(PtoneBasicDictCache ptoneBasicDictCache) {
    this.ptoneBasicDictCache = ptoneBasicDictCache;
  }

  public PtoneBasicChartInfoCache getPtoneBasicChartInfoCache() {
    return ptoneBasicChartInfoCache;
  }

  public void setPtoneBasicChartInfoCache(PtoneBasicChartInfoCache ptoneBasicChartInfoCache) {
    this.ptoneBasicChartInfoCache = ptoneBasicChartInfoCache;
  }

  public PtoneDsInfoCache getPtoneDsInfoCache() {
    return ptoneDsInfoCache;
  }

  public void setPtoneDsInfoCache(PtoneDsInfoCache ptoneDsInfoCache) {
    this.ptoneDsInfoCache = ptoneDsInfoCache;
  }

}
