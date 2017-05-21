package com.sizzler.proxy.dispatcher;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class PtoneVariableChartData implements Serializable {

  private static final long serialVersionUID = -7326049355447343336L;

  private GraphType graphType;
  private Map<String, Object> chartDataMap;

  public GraphType getGraphType() {
    return graphType;
  }

  public void setGraphType(GraphType graphType) {
    this.graphType = graphType;
  }

  public Map<String, Object> getChartDataMap() {
    return chartDataMap;
  }

  public void setChartDataMap(Map<String, Object> chartDataMap) {
    this.chartDataMap = chartDataMap;
  }

  @Override
  public String toString() {
    String str = "{ graphType=" + graphType.toString() + "\n";
    str += JSON.toJSONString(chartDataMap);
    str += "\n}";
    return str;
  }
}
