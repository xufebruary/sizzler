package com.sizzler.service;

import java.util.List;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.widget.GaWidgetInfo;

public interface GaWidgetService extends ServiceBaseInterface<GaWidgetInfo, String> {

  // public GaWidgetInfo getGaWidget(String widgetId, String variableId);

  public long queryWidgetCountOfAccount(String connectionId);

  public long queryWidgetCountOfSource(List<String> tableIdList);
}
