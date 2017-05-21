package com.sizzler.common.monitor.service;

import com.sizzler.common.monitor.jvm.domain.JvmMonitorInfo;


public interface MonitorStatService {

  /**
   * 持久化jvmMonitorInfo
   */
  public void saveJvmMonitorInfo(JvmMonitorInfo jvmMonitorInfo);

}
