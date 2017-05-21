package com.sizzler.dao.sys.impl;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.sys.SysConfigParamDao;
import com.sizzler.domain.sys.SysConfigParam;

@Repository("sysConfigParamDao")
public class SysConfigParamDaoImpl extends DaoBaseInterfaceImpl<SysConfigParam, Long> implements
    SysConfigParamDao {

}
