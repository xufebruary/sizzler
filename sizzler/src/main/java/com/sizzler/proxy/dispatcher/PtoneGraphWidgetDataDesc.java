package com.sizzler.proxy.dispatcher;

public class PtoneGraphWidgetDataDesc {

  private PtoneWidgetData ptoneWidgetData;
  private GraphType graphType;

  public PtoneGraphWidgetDataDesc(GraphType graphType) {
    this.graphType = graphType;
  }

  public GraphType getGraphType() {
    return graphType;
  }

  public void setGraphType(GraphType graphType) {
    this.graphType = graphType;
  }

  public PtoneWidgetData getPtoneWidgetData() {
    return ptoneWidgetData;
  }

  public void setPtoneWidgetData(PtoneWidgetData ptoneWidgetData) {
    this.ptoneWidgetData = ptoneWidgetData;
  }

  public String getKey() {
    return graphType + "";
  }

}
