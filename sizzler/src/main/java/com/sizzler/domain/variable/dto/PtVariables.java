package com.sizzler.domain.variable.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.widget.dto.SegmentData;

public class PtVariables implements Serializable {

  private static final long serialVersionUID = -7618039749565029097L;

  private String variableId;
  private Long ptoneDsInfoId;
  private String dsCode;
  private Long variableGraphId;
  private String dateDimensionId; // 时间范围对应时间维度列ID
  private String variableColor;
  private String accountName; // ga授权账号
  private String connectionId;
  private String accountId;
  private String propertyId;
  private String profileId;
  private List<PtoneMetricsDimension> metrics = new ArrayList<PtoneMetricsDimension>(); // 包含：指标、是否显示指标总量、sgment过滤器信息
  private String ignoreNullMetrics; // 是否忽略为空的指标值, 默认为0-不忽略
  private List<PtoneMetricsDimension> dimensions = new ArrayList<PtoneMetricsDimension>();
  private String ignoreNullDimension; // 是否忽略为空的维度值, 默认为0-不忽略
  private SegmentData segment;
  private SegmentData filters;
  private String sort;

  public String getDsCode() {
    return dsCode;
  }

  public void setDsCode(String dsCode) {
    this.dsCode = dsCode;
  }

  public String getVariableId() {
    return variableId;
  }

  public void setVariableId(String variableId) {
    this.variableId = variableId;
  }

  public Long getPtoneDsInfoId() {
    return ptoneDsInfoId;
  }

  public void setPtoneDsInfoId(Long ptoneDsInfoId) {
    this.ptoneDsInfoId = ptoneDsInfoId;
  }

  public Long getVariableGraphId() {
    return variableGraphId;
  }

  public void setVariableGraphId(Long variableGraphId) {
    this.variableGraphId = variableGraphId;
  }

  public String getDateDimensionId() {
    return dateDimensionId;
  }

  public void setDateDimensionId(String dateDimensionId) {
    this.dateDimensionId = dateDimensionId;
  }

  public String getVariableColor() {
    return variableColor;
  }

  public void setVariableColor(String variableColor) {
    this.variableColor = variableColor;
  }

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public String getPropertyId() {
    return propertyId;
  }

  public void setPropertyId(String propertyId) {
    this.propertyId = propertyId;
  }

  public String getProfileId() {
    return profileId;
  }

  public void setProfileId(String profileId) {
    this.profileId = profileId;
  }

