package com.sizzler.domain.ds;

import java.io.Serializable;

import com.sizzler.dexcoder.annotation.PK;

public class UserCompoundMetricsDimension implements Serializable {

  private static final long serialVersionUID = -1887528589605163896L;

  public static final String SOURCE_TYPE_USER_CREATE = "user create";
  public static final String SOURCE_TYPE_PANEL_TEMPLATE = "panel template";
  public static final String SOURCE_TYPE_WIDGET_GALLERY = "widget gallery";

  @PK
  private String id; // uuid
  private String name;
  private String code; // uuid
  private String formula; // [uuid] + [uuid2] 表达式
  private String aggregator;// 前端展示使用
  private String originalAggregator; // 运营统计数据
  private String objectsData; // 所有指标的列表包含uuid、id、code、i18nCode，dataType、unit、dsId，dsCode、connectionId、sourceId
  // 、tableId等信息
  private String type; // 类型： compoundMetrics || compoundDimension
  private String description;
  private Integer allowSegment;
  private Integer allowFilter;

  private String spaceId;
  private String dsId;
  private String dsCode;
  private String tableId;
  private String uid;
  private String userEmail;
  private String sourceType; // 来源类型 ， 用户创建user || 根据模板自动生成ptone
  private String templetId; // 来源自模板自动生成的计算指标的原计算指标id

  // 解析用户表达式后需要生成的字段
  private String queryCode; // 取数查询用表达式，保存后解析
  private String dataType;
  private String dataFormat;
  private String unit;
  private String objectsIdList;
  private String dsIdList;
  private String dsCodeList;
  private String connectionIdList;
  private String sourceIdList;
  private String tableIdList;
  private Integer isValidate;
  private Integer metricsCount; // 使用原始指标的数量
  private String isContainsFunc; // 表达式中是否包含计算函数
  private String lastUseTime; // 最后使用时间（widget添加指标时统计）
  private String countOfUse; // 使用数量，每天统计一次，widget中使用的次数

  private String creatorId;
  private String createTime;
  private String modifierId;
  private String modifyTime;
  private Integer orderNumber;
  private Integer isDelete;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getAggregator() {
    return aggregator;
  }

  public String getFormula() {
    return formula;
  }

  public void setFormula(String formula) {
    this.formula = formula;
  }

  public void setAggregator(String aggregator) {
    this.aggregator = aggregator;
  }

  public String getObjectsData() {
    return objectsData;
  }

  public void setObjectsData(String objectsData) {
    this.objectsData = objectsData;
  }

  public String getQueryCode() {
    return queryCode;
  }

  public void setQueryCode(String queryCode) {
    this.queryCode = queryCode;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getDataFormat() {
    return dataFormat;
  }

  public void setDataFormat(String dataFormat) {
    this.dataFormat = dataFormat;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getAllowSegment() {
    return allowSegment;
  }

  public void setAllowSegment(Integer allowSegment) {
    this.allowSegment = allowSegment;
  }

  public Integer getAllowFilter() {
    return allowFilter;
  }

  public void setAllowFilter(Integer allowFilter) {
    this.allowFilter = allowFilter;
  }

  public Integer getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(Integer orderNumber) {
    this.orderNumber = orderNumber;
  }

  public Integer getIsDelete() {
    return isDelete;
  }

  public void setIsDelete(Integer isDelete) {
    this.isDelete = isDelete;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

  public String getDsId() {
    return dsId;
  }

  public void setDsId(String dsId) {
    this.dsId = dsId;
  }

  public String getDsCode() {
    return dsCode;
  }

  public void setDsCode(String dsCode) {
    this.dsCode = dsCode;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  public String getTempletId() {
    return templetId;
  }

  public void setTempletId(String templetId) {
    this.templetId = templetId;
  }

  public String getObjectsIdList() {
    return objectsIdList;
  }

  public void setObjectsIdList(String objectsIdList) {
    this.objectsIdList = objectsIdList;
  }

  public String getDsIdList() {
    return dsIdList;
  }

  public void setDsIdList(String dsIdList) {
    this.dsIdList = dsIdList;
  }

  public String getDsCodeList() {
    return dsCodeList;
  }

  public void setDsCodeList(String dsCodeList) {
    this.dsCodeList = dsCodeList;
  }

  public String getConnectionIdList() {
    return connectionIdList;
  }

  public void setConnectionIdList(String connectionIdList) {
    this.connectionIdList = connectionIdList;
  }

  public String getSourceIdList() {
    return sourceIdList;
  }

  public void setSourceIdList(String sourceIdList) {
    this.sourceIdList = sourceIdList;
  }

  public String getTableIdList() {
    return tableIdList;
  }

  public void setTableIdList(String tableIdList) {
    this.tableIdList = tableIdList;
  }

  public String getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getModifierId() {
    return modifierId;
  }

  public void setModifierId(String modifierId) {
    this.modifierId = modifierId;
  }

  public String getModifyTime() {
    return modifyTime;
  }

  public void setModifyTime(String modifyTime) {
    this.modifyTime = modifyTime;
  }

  public Integer getIsValidate() {
    return isValidate;
  }

  public void setIsValidate(Integer isValidate) {
    this.isValidate = isValidate;
  }

  public String getTableId() {
    return tableId;
  }

  public void setTableId(String tableId) {
    this.tableId = tableId;
  }

  public String getIsContainsFunc() {
    return isContainsFunc;
  }

  public void setIsContainsFunc(String isContainsFunc) {
    this.isContainsFunc = isContainsFunc;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getLastUseTime() {
    return lastUseTime;
  }

  public void setLastUseTime(String lastUseTime) {
    this.lastUseTime = lastUseTime;
  }

  public String getCountOfUse() {
    return countOfUse;
  }

  public void setCountOfUse(String countOfUse) {
    this.countOfUse = countOfUse;
  }

  public String getOriginalAggregator() {
    return originalAggregator;
  }

  public void setOriginalAggregator(String originalAggregator) {
    this.originalAggregator = originalAggregator;
  }

  public Integer getMetricsCount() {
    return metricsCount;
  }

  public void setMetricsCount(Integer metricsCount) {
    this.metricsCount = metricsCount;
  }

  @Override
  public String toString() {
    return "UserCompoundMetricsDimension [id=" + id + ", name=" + name + ", code=" + code
        + ", formula=" + formula + ", aggregator=" + aggregator + ", objectsData=" + objectsData
        + ", type=" + type + ", description=" + description + ", allowSegment=" + allowSegment
        + ", allowFilter=" + allowFilter + ", spaceId=" + spaceId + ", dsId=" + dsId + ", dsCode="
        + dsCode + ", tableId=" + tableId + ", uid=" + uid + ", sourceType=" + sourceType
        + ", templetId=" + templetId + ", queryCode=" + queryCode + ", dataType=" + dataType
        + ", dataFormat=" + dataFormat + ", unit=" + unit + ", objectsIdList=" + objectsIdList
        + ", dsIdList=" + dsIdList + ", dsCodeList=" + dsCodeList + ", connectionIdList="
        + connectionIdList + ", sourceIdList=" + sourceIdList + ", tableIdList=" + tableIdList
        + ", isValidate=" + isValidate + ", creatorId=" + creatorId + ", createTime=" + createTime
        + ", modifierId=" + modifierId + ", modifyTime=" + modifyTime + ", orderNumber="
        + orderNumber + ", isDelete=" + isDelete + "]";
  }

}
