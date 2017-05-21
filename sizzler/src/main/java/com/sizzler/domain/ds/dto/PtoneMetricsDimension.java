package com.sizzler.domain.ds.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sizzler.common.DataTypeConstants;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.widget.dto.SegmentData;

public class PtoneMetricsDimension extends DataTypeConstants implements Serializable {

  private static final long serialVersionUID = -3554596892739643004L;

  public static final long CATEGORY_COMPOUND_TEMPLET_ID = 192;

  public static final String TYPE_METRICS = "metrics";
  public static final String TYPE_COMPOUND_METRICS = "compoundMetrics";
  public static final String TYPE_DIMENSION = "dimension";
  public static final String TYPE_COMPOUND_DIMENSION = "compoundDimension";

  public static final String SCOPE_USER = "0";
  public static final String SCOPE_SESSION = "1";
  public static final String SCOPE_USER_SESSION = "2";

  public static final String VALIDATE_STATUS_DELETE = "-1"; // 标记指标已删除

  public static final String TYPE_SPLITER = "spliter";

  public static final String DATA_FORMAT_TIMESTAMP = "timestamp";

  public static final String DATA_TYPE_DURATION = "DURATION";
  public static final String DATA_FORMAT_DURATION_MILLISECOND = "##ms"; // 毫秒
  public static final String DATA_UNIT_DURATION_MILLISECOND = "ms"; // 毫秒

  public static final String DATA_FORMAT_DURATION_SECOND = "##s"; // 秒
  public static final String DATA_UNIT_DURATION_SECOND = "s"; // 秒
  public static final String DATA_FORMAT_DURATION_MINUTE = "##m"; // 分
  public static final String DATA_UNIT_DURATION_MINUTE = "m"; // 分
  public static final String DATA_FORMAT_DURATION_HOUR = "##h"; // 小时

  public static final String DATA_UNIT_DURATION_HOUR = "h"; // 小时

  public static final String DATA_TYPE_NUMBER = "NUMBER";

  public static final String DATA_TYPE_PERCENT = "PERCENT";
  public static final String DATA_UNIT_PERCENT = "%";

  public static final String DATA_TYPE_CURRENCY = "CURRENCY";

  public static final String DATA_FORMAT_CURRENCY_USD = "$##"; // 美元
  public static final String DATA_UNIT_CURRENCY_USD = "$"; // 美元
  public static final String DATA_FORMAT_CURRENCY_EUR = "€##"; // 欧元
  public static final String DATA_UNIT_CURRENCY_EUR = "€"; // 欧元
  public static final String DATA_FORMAT_CURRENCY_JPY = "¥##"; // 日元
  public static final String DATA_UNIT_CURRENCY_JPY = "¥"; // 日元
  public static final String DATA_FORMAT_CURRENCY_RMB = "¥###"; // 人民币

  public static final String DATA_UNIT_CURRENCY_RMB = "¥"; // 人民币

  public static final String DATA_TYPE_STRING = "STRING";
  public static final String DATA_TYPE_DOUBLE = "DOUBLE";
  public static final String DATA_TYPE_FLOAT = "FLOAT";
  public static final String DATA_TYPE_INTEGER = "INTEGER";
  public static final String DATA_TYPE_LONG = "LONG";

  public static final String DATA_TYPE_BOOLEAN = "BOOLEAN";
  public static final String DATA_TYPE_CUSTOM = "CUSTOM"; // ptapp自定义变量的数据类型为CUSTOM

  /**
   * 维度上的sort设置
   */
  public static final String SORT_ATTRIBUTE_TYPE = "type"; // 排序类型： default ||
                                                           // dimensionVlaue
                                                           // ||　metricsValue
  public static final String SORT_ATTRIBUTE_SORT_BY = "sortBy"; // 排序规则：
                                                                // dataValue ||
                                                                // stringValue
                                                                // ||
                                                                // date
  public static final String SORT_ATTRIBUTE_SORT_ORDER = "sortOrder"; // 排序规则：
                                                                      // asc ||
                                                                      // desc
  public static final String SORT_ATTRIBUTE_SORT_COLUMN = "sortColumn"; // 排序列:
                                                                        // uuid
  public static final String SORT_TYPE_DEFAULT = "default"; // 默认排序
  public static final String SORT_TYPE_DIMENSION_VALUE = "dimensionValue"; // 按维度排序
  public static final String SORT_TYPE_METRICS_VALUE = "metricsValue"; // 按指标排序
  public static final String SORT_BY_NUMBER = "number"; // 按数值排序（某一指标）
  public static final String SORT_BY_STRING = "string"; // 按字符串排序（某一维度值）
  public static final String SORT_BY_DATE = "date"; // 按时间排序
  public static final String SORT_ORDER_ASC = "asc";
  public static final String SORT_ORDER_DESC = "desc";

