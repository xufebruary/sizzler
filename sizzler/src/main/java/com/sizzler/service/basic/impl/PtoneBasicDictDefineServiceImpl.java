package com.sizzler.service.basic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.basic.PtoneBasicDictDefineDao;
import com.sizzler.domain.basic.PtoneBasicDictDefine;
import com.sizzler.service.basic.PtoneBasicDictDefineService;

@Service("ptoneBasicDictDefineService")
public class PtoneBasicDictDefineServiceImpl extends
    ServiceBaseInterfaceImpl<PtoneBasicDictDefine, Long> implements PtoneBasicDictDefineService {

  @Autowired
  private PtoneBasicDictDefineDao ptoneBasicDictDefineDao;

}
