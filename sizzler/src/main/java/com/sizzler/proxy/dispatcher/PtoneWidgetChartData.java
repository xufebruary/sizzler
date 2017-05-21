package com.sizzler.proxy.dispatcher;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class PtoneWidgetChartData implements Serializable {

  private static final long serialVersionUID = 269594379398439894L;

  private ChartPluginType chartPluginType;
  private Map<String, Object> chartPluginDataMap;

  public ChartPluginType getChartPluginType() {
    return chartPluginType;
  }

  public void setChartPluginType(ChartPluginType chartPluginType) {
    this.chartPluginType = chartPluginType;
  }

  public Map<String, Object> getChartPluginDataMap() {
    return chartPluginDataMap;
  }

  public void setChartPluginDataMap(Map<String, Object> chartPluginDataMap) {
    this.chartPluginDataMap = chartPluginDataMap;
  }

  @Override
  public String toString() {
    String str = "{ chartPluginType=" + chartPluginType.toString() + "\n";
    str += JSON.toJSONString(chartPluginDataMap);
    str += "\n}";
    return str;
  }
}
