/**
 * Project Name:ptone-ui-backgroud File Name:GaWidgetDao.java Package Name:com.ptmind.ptone.rest.dao
 * Date:2015年4月20日下午4:38:52 Copyright (c) 2015, peng.xu@ptthink.com All Rights Reserved.
 *
 */

package com.sizzler.dao;

import java.util.List;

import com.sizzler.common.base.dao.DaoBaseInterface;
import com.sizzler.domain.widget.GaWidgetInfo;

public interface GaWidgetDao extends DaoBaseInterface<GaWidgetInfo, String> {

  public List<GaWidgetInfo> findGaWidgetInfo(String pid);

  public List<GaWidgetInfo> findGaWidgetInfoByWidgetId(List<String> widgetIdList);

  public long queryWidgetCountOfAccount(String connectionId);

  public long queryWidgetCountOfSource(List<String> tableIdList);

}