  public List<PtoneMetricsDimension> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<PtoneMetricsDimension> metrics) {
    this.metrics = metrics;
  }

  public List<PtoneMetricsDimension> getDimensions() {
    return dimensions;
  }

  public void setDimensions(List<PtoneMetricsDimension> dimensions) {
    this.dimensions = dimensions;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getIgnoreNullMetrics() {
    return ignoreNullMetrics;
  }

  public void setIgnoreNullMetrics(String ignoreNullMetrics) {
    this.ignoreNullMetrics = ignoreNullMetrics;
  }

  public String getIgnoreNullDimension() {
    return ignoreNullDimension;
  }

  public void setIgnoreNullDimension(String ignoreNullDimension) {
    this.ignoreNullDimension = ignoreNullDimension;
  }

  public SegmentData getSegment() {
    return segment;
  }

  public void setSegment(SegmentData segment) {
    this.segment = segment;
  }

  public SegmentData getFilters() {
    return filters;
  }

  public void setFilters(SegmentData filters) {
    this.filters = filters;
  }

  public List<String> getMetricsCodeList() {
    List<String> metricsCodeList = new ArrayList<String>();
    if (this.metrics != null && this.metrics.size() > 0) {
      for (PtoneMetricsDimension md : this.metrics) {
        metricsCodeList.add(md.getCode());
      }
    }
    return metricsCodeList;
  }

  public List<String> getDimensionCodeList() {
    List<String> dimensionCodeList = new ArrayList<String>();
    if (this.dimensions != null && this.dimensions.size() > 0) {
      for (PtoneMetricsDimension dd : this.dimensions) {
        dimensionCodeList.add(dd.getCode());
      }
    }
    return dimensionCodeList;
  }

  /**
   * 获取影响widgetData数据的指标的核心参数key
   * @return
   * @date: 2016年9月13日
   * @author peng.xu
   */
  public String getMetricsDataCoreInfoKey() {
    List<PtoneMetricsDimension> coreInfoList = new ArrayList<PtoneMetricsDimension>();
    if (this.metrics != null && this.metrics.size() > 0) {
      for (PtoneMetricsDimension md : this.metrics) {
        PtoneMetricsDimension m = new PtoneMetricsDimension();
        m.setId(md.getId());
        m.setName(md.getName());
        m.setCode(md.getCode());
        m.setCalculateType(md.getCalculateType());
        m.setDataType(md.getDataType());
        m.setDataFormat(md.getDataFormat());
        m.setCategoryCode(md.getCategoryCode());
        m.setType(md.getType());
        m.setIsValidate(md.getIsValidate());
        m.setFormula(md.getFormula());
        coreInfoList.add(m);
      }
    }
    return JSON.toJSONString(coreInfoList);
  }

  /**
   * 获取影响widgetData数据的维度的核心参数key
   * @return
   * @date: 2016年9月13日
   * @author peng.xu
   */
  public String getDimensionDataCoreInfoKey() {
    List<PtoneMetricsDimension> coreInfoList = new ArrayList<PtoneMetricsDimension>();
    if (this.dimensions != null && this.dimensions.size() > 0) {
      for (PtoneMetricsDimension dd : this.dimensions) {
        PtoneMetricsDimension d = new PtoneMetricsDimension();
        d.setId(dd.getId());
        d.setName(dd.getName());
        d.setCode(dd.getCode());
        d.setSort(dd.getSort());
        d.setMax(dd.getMax());
        d.setShowOthers(dd.getShowOthers());
        d.setDataType(dd.getDataType());
        d.setDataFormat(dd.getDataFormat());
        d.setCategoryCode(dd.getCategoryCode());
        d.setIsValidate(dd.getIsValidate());
        d.setDatePeriod(dd.getDatePeriod());
        coreInfoList.add(d);
      }
    }
    return JSON.toJSONString(coreInfoList);
  }

  public String getMetricsCode() {
    List<String> metricsCodeList = new ArrayList<String>();
    if (CollectionUtil.isNotEmpty(this.metrics)) {
      for (PtoneMetricsDimension md : this.metrics) {
        if (md != null) {
          metricsCodeList.add(md.getCode());
        }
      }
    }
    return StringUtil.join(metricsCodeList, ",");
  }

  public String getMetricsName() {
    List<String> metricsNameList = new ArrayList<String>();
    if (CollectionUtil.isNotEmpty(this.metrics)) {
      for (PtoneMetricsDimension md : this.metrics) {
        if (md != null) {
          metricsNameList.add(md.getName());
        }
      }
    }
    return StringUtil.join(metricsNameList, ",");
  }

  public String getDimensionCode() {
    List<String> dimensionCodeList = new ArrayList<String>();
    if (CollectionUtil.isNotEmpty(this.dimensions)) {
      for (PtoneMetricsDimension dd : this.dimensions) {
        if (dd != null) {
          dimensionCodeList.add(dd.getCode());
        }
      }
    }
    return StringUtil.join(dimensionCodeList, ",");
  }

  public String getDimensionName() {
    List<String> dimensionNameList = new ArrayList<String>();
    if (CollectionUtil.isNotEmpty(this.dimensions)) {
      for (PtoneMetricsDimension dd : this.dimensions) {
        if (dd != null) {
          dimensionNameList.add(dd.getName());
        }
      }
    }
    return StringUtil.join(dimensionNameList, ",");
  }

}
