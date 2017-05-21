package com.sizzler.proxy.dispatcher;

import java.io.Serializable;

public class PtoneGraphVariableDataDesc implements Serializable {

  private static final long serialVersionUID = 3958023353146568733L;

  private ChartDataType chartDataType;
  private GraphType graphType;

  public PtoneGraphVariableDataDesc(ChartDataType chartDataType) {
    this.chartDataType = chartDataType;
  }

  public ChartDataType getChartDataType() {
    return chartDataType;
  }

  public void setChartDataType(ChartDataType chartDataType) {
    this.chartDataType = chartDataType;
  }

  public GraphType getGraphType() {
    return graphType;
  }

  public void setGraphType(GraphType graphType) {
    this.graphType = graphType;
  }

  public String getKey() {
    return chartDataType.toString();
  }

}
