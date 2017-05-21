package com.sizzler.proxy.dispatcher;

import java.util.Map;

public class PtoneChartPluginDesc {

  private ChartPluginType chartPluginType;
  private String widgetId;
  private Map<String, String> webParamMap;

  public PtoneChartPluginDesc(ChartPluginType chartPluginType) {
    this.chartPluginType = chartPluginType;
  }

  public ChartPluginType getChartPluginType() {
    return chartPluginType;
  }

  public void setChartPluginType(ChartPluginType chartPluginType) {
    this.chartPluginType = chartPluginType;
  }

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public Map<String, String> getWebParamMap() {
    return webParamMap;
  }

  public void setWebParamMap(Map<String, String> webParamMap) {
    this.webParamMap = webParamMap;
  }

  public String getKey() {
    return chartPluginType + "";
  }

}
