/**
 * Project Name:ptone-ui-backgroud File Name:GaWidgetServiceImpl.java Package
 * Name:com.ptmind.ptone.rest.service.impl Date:2015年4月20日下午4:55:34 Copyright (c) 2015,
 * peng.xu@ptthink.com All Rights Reserved.
 *
 */

package com.sizzler.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.GaWidgetDao;
import com.sizzler.domain.widget.GaWidgetInfo;
import com.sizzler.service.GaWidgetService;

/**
 * ClassName:GaWidgetServiceImpl <br/>
 * Date: 2015年4月20日 下午4:55:34 <br/>
 * 
 * @author peng.xu
 * @version
 * @since JDK 1.6
 * @see
 */
@Service("gaWidgetService")
public class GaWidgetServiceImpl extends ServiceBaseInterfaceImpl<GaWidgetInfo, String> implements
    GaWidgetService {

  @Autowired
  private GaWidgetDao gaWidgetDao;

  @Override
  public long queryWidgetCountOfAccount(String connectionId) {
    return gaWidgetDao.queryWidgetCountOfAccount(connectionId);
  }

  @Override
  public long queryWidgetCountOfSource(List<String> tableIdList) {
    return gaWidgetDao.queryWidgetCountOfSource(tableIdList);
  }

  /*
   * public GaWidgetInfo getGaWidget(String widgetId, String variableId) { return
   * gaWidgetDao.getGaWidget(widgetId, variableId); }
   */

}
