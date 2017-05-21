/**
 * Project Name:ptone-ui-backgroud File Name:VariableDao.java Package Name:com.ptmind.ptone.rest.dao
 * Date:2015年4月20日下午4:44:19 Copyright (c) 2015, peng.xu@ptthink.com All Rights Reserved.
 *
 */

package com.sizzler.dao;

import java.util.List;

import com.sizzler.common.base.dao.DaoBaseInterface;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.variable.dto.PtoneVariableWithWidgetId;

public interface VariableDao extends DaoBaseInterface<PtoneVariableInfo, String> {

  public List<PtoneVariableWithWidgetId> findVariableByPanelId(String pid);

  public List<PtoneVariableWithWidgetId> findVariableByWidgetId(List<String> widgetIdList);

}
