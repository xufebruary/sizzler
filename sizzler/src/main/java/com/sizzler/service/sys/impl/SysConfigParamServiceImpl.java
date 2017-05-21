package com.sizzler.service.sys.impl;

import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.domain.sys.SysConfigParam;
import com.sizzler.service.sys.SysConfigParamService;

@Service("sysConfigParamService")
public class SysConfigParamServiceImpl extends ServiceBaseInterfaceImpl<SysConfigParam, Long>
    implements SysConfigParamService {

}
