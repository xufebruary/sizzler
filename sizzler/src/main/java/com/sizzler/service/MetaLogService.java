package com.sizzler.service;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.sys.SysMetaLog;


public interface MetaLogService extends ServiceBaseInterface<SysMetaLog, Long> {
  public abstract void processMetaLog(SysMetaLog sysMetaLog);
}