  private String id;
  private String alias;// 指标维度别名
  private String name;
  private String code;
  private String queryCode;
  private String i18nCode;
  private String type; // metrics or dimension
  private String dataType; // 数据类型 TIME、NUMBER
  private String dataFormat; // 数据格式
  private String unit;
  private String description;
  private String defaultDatePeriod;
  private String availableDatePeriod;
  private Long categoryId;
  private String categoryCode;
  private Integer allowSegment;
  private Integer allowFilter;
  private Integer orderNumber;
  private String hasFilterItem; // 过滤器中是否含有固定选项，0：没有，1：有(针对维度，所有指标使用比较运算符)
  private String segmentUserSession; // 0:用户级,1:会话级,2:两者兼有
  private String filterUserSession; // 0:用户级,1:会话级,2:两者兼有
  private String columnType;// 数据源原始列类型

  private Long uid;
  private Long dsId;
  private String dsCode;
  private String connectionId;
  private String sourceId;
  private String tableId;

  private String uuid; // 用于存放前端传递过来的维度、指标中的UUID add by you.zou 2016.2.22
  private String datePeriod; // 用于给X时间轴传递时间粒度 add by you.zou 2016.2.23
  private String formula; // 复合指标计算公式
  private String isContainsFunc; // 表达式中是否包含计算函数
  private String isValidate;

  private String calculateType; // 计算类型 add by you.zou on 2016.8.12
                                // 用于在需要使用聚合函数的数据源中可以使用

  // 用于widget已选指标、维度列表
  private String realName;// 原始名称
  private Map<String, String> i18nName;// 国际化name
  private Boolean showMetricAmount;
  private SegmentData segment;
  private Map<String, String> sort; // {"type":"", "sortBy":"", sortOrder:"",
                                    // "sortColumn":""}
  private Integer max; // topN
  private String showOthers; // 0 || 1

  private Integer isDefaultSelect; // 是否默认选中，0-否，1-是，设置1时，前端会将需要默认选中的指标维度显示在widget的指标维度列表，用户不能删除、修改

  // 标识Time控件中的下拉列表中是否显示;前端先根据数据类型判断，再根据该字段进行判断
  private int isShowOnTimeDropdowns;

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

  public String getQueryCode() {
    return queryCode;
  }

  public void setQueryCode(String queryCode) {
    this.queryCode = queryCode;
  }

  public String getI18nCode() {
    return i18nCode;
  }

