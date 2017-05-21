package com.sizzler.proxy.dispatcher;

public abstract class PtoneDatasourceGraphDesc {

  private String datasourceType;
  private ChartDataType chartDataType;
  private GraphType graphType;

  public PtoneDatasourceGraphDesc(String datasourceType, ChartDataType chartDataType) {
    this.datasourceType = datasourceType;
    this.chartDataType = chartDataType;
  }

  public String getDatasourceType() {
    return datasourceType;
  }

  public void setDatasourceType(String datasourceType) {
    this.datasourceType = datasourceType;
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
    return datasourceType + "" + chartDataType;
  }

}
