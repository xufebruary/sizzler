/**
 * Project Name:ptone-ui-backgroud File Name:VariableServiceImpl.java Package
 * Name:com.ptmind.ptone.rest.service.impl Date:2015年4月20日下午4:55:48 Copyright (c) 2015,
 * peng.xu@ptthink.com All Rights Reserved.
 *
 */

package com.sizzler.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.VariableDao;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.service.VariableService;

@Service("variableService")
public class VariableServiceImpl extends ServiceBaseInterfaceImpl<PtoneVariableInfo, String>
    implements VariableService {

  @Autowired
  private VariableDao variableDao;

  /*
   * @Override public PtoneVariableInfo getVariable(String variableId) { return
   * variableDao.getVariable(variableId); }
   */


}
