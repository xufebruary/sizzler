package com.sizzler.domain.widget;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class PtoneWidgetChartSetting implements Serializable {

  private static final long serialVersionUID = 1515669054290435415L;

  @PK
  private String widgetId;
  private String stackedChart; // 堆叠图(''、normal、percent; 0、 1)
  private String areaChart; // 面积图(0、 1)
  private String showLegend;// 显示图例(0、 1)
  private String showDataLabels; // 是否显示数值
  private String xAxis; // x轴设置json
  private String yAxis; // y轴设置的json串
  private Integer showMultiY; // 是否开启多个y轴
  private String metricsToY; // 指标对应y轴映射关系
  private String showMapName;
  private String hideDetail;
  private String reverseTarget;
  private String hideCalculateName;

  private int isDelete;

  public String getShowMapName() {
    return showMapName;
  }

  public void setShowMapName(String showMapName) {
    this.showMapName = showMapName;
  }

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public String getStackedChart() {
    return stackedChart;
  }

  public void setStackedChart(String stackedChart) {
    this.stackedChart = stackedChart;
  }

  public String getAreaChart() {
    return areaChart;
  }

  public void setAreaChart(String areaChart) {
    this.areaChart = areaChart;
  }

  public String getShowLegend() {
    return showLegend;
  }

  public void setShowLegend(String showLegend) {
    this.showLegend = showLegend;
  }

  public String getShowDataLabels() {
    return showDataLabels;
  }

  public void setShowDataLabels(String showDataLabels) {
    this.showDataLabels = showDataLabels;
  }

  public String getxAxis() {
    return xAxis;
  }

  public void setxAxis(String xAxis) {
    this.xAxis = xAxis;
  }

  public String getyAxis() {
    return yAxis;
  }

  public void setyAxis(String yAxis) {
    this.yAxis = yAxis;
  }

  public Integer getShowMultiY() {
    return showMultiY;
  }

  public void setShowMultiY(Integer showMultiY) {
    this.showMultiY = showMultiY;
  }

  public String getMetricsToY() {
    return metricsToY;
  }

  public void setMetricsToY(String metricsToY) {
    this.metricsToY = metricsToY;
  }

  public String getHideDetail() {
    return hideDetail;
  }

  public void setHideDetail(String hideDetail) {
    this.hideDetail = hideDetail;
  }

  public String getReverseTarget() {
    return reverseTarget;
  }

  public void setReverseTarget(String reverseTarget) {
    this.reverseTarget = reverseTarget;
  }

  public String getHideCalculateName() {
    return hideCalculateName;
  }

  public void setHideCalculateName(String hideCalculateName) {
    this.hideCalculateName = hideCalculateName;
  }

  public int getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(int isDelete) {
    this.isDelete = isDelete;
  }

}
