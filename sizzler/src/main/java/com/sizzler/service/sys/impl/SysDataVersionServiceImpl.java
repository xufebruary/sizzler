package com.sizzler.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.sys.SysDataVersionDao;
import com.sizzler.domain.sys.SysDataVersion;
import com.sizzler.service.sys.SysDataVersionService;

@Service("sysDataVersionService")
public class SysDataVersionServiceImpl extends ServiceBaseInterfaceImpl<SysDataVersion, Long>
    implements SysDataVersionService {

  @Autowired
  private SysDataVersionDao sysDataVersionDao;

}
