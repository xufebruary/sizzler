package com.sizzler.proxy.dispatcher;

public abstract class PtoneChartPluginGraphDesc {

  private ChartPluginType chartPluginType;
  private GraphType graphType;

  public PtoneChartPluginGraphDesc(ChartPluginType chartPluginType, GraphType graphType) {
    this.chartPluginType = chartPluginType;
    this.graphType = graphType;
  }

  public ChartPluginType getChartPluginType() {
    return chartPluginType;
  }

  public void setChartPluginType(ChartPluginType chartPluginType) {
    this.chartPluginType = chartPluginType;
  }

  public GraphType getGraphType() {
    return graphType;
  }

  public void setGraphType(GraphType graphType) {
    this.graphType = graphType;
  }

  public String getKey() {
    return chartPluginType + "" + graphType;
  }

}
