package com.sizzler.service.impl;

import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.domain.widget.PtoneWidgetChartSetting;
import com.sizzler.service.WidgetChartSettingService;

@Service("widgetChartSettingService")
public class WidgetChartSettingServiceImpl extends
    ServiceBaseInterfaceImpl<PtoneWidgetChartSetting, String> implements WidgetChartSettingService {

  @Override
  public void saveOrUpdate(PtoneWidgetChartSetting chartSetting) {
    if (chartSetting != null && chartSetting.getWidgetId() != null
        && !"".equals(chartSetting.getWidgetId())) {
      PtoneWidgetChartSetting setting = this.get(chartSetting.getWidgetId());
      if (setting == null) {
        this.save(chartSetting);
      } else {
        this.update(chartSetting);
      }
    }
  }

}
