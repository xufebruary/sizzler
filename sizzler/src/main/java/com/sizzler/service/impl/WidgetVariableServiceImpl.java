/**
 * Project Name:ptone-ui-backgroud File Name:WidgetVariableServiceImpl.java Package
 * Name:com.ptmind.ptone.rest.service.impl Date:2015年4月20日下午4:56:16 Copyright (c) 2015,
 * peng.xu@ptthink.com All Rights Reserved.
 *
 */

package com.sizzler.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.WidgetVariableDao;
import com.sizzler.domain.widget.PtoneWidgetVariable;
import com.sizzler.service.WidgetVariableService;

/**
 * ClassName:WidgetVariableServiceImpl <br/>
 * Date: 2015年4月20日 下午4:56:16 <br/>
 * 
 * @author peng.xu
 * @version
 * @since JDK 1.6
 * @see
 */
@Service("widgetVariableService")
public class WidgetVariableServiceImpl extends
    ServiceBaseInterfaceImpl<PtoneWidgetVariable, String> implements WidgetVariableService {

  @Autowired
  private WidgetVariableDao widgetVariableDao;

  /*
   * @Override public List<PtoneWidgetVariable> findWidgetVariableByWidget(String widgetId) { return
   * widgetVariableDao.findWidgetVariableByWidget(widgetId); }
   * 
   * @Override public PtoneWidgetVariable getWidgetVariable(String widgetId, String variableId) {
   * return widgetVariableDao.getWidgetVariable(widgetId, variableId); }
   */

}
