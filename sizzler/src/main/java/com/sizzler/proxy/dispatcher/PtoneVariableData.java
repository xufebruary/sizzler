package com.sizzler.proxy.dispatcher;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;

/**
 * variable对应数据结构对象(对应一个series)<br>
 * 对于一个ptoneVariableInfo中包含多个指标情况会拆出多个PtoneVariableData
 * 
 * @author peng.xu
 * 
 */
public class PtoneVariableData implements Serializable {

  private static final long serialVersionUID = 2383961603996821148L;

  private String variableId;
  private String variableName;
  private String metricsId;
  private String metricsCode;
  private String metricsName;
  private String metricsKey; // code + uuid
  private String dimensionsKey; // code + uuid
  private String dimensionsId;
  private String dimensions;
  private String stack;
  private Map<String, String> dataTypeMap = new HashMap<String, String>(); // 数据类型<metricsKey，数据类型>
  private Map<String, String> dataFormatMap = new HashMap<String, String>(); // 数据类型<metricsKey，数据格式>
  private Map<String, String> unitMap = new HashMap<String, String>(); // metricsKey : 单位
  private String datePeriod;
  private List<String> dateRange; // 时间范围：[startDate,endDate], 06 Mar.2015
  private GraphType graphType;
  private List<List<Object>> rows;
  
  private Map<String, Double> totals = new HashMap<String, Double>(); // <metricsKey,Amount>:所有数据总数
  private String color;
  private Map<String, String> colorMap = new HashMap<String, String>(); // 数据类型<variableName, color>
  private String sortType; // default || metricsValue || dimensionValue
  private String orderType; // defaut (默认)、 date(日期)、dataValue（数值）
  private String orderRule; // asc、 desc
  private String orderColumn;// 排序的列（指标列）: 前端设置为uuid， 在后端设置时转为metricsKey（code + '-' + uuid）
  private String orderDimensionDataType;
  private String orderDimensionDataFormat;
  private Integer max;
  private String showOthers;
  private Map<String, Map<String, Object>> metricsTotalsMap =
      new HashMap<String, Map<String, Object>>(); // <metricsKey,Amount>
  private boolean useDatetimeAxis;
  private List<PtoneMetricsDimension> xAxisDateDimensionList; // x轴对应的时间维度
  private String dataKey; // 每次查询都会生成一个uuid
  private Map<String, Object> extInfo = new HashMap<String, Object>();
  private boolean isDetail;// 返回的数据是否为明细数据(用于复合指标的运算，部分数据源复合指标需要使用明细数据来进行运算)

  public String getVariableId() {
    return variableId;
  }

  public void setVariableId(String variableId) {
    this.variableId = variableId;
  }

