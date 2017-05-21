package com.sizzler.service.ds.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.ds.PtoneDsInfoDao;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.service.ds.PtoneDsInfoService;

@Service("ptoneDsInfoService")
public class PtoneDsInfoServiceImpl extends ServiceBaseInterfaceImpl<PtoneDsInfo, Long> implements
    PtoneDsInfoService {

  @Autowired
  private PtoneDsInfoDao ptoneDsInfoDao;

}
