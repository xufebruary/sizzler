package com.sizzler.dao.ds.impl;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.ds.PtoneDsInfoDao;
import com.sizzler.domain.ds.PtoneDsInfo;

@Repository("ptoneDsInfoDao")
public class PtoneDsInfoDaoImpl extends DaoBaseInterfaceImpl<PtoneDsInfo, Long> implements
    PtoneDsInfoDao {

}
