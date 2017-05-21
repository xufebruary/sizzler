package com.sizzler.domain.ds.dto;

import java.io.Serializable;

public class UserCompoundMetricsDimensionObject extends PtoneMetricsDimension implements
    Serializable {

  private static final long serialVersionUID = -2141754222307978159L;

  private String formulaId; // 公式对象id， uuid， 与公式中的[uuid]匹配
  private String metricsId; // 原始指标id

  public String getFormulaId() {
    return formulaId;
  }

  public void setFormulaId(String formulaId) {
    this.formulaId = formulaId;
  }

  public String getMetricsId() {
    return metricsId;
  }

  public void setMetricsId(String metricsId) {
    this.metricsId = metricsId;
  }

  @Override
  public String toString() {
    return "UserCompoundMetricsDimensionObject [formulaId=" + formulaId + ", metricsId="
        + metricsId + ", getId()=" + getId() + ", getName()=" + getName() + ", getCode()="
        + getCode() + ", getQueryCode()=" + getQueryCode() + ", getI18nCode()=" + getI18nCode()
        + ", getType()=" + getType() + ", getDataType()=" + getDataType() + ", getDataFormat()="
        + getDataFormat() + ", getUnit()=" + getUnit() + ", getDescription()=" + getDescription()
        + ", getDefaultDatePeriod()=" + getDefaultDatePeriod() + ", getAvailableDatePeriod()="
        + getAvailableDatePeriod() + ", getCategoryId()=" + getCategoryId()
        + ", getCategoryCode()=" + getCategoryCode() + ", getAllowSegment()=" + getAllowSegment()
        + ", getOrderNumber()=" + getOrderNumber() + ", getUid()=" + getUid() + ", getDsId()="
        + getDsId() + ", getDsCode()=" + getDsCode() + ", getConnectionId()=" + getConnectionId()
        + ", getSourceId()=" + getSourceId() + ", getTableId()=" + getTableId() + ", getUuid()="
        + getUuid() + ", getDatePeriod()=" + getDatePeriod() + ", getHasFilterItem()="
        + getHasFilterItem() + ", getColumnType()=" + getColumnType() + ", getAllowFilter()="
        + getAllowFilter() + ", getCalculateType()=" + getCalculateType() + "]";
  }

}
