package com.sizzler.service;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.widget.PtoneWidgetChartSetting;

public interface WidgetChartSettingService extends
    ServiceBaseInterface<PtoneWidgetChartSetting, String> {

  public void saveOrUpdate(PtoneWidgetChartSetting chartSetting);

}
