package com.sizzler.dao.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.WidgetChartSettingDao;
import com.sizzler.domain.widget.PtoneWidgetChartSetting;

@Repository("widgetChartSettingDao")
public class WidgetChartSettingDaoImpl extends
    DaoBaseInterfaceImpl<PtoneWidgetChartSetting, String> implements WidgetChartSettingDao {

  @Override
  public PtoneWidgetChartSetting get(String id) {
    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("widgetId", new Object[] {id});
    return getByWhere(paramMap);
  }

}