  public void setI18nCode(String i18nCode) {
    this.i18nCode = i18nCode;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDefaultDatePeriod() {
    return defaultDatePeriod;
  }

  public void setDefaultDatePeriod(String defaultDatePeriod) {
    this.defaultDatePeriod = defaultDatePeriod;
  }

  public String getAvailableDatePeriod() {
    return availableDatePeriod;
  }

  public void setAvailableDatePeriod(String availableDatePeriod) {
    this.availableDatePeriod = availableDatePeriod;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryCode() {
    return categoryCode;
  }

  public void setCategoryCode(String categoryCode) {
    this.categoryCode = categoryCode;
  }

  public Integer getAllowSegment() {
    return allowSegment;
  }

  public void setAllowSegment(Integer allowSegment) {
    this.allowSegment = allowSegment;
  }

  public Integer getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(Integer orderNumber) {
    this.orderNumber = orderNumber;
  }

  public Long getUid() {
    return uid;
  }

  public void setUid(Long uid) {
    this.uid = uid;
  }

  public Long getDsId() {
    return dsId;
  }

  public void setDsId(Long dsId) {
    this.dsId = dsId;
  }

  public String getDsCode() {
    return dsCode;
  }

  public void setDsCode(String dsCode) {
    this.dsCode = dsCode;
  }

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getTableId() {
    return tableId;
  }

  public void setTableId(String tableId) {
    this.tableId = tableId;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getDatePeriod() {
    return datePeriod;
  }

  public void setDatePeriod(String datePeriod) {
    this.datePeriod = datePeriod;
  }

  public String getHasFilterItem() {
    return hasFilterItem;
  }

  public void setHasFilterItem(String hasFilterItem) {
    this.hasFilterItem = hasFilterItem;
  }

  public String getSegmentUserSession() {
    return segmentUserSession;
  }

  public void setSegmentUserSession(String segmentUserSession) {
    this.segmentUserSession = segmentUserSession;
  }

  public String getFilterUserSession() {
    return filterUserSession;
  }

  public void setFilterUserSession(String filterUserSession) {
    this.filterUserSession = filterUserSession;
  }

  public static String getSelectedMetricsOrDimensionKey(PtoneMetricsDimension data) {
    return data != null ? data.getCode() + "-" + data.getUuid() : "";
  }

  public String getColumnType() {
    return columnType;
  }

  public void setColumnType(String columnType) {
    this.columnType = columnType;
  }

  public Integer getAllowFilter() {
    return allowFilter;
  }

  public void setAllowFilter(Integer allowFilter) {
    this.allowFilter = allowFilter;
  }

  public String getCalculateType() {
    return calculateType;
  }

  public void setCalculateType(String calculateType) {
    this.calculateType = calculateType;
  }

  public String getFormula() {
    return formula;
  }

  public void setFormula(String formula) {
    this.formula = formula;
  }

  public String getIsContainsFunc() {
    return isContainsFunc;
  }

  public void setIsContainsFunc(String isContainsFunc) {
    this.isContainsFunc = isContainsFunc;
  }

  public String getIsValidate() {
    return isValidate;
  }

  public void setIsValidate(String isValidate) {
    this.isValidate = isValidate;
  }

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  public Map<String, String> getI18nName() {
    return i18nName;
  }

  public void setI18nName(Map<String, String> i18nName) {
    this.i18nName = i18nName;
  }

  public Boolean getShowMetricAmount() {
    return showMetricAmount;
  }

  public void setShowMetricAmount(Boolean showMetricAmount) {
    this.showMetricAmount = showMetricAmount;
  }

  public SegmentData getSegment() {
    return segment;
  }

  public void setSegment(SegmentData segment) {
    this.segment = segment;
  }

  public Map<String, String> getSort() {
    return sort;
  }

  public void setSort(Map<String, String> sort) {
    this.sort = sort;
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

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  // //////////////////////////////////////////////

  public Integer getIsDefaultSelect() {
    return isDefaultSelect;
  }

  public void setIsDefaultSelect(Integer isDefaultSelect) {
    this.isDefaultSelect = isDefaultSelect;
  }

  public static boolean isDateDimension(String dataType) {
    return isDateDataType(dataType);
  }

  public int getIsShowOnTimeDropdowns() {
    return isShowOnTimeDropdowns;
  }

  public void setIsShowOnTimeDropdowns(int isShowOnTimeDropdowns) {
    this.isShowOnTimeDropdowns = isShowOnTimeDropdowns;
  }

  /**
   * filter的key（用于获取对应filterItem）
   */
  public static String getFilterKey(PtoneMetricsDimension data) {
    return data.getDsId() + "-" + data.getType() + "-" + data.getId();
  }

  public static boolean isDateDataType(String dataType) {
    return PtoneMetricsDimension.DATA_TYPE_DATE.equalsIgnoreCase(dataType)
        || PtoneMetricsDimension.DATA_TYPE_TIMESTAMP.equalsIgnoreCase(dataType)
        || PtoneMetricsDimension.DATA_TYPE_DATETIME.equalsIgnoreCase(dataType);
  }

  /**
   * 判断是否为数值类型的数据类型
   * 
   * @param dataType
   * @return
   * @date: 2016年7月14日
   * @author peng.xu
   */
  public static boolean isNumberDataType(String dataType) {
    return DATA_TYPE_NUMBER.equals(dataType) || DATA_TYPE_DOUBLE.equals(dataType)
        || DATA_TYPE_FLOAT.equals(dataType) || DATA_TYPE_LONG.equals(dataType)
        || DATA_TYPE_INTEGER.equals(dataType) || DATA_TYPE_PERCENT.equals(dataType)
        || DATA_TYPE_CURRENCY.equals(dataType) || DATA_TYPE_DURATION.equals(dataType);
  }

  /**
   * 根据数据类型和数据格式获取单位
   * 
   * @param dataType
   * @param dataFormat
   * @return
   * @date: 2016年7月18日
   * @author peng.xu
   */
  public static String getUnitByDataTypeAndFormat(String dataType, String dataFormat) {
    String unit = "";
    if (PtoneMetricsDimension.DATA_TYPE_PERCENT.equals(dataType)) {
      unit = PtoneMetricsDimension.DATA_UNIT_PERCENT;
    } else if (PtoneMetricsDimension.DATA_TYPE_DURATION.equals(dataType)) {
      if (PtoneMetricsDimension.DATA_FORMAT_DURATION_MILLISECOND.equals(dataFormat)) {
        unit = PtoneMetricsDimension.DATA_UNIT_DURATION_MILLISECOND;
      } else if (PtoneMetricsDimension.DATA_FORMAT_DURATION_SECOND.equals(dataFormat)) {
        unit = PtoneMetricsDimension.DATA_UNIT_DURATION_SECOND;
      } else if (PtoneMetricsDimension.DATA_FORMAT_DURATION_MINUTE.equals(dataFormat)) {
        unit = PtoneMetricsDimension.DATA_UNIT_DURATION_MINUTE;
      } else if (PtoneMetricsDimension.DATA_FORMAT_DURATION_HOUR.equals(dataFormat)) {
        unit = PtoneMetricsDimension.DATA_UNIT_DURATION_HOUR;
      }
    } else if (PtoneMetricsDimension.DATA_TYPE_CURRENCY.equals(dataType)) {
      if (PtoneMetricsDimension.DATA_FORMAT_CURRENCY_USD.equals(dataFormat)) {
        unit = PtoneMetricsDimension.DATA_UNIT_CURRENCY_USD;
      } else if (PtoneMetricsDimension.DATA_FORMAT_CURRENCY_EUR.equals(dataFormat)) {
        unit = PtoneMetricsDimension.DATA_UNIT_CURRENCY_EUR;
      } else if (PtoneMetricsDimension.DATA_FORMAT_CURRENCY_JPY.equals(dataFormat)) {
        unit = PtoneMetricsDimension.DATA_UNIT_CURRENCY_JPY;
      } else if (PtoneMetricsDimension.DATA_FORMAT_CURRENCY_RMB.equals(dataFormat)) {
        unit = PtoneMetricsDimension.DATA_UNIT_CURRENCY_RMB;
      }
    }
    return unit;
  }

  /**
   * 根据数据类型列表判断复合指标的数据类型
   * 
   * @param dataTypeList
   * @return
   * @date: 2016年7月18日
   * @author peng.xu
   */
  public static String getCompoundMetricsDataType(List<String> dataTypeList) {
    if (dataTypeList != null && dataTypeList.size() > 0) {
      if (dataTypeList.contains(DATA_TYPE_CURRENCY) && !dataTypeList.contains(DATA_TYPE_DURATION)) {
        // return DATA_TYPE_CURRENCY;
        return DATA_TYPE_NUMBER;
      } else if (!dataTypeList.contains(DATA_TYPE_CURRENCY)
          && dataTypeList.contains(DATA_TYPE_DURATION)) {
        return DATA_TYPE_DURATION;
      }
    }
    return DATA_TYPE_NUMBER; // 默认返回数值类型
  }

  /**
   * 获取复合指标的数据格式
   * 
   * @param dataType
   * @param dataFormatList
   * @return
   * @date: 2016年7月26日
   * @author peng.xu
   */
  public static String getCompoundMetricsDataFormat(String dataType, List<String> dataFormatList) {
    if (dataFormatList != null && dataFormatList.size() > 0) {
      Set<String> dataFormatSet = new HashSet<String>();
      for (String dataFormat : dataFormatList) {
        if (StringUtil.isNotBlank(dataFormat)) {
          dataFormatSet.add(dataFormat);
        }
      }
      if (DATA_TYPE_CURRENCY.equals(dataType) && dataFormatSet.size() == 1) {
        return dataFormatSet.toArray(new String[] {})[0];
      } else if (DATA_TYPE_DURATION.equals(dataType)) {
        // 对于持续时间，以小的单位为最终单位
        if (dataFormatSet.contains(DATA_FORMAT_DURATION_MILLISECOND)) {
          return DATA_FORMAT_DURATION_MILLISECOND;
        } else if (dataFormatSet.contains(DATA_FORMAT_DURATION_SECOND)) {
          return DATA_FORMAT_DURATION_SECOND;
        } else if (dataFormatSet.contains(DATA_FORMAT_DURATION_MINUTE)) {
          return DATA_FORMAT_DURATION_MINUTE;
        } else if (dataFormatSet.contains(DATA_FORMAT_DURATION_HOUR)) {
          return DATA_FORMAT_DURATION_HOUR;
        }
      }
    }
    return ""; // 默认为空
  }

  /**
   * 获取复合指标的单位
   * 
   * @param dataType
   * @param unitList
   * @return
   * @date: 2016年7月18日
   * @author peng.xu
   */
  public static String getCompoundMetricsDataUnit(String dataType, List<String> unitList) {
    if (unitList != null && unitList.size() > 0) {
      Set<String> unitSet = new HashSet<String>();
      unitSet.addAll(unitList);
      if (DATA_TYPE_CURRENCY.equals(dataType) && unitSet.size() == 1) {
        return unitSet.toArray(new String[] {})[0];
      } else if (DATA_TYPE_DURATION.equals(dataType)) {
        // 对于持续时间，以小的单位为最终单位
        if (unitSet.contains(DATA_UNIT_DURATION_MILLISECOND)) {
          return DATA_UNIT_DURATION_MILLISECOND;
        } else if (unitSet.contains(DATA_UNIT_DURATION_SECOND)) {
          return DATA_UNIT_DURATION_SECOND;
        } else if (unitSet.contains(DATA_UNIT_DURATION_MINUTE)) {
          return DATA_UNIT_DURATION_MINUTE;
        } else if (unitSet.contains(DATA_UNIT_DURATION_HOUR)) {
          return DATA_UNIT_DURATION_HOUR;
        }
      }
    }
    return ""; // 默认为空
  }

  /**
   * 获取持续时间类型单位之间转换表达式
   * 
   * @param srcUnit
   * @param targetUnit
   * @return
   * @date: 2016年7月18日
   * @author peng.xu
   */
  public static String getParseDurationStr(String srcUnit, String targetUnit) {
    // 单位不为空，且单位不相等则进行转换
    String parseStr = "";
    if (StringUtil.isNotBlank(srcUnit) && StringUtil.isNotBlank(targetUnit)
        && !srcUnit.equalsIgnoreCase(targetUnit)) {
      // 首先将原类型转为 ms
      if (DATA_UNIT_DURATION_MILLISECOND.equalsIgnoreCase(srcUnit)) {
      } else if (DATA_UNIT_DURATION_SECOND.equalsIgnoreCase(srcUnit)) {
        parseStr += "*1000";
      } else if (DATA_UNIT_DURATION_MINUTE.equalsIgnoreCase(srcUnit)) {
        parseStr += "*60*1000";
      } else if (DATA_UNIT_DURATION_HOUR.equalsIgnoreCase(srcUnit)) {
        parseStr += "*60*60*1000";
      }

      // 然后将对应的ms转为目标单位
      if (DATA_UNIT_DURATION_MILLISECOND.equalsIgnoreCase(targetUnit)) {
      } else if (DATA_UNIT_DURATION_SECOND.equalsIgnoreCase(targetUnit)) {
        parseStr += "/1000";
      } else if (DATA_UNIT_DURATION_MINUTE.equalsIgnoreCase(targetUnit)) {
        parseStr += "/60/1000";
      } else if (DATA_UNIT_DURATION_HOUR.equalsIgnoreCase(targetUnit)) {
        parseStr += "/60/60/1000";
      }

    }
    return parseStr; // 默认为空
  }

}
