package com.sizzler.service.basic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.basic.PtoneBasicDictCategoryDao;
import com.sizzler.domain.basic.PtoneBasicDictCategory;
import com.sizzler.service.basic.PtoneBasicDictCategoryService;

@Service("ptoneBasicDictCategoryService")
public class PtoneBasicDictCategoryServiceImpl extends
    ServiceBaseInterfaceImpl<PtoneBasicDictCategory, Long> implements PtoneBasicDictCategoryService {

  @Autowired
  private PtoneBasicDictCategoryDao ptoneBasicDictCategoryDao;

}
