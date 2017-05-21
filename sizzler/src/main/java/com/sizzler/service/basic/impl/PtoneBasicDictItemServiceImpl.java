package com.sizzler.service.basic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.basic.PtoneBasicDictItemDao;
import com.sizzler.domain.basic.PtoneBasicDictItem;
import com.sizzler.service.basic.PtoneBasicDictItemService;

@Service("ptoneBasicDictItemService")
public class PtoneBasicDictItemServiceImpl extends
    ServiceBaseInterfaceImpl<PtoneBasicDictItem, Long> implements PtoneBasicDictItemService {

  @Autowired
  private PtoneBasicDictItemDao ptoneBasicDictItemDao;

}
