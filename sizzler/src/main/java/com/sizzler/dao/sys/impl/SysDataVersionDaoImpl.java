package com.sizzler.dao.sys.impl;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.sys.SysDataVersionDao;
import com.sizzler.domain.sys.SysDataVersion;

@Repository("sysDataVersionDao")
public class SysDataVersionDaoImpl extends DaoBaseInterfaceImpl<SysDataVersion, Long> implements
    SysDataVersionDao {

}
