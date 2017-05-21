package com.sizzler.domain.widget.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sizzler.domain.widget.PtoneWidgetChartSetting;

public class PtoneWidgetChartSettingDto implements Serializable {

  private static final long serialVersionUID = 1515669054290435415L;

  private String widgetId;
  private String stackedChart; // 堆叠图(''、normal、percent; 0、 1)
  private String areaChart; // 面积图(0、 1)
  private String showLegend;// 显示图例(0、 1)
  private String showDataLabels; // 是否显示数值
  @SuppressWarnings("rawtypes")
  private List<Map> xAxis; // x轴设置json
  @SuppressWarnings("rawtypes")
  private List<Map> yAxis; // y轴设置的json串
  private Integer showMultiY; // 是否开启多个y轴
  private Map<String, Object> metricsToY; // 指标对应y轴映射关系
  private String showMapName;
  private String hideDetail;
  private String reverseTarget;
  private String hideCalculateName;

  public String getShowMapName() {
    return showMapName;
  }

  public void setShowMapName(String showMapName) {
    this.showMapName = showMapName;
  }

  public PtoneWidgetChartSettingDto() {}

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public PtoneWidgetChartSettingDto(PtoneWidgetChartSetting chartSetting) {
    if (chartSetting != null) {
      this.setWidgetId(chartSetting.getWidgetId());
      this.setStackedChart(chartSetting.getStackedChart());
      this.setAreaChart(chartSetting.getAreaChart());
      this.setShowLegend(chartSetting.getShowLegend());
      this.setShowDataLabels(chartSetting.getShowDataLabels());
      this.setShowMultiY(chartSetting.getShowMultiY());
      this.setShowMapName(chartSetting.getShowMapName());
      this.setHideDetail(chartSetting.getHideDetail());
      this.setHideCalculateName(chartSetting.getHideCalculateName());
      this.setReverseTarget(chartSetting.getReverseTarget());

      String xAxisStr = chartSetting.getxAxis();
      if (xAxisStr != null && !"".equals(xAxisStr)) {
        this.setxAxis(JSON.parseArray(xAxisStr, Map.class));
      } else {
        this.setxAxis(new ArrayList<Map>());
      }

      String yAxisStr = chartSetting.getyAxis();
      if (yAxisStr != null && !"".equals(yAxisStr)) {
        this.setyAxis(JSON.parseArray(yAxisStr, Map.class));
      } else {
        this.setyAxis(new ArrayList<Map>());
      }

      String metricsToYStr = chartSetting.getMetricsToY();
      if (metricsToYStr != null && !"".equals(metricsToYStr)) {
        this.setMetricsToY(JSON.parseObject(metricsToYStr, HashMap.class));
      } else {
        this.setMetricsToY(new HashMap<String, Object>());
      }
    }
  }

  public PtoneWidgetChartSetting parseChartSetting() {
    PtoneWidgetChartSetting chartSetting = new PtoneWidgetChartSetting();
    chartSetting.setWidgetId(this.getWidgetId());
    chartSetting.setStackedChart(this.getStackedChart());
    chartSetting.setAreaChart(this.getAreaChart());
    chartSetting.setShowLegend(this.getShowLegend());
    chartSetting.setShowDataLabels(this.getShowDataLabels());
    chartSetting.setxAxis(JSON.toJSONString(this.getxAxis()));
    chartSetting.setyAxis(JSON.toJSONString(this.getyAxis()));
    chartSetting.setShowMultiY(this.getShowMultiY());
    chartSetting.setMetricsToY(JSON.toJSONString(this.getMetricsToY()));
    chartSetting.setShowMapName(this.getShowMapName());
    chartSetting.setHideDetail(this.getHideDetail());
    chartSetting.setHideCalculateName(this.getHideCalculateName());
    chartSetting.setReverseTarget(this.getReverseTarget());
    return chartSetting;
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

  @SuppressWarnings("rawtypes")
  public List<Map> getxAxis() {
    return xAxis;
  }

  @SuppressWarnings("rawtypes")
  public void setxAxis(List<Map> xAxis) {
    this.xAxis = xAxis;
  }

  @SuppressWarnings("rawtypes")
  public List<Map> getyAxis() {
    return yAxis;
  }

  @SuppressWarnings("rawtypes")
  public void setyAxis(List<Map> yAxis) {
    this.yAxis = yAxis;
  }

  public Integer getShowMultiY() {
    return showMultiY;
  }

  public void setShowMultiY(Integer showMultiY) {
    this.showMultiY = showMultiY;
  }

  public Map<String, Object> getMetricsToY() {
    return metricsToY;
  }

  public void setMetricsToY(Map<String, Object> metricsToY) {
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

  @SuppressWarnings("rawtypes")
  public String getYAxisCoreInfoKey() {
    String coreInfo = "";
    if (this.getyAxis() != null && this.getyAxis().size() > 0) {
      for(Map map : this.getyAxis()){
        String chartType = String.valueOf(map.get("chartType"));
        coreInfo += chartType + "|";
      }
    }
    return coreInfo;
  }

}