  public String getVariableName() {
    return variableName;
  }

  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }

  public String getMetricsId() {
    return metricsId;
  }

  public void setMetricsId(String metricsId) {
    this.metricsId = metricsId;
  }

  public String getMetricsCode() {
    return metricsCode;
  }

  public void setMetricsCode(String metricsCode) {
    this.metricsCode = metricsCode;
  }

  public String getMetricsName() {
    return metricsName;
  }

  public void setMetricsName(String metricsName) {
    this.metricsName = metricsName;
  }

  public String getMetricsKey() {
    return metricsKey;
  }

  public void setMetricsKey(String metricsKey) {
    this.metricsKey = metricsKey;
  }

  public String getDimensionsKey() {
    return dimensionsKey;
  }

  public void setDimensionsKey(String dimensionsKey) {
    this.dimensionsKey = dimensionsKey;
  }

  public String getStack() {
    return stack;
  }

  public void setStack(String stack) {
    this.stack = stack;
  }

  public String getDimensionsId() {
    return dimensionsId;
  }

  public void setDimensionsId(String dimensionsId) {
    this.dimensionsId = dimensionsId;
  }

  public String getDimensions() {
    return dimensions;
  }

  public void setDimensions(String dimensions) {
    this.dimensions = dimensions;
  }

  public Map<String, String> getDataTypeMap() {
    return dataTypeMap;
  }

  public void setDataTypeMap(Map<String, String> dataTypeMap) {
    this.dataTypeMap = dataTypeMap;
  }

  public Map<String, String> getDataFormatMap() {
    return dataFormatMap;
  }

  public void setDataFormatMap(Map<String, String> dataFormatMap) {
    this.dataFormatMap = dataFormatMap;
  }

  public Map<String, String> getUnitMap() {
    return unitMap;
  }

  public void setUnitMap(Map<String, String> unitMap) {
    this.unitMap = unitMap;
  }

  public String getDatePeriod() {
    return datePeriod;
  }

  public void setDatePeriod(String datePeriod) {
    this.datePeriod = datePeriod;
  }

  public List<String> getDateRange() {
    return dateRange;
  }

  public void setDateRange(List<String> dateRange) {
    this.dateRange = dateRange;
  }

  public GraphType getGraphType() {
    return graphType;
  }

  public void setGraphType(GraphType graphType) {
    this.graphType = graphType;
  }

  public List<List<Object>> getRows() {
    return rows;
  }

  public void setRows(List<List<Object>> rows) {
    this.rows = rows;
  }

  public Map<String, Double> getTotals() {
    return totals;
  }

  public void setTotals(Map<String, Double> totals) {
    this.totals = totals;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public Map<String, String> getColorMap() {
    return colorMap;
  }

  public void setColorMap(Map<String, String> colorMap) {
    this.colorMap = colorMap;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public String getOrderRule() {
    return orderRule;
  }

  public void setOrderRule(String orderRule) {
    this.orderRule = orderRule;
  }

  public Map<String, Map<String, Object>> getMetricsTotalsMap() {
    return metricsTotalsMap;
  }

  public void setMetricsTotalsMap(Map<String, Map<String, Object>> metricsTotalsMap) {
    this.metricsTotalsMap = metricsTotalsMap;
  }

  public boolean isUseDatetimeAxis() {
    return useDatetimeAxis;
  }

  public boolean getUseDatetimeAxis() {
    return useDatetimeAxis;
  }

  public void setUseDatetimeAxis(boolean useDatetimeAxis) {
    this.useDatetimeAxis = useDatetimeAxis;
  }

  public List<PtoneMetricsDimension> getxAxisDateDimensionList() {
    return xAxisDateDimensionList;
  }

  public void setxAxisDateDimensionList(List<PtoneMetricsDimension> xAxisDateDimensionList) {
    this.xAxisDateDimensionList = xAxisDateDimensionList;
  }

  public String getDataKey() {
    return dataKey;
  }

  public void setDataKey(String dataKey) {
    this.dataKey = dataKey;
  }

  public Map<String, Object> getExtInfo() {
    return extInfo;
  }

  public void setExtInfo(Map<String, Object> extInfo) {
    this.extInfo = extInfo;
  }

  public String getOrderColumn() {
    return orderColumn;
  }

  public void setOrderColumn(String orderColumn) {
    this.orderColumn = orderColumn;
  }

  public Integer getMax() {
    return max;
  }

  public void setMax(Integer max) {
    this.max = max;
  }

  public String getShowOthers() {
    return showOthers;
  }

  public void setShowOthers(String showOthers) {
    this.showOthers = showOthers;
  }

  public String getSortType() {
    return sortType;
  }

  public void setSortType(String sortType) {
    this.sortType = sortType;
  }

  public String getOrderDimensionDataType() {
    return orderDimensionDataType;
  }

  public void setOrderDimensionDataType(String orderDimensionDataType) {
    this.orderDimensionDataType = orderDimensionDataType;
  }

  public String getOrderDimensionDataFormat() {
    return orderDimensionDataFormat;
  }

  public void setOrderDimensionDataFormat(String orderDimensionDataFormat) {
    this.orderDimensionDataFormat = orderDimensionDataFormat;
  }

  public boolean isDetail() {
    return isDetail;
  }

  public void setDetail(boolean isDetail) {
    this.isDetail = isDetail;
  }

  public boolean getIsDetail() {
    return isDetail;
  }

  public void setIsDetail(boolean isDetail) {
    this.isDetail = isDetail;
  }


  @Override
  public String toString() {
    return "PtoneVariableData [variableId=" + variableId + ", variableName=" + variableName
        + ", metricsId=" + metricsId + ", metricsName=" + metricsName + ", dimensionsId="
        + dimensionsId + ", dimensions=" + dimensions + ", dataTypeMap=" + dataTypeMap
        + ", unitMap=" + unitMap + ", datePeriod=" + datePeriod + ", dateRange=" + dateRange
        + ", graphType=" + graphType + ", rows=" + JSON.toJSONString(rows) + ", totals=" + totals
        + ", color=" + color + ", colorMap=" + colorMap + ", orderType=" + orderType + ", dataKey="
        + dataKey + ", orderRule=" + orderRule + ", metricsTotalsMap=" + metricsTotalsMap + "]";
  }

}
