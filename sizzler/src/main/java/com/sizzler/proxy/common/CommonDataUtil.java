package com.sizzler.proxy.common;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sizzler.cache.DataCacheService;
import com.sizzler.cache.SysConfigParamCache;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.PtoneDateUtil;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.JodaDateUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.common.utils.UuidUtil;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionDto;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionObject;
import com.sizzler.domain.sys.SysMetaLog;
import com.sizzler.proxy.common.model.CommonQueryParam;
import com.sizzler.proxy.common.model.ModelData;
import com.sizzler.proxy.dispatcher.GraphType;
import com.sizzler.proxy.dispatcher.PtoneVariableData;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;
import com.sizzler.service.DataSourceManagerService;
import com.sizzler.service.ds.PtoneDsService;
import com.sizzler.service.ds.UserConnectionSourceTableColumnService;
import com.sizzler.system.Constants;

@Component("commonDataUtil")
public class CommonDataUtil {

  private Logger log = LoggerFactory.getLogger(CommonDataUtil.class);

  @Autowired
  private DataSourceManagerService dataSourceManagerService;

  @Autowired
  private UserConnectionSourceTableColumnService userConnectionSourceTableColumnService;

  @Autowired
  private PtoneDsService ptoneDsService;

  @Autowired
  private SysConfigParamCache sysConfigParamCache;

  @Autowired
  private DataCacheService dataCacheService;

  public static final String BLANK_DIMENSION_VALUE = "(Blank)"; // 此处需要国际化

  // 过滤器操作符
  public static final String FILTER_OP_CONTAIN_SUBSTR = "=@";// "Contains substring"
  public static final String FILTER_OP_NOT_CONTAIN_SUB = "!@";// "Does not contain substring"
  public static final String FILTER_OP_REGEXP_CONTAIN = "=~"; // "Does not contain substring"
  public static final String FILTER_OP_REQEXP__NOT_CONTAIN = "!~"; // "Does not contain a match for regular expression"

  public static final String FILTER_OP_EQUAL = "equal";
  public static final String FILTER_OP_NOT_EQUAL = "not_equal";
  public static final String FILTER_OP_CONTAIN = "contain";
  public static final String FILTER_OP_NOT_CONTAIN = "not_contain";
  public static final String FILTER_OP_START = "start";
  public static final String FILTER_OP_NOT_START = "not_start";
  public static final String FILTER_OP_END = "end";
  public static final String FILTER_OP_NOT_END = "not_end";
  public static final String FILTER_OP_IN = "in";
  public static final String FILTER_OP_NOT_IN = "not_in";

  public static final String FILTER_OP_NE = "<>";
  public static final String FILTER_OP_NE_2 = "!=";
  public static final String FILTER_OP_NE_3 = "!=="; // 为兼容前台code冲突问题增加转换符号
  public static final String FILTER_OP_EQ = "=";
  public static final String FILTER_OP_EQ_2 = "==";
  public static final String FILTER_OP_EQ_3 = "==="; // 为兼容前台code冲突问题增加转换符号
  public static final String FILTER_OP_LT = "<";
  public static final String FILTER_OP_LE = "<=";
  public static final String FILTER_OP_GT = ">";
  public static final String FILTER_OP_GE = ">=";

  public static final String FILTER_OP_UPCASE_IN = "IN";
  public static final String FILTER_OP_UPCASE_CONTAIN = "CONTAIN";
  public static final String FILTER_OP_UPCASE_GREATER_THAN = "GREATER_THAN";
  public static final String FILTER_OP_UPCASE_GREATER_THAN_OR_EQUAL = "GREATER_THAN_OR_EQUAL";
  public static final String FILTER_OP_UPCASE_EQUAL = "EQUAL";
  public static final String FILTER_OP_UPCASE_LESS_THAN = "LESS_THAN";
  public static final String FILTER_OP_UPCASE_LESS_THAN_OR_EQUAL = "LESS_THAN_OR_EQUAL";
  public static final String FILTER_OP_UPCASE_NOT_EQUAL_CODE = "NOT_EQUAL";

  public static final String FILTER_OP_IS_NULL = "is_null";
  public static final String FILTER_OP_IS_NOT_NULL = "is_not_null";

  /**
   * 设置用户设置的排序规则
   * 
   * @param ptoneVariableData
   * @param ptoneWidgetParam
   */
  public void setUserSortAndMax(PtoneVariableData ptoneVariableData,
      PtoneWidgetParam ptoneWidgetParam) {
    GraphType graphType = GraphType.valueOf(ptoneWidgetParam.getGraphType().toUpperCase());
    // // 如果是复合指标的拆分查询，获取原始图形类型
    // if(ptoneWidgetParam.isReturnTableDataForCompoundMetrics()){
    // graphType =
    // GraphType.valueOf(ptoneWidgetParam.getOriginalGraphType().toUpperCase());
    // }
    if (GraphType.TABLE.equals(graphType) || GraphType.MAP.equals(graphType)
        || GraphType.SIMPLENUMBER.equals(graphType) || GraphType.NUMBER.equals(graphType)
        || GraphType.CIRCLEPERCENT.equals(graphType) || GraphType.PROGRESSBAR.equals(graphType)) {
      return;
    }

    PtoneMetricsDimension varSortDimension = ptoneWidgetParam.getVariableSortDimension();
    if (varSortDimension != null) {
      PtoneMetricsDimension dimension = varSortDimension;
      // salesforce不需要转换（从API获取指标维度列表）
      if (!ptoneWidgetParam.getDsCode().equalsIgnoreCase(DsConstants.DS_CODE_SALESFORCE)) {
        dimension = this.getPtoneMetricsDimension(ptoneWidgetParam.getDsCode(),
            varSortDimension.getId(), PtoneMetricsDimension.TYPE_DIMENSION);
      }
      ptoneVariableData.setOrderDimensionDataType(dimension.getDataType());
      ptoneVariableData.setOrderDimensionDataFormat(dimension.getDataFormat());
    }

    Map<String, String> variableSort = ptoneWidgetParam.getVariableSort();
    if (variableSort != null) {
      String sortType = variableSort.get(PtoneMetricsDimension.SORT_ATTRIBUTE_TYPE);
      String sortBy = variableSort.get(PtoneMetricsDimension.SORT_ATTRIBUTE_SORT_BY);
      String sortOrder = variableSort.get(PtoneMetricsDimension.SORT_ATTRIBUTE_SORT_ORDER);
      String sortColumn = variableSort.get(PtoneMetricsDimension.SORT_ATTRIBUTE_SORT_COLUMN);
      ptoneVariableData.setSortType(sortType);
      ptoneVariableData.setOrderType(sortBy);
      ptoneVariableData.setOrderRule(sortOrder);
      ptoneVariableData.setOrderColumn(sortColumn);

    } else {
      if (varSortDimension != null
          && (ptoneWidgetParam.getDsCode().equalsIgnoreCase(DsConstants.DS_CODE_DOUBLECLICK) || ptoneWidgetParam
              .getDsCode().equalsIgnoreCase(DsConstants.DS_CODE_FACEBOOKAD))
          && StringUtil.isBlank(varSortDimension.getDataType())) {
        varSortDimension.setDataType("STRING");
      }

      // 兼容历史数据处理
      if (varSortDimension != null && StringUtil.isNotBlank(varSortDimension.getDataType())) {
        // 时间类型维度：时间升序，其他的：数据量降序
        if (PtoneMetricsDimension.isDateDimension(varSortDimension.getDataType())
            || PtoneMetricsDimension.DATA_TYPE_TIME
                .equalsIgnoreCase(varSortDimension.getDataType())) {
          ptoneVariableData.setSortType(PtoneMetricsDimension.SORT_TYPE_DIMENSION_VALUE);
          ptoneVariableData.setOrderType(PtoneMetricsDimension.SORT_BY_DATE);
          ptoneVariableData.setOrderRule(PtoneMetricsDimension.SORT_ORDER_ASC);
          ptoneVariableData.setOrderColumn(null);
        } else {
          ptoneVariableData.setSortType(PtoneMetricsDimension.SORT_TYPE_METRICS_VALUE);
          ptoneVariableData.setOrderType(PtoneMetricsDimension.SORT_BY_NUMBER);
          ptoneVariableData.setOrderRule(PtoneMetricsDimension.SORT_ORDER_DESC);
        }
      }

    }

    // 修正orderColumn为metricsKey，如果设置column不存在设为第一个指标desc
    if (PtoneMetricsDimension.SORT_TYPE_METRICS_VALUE.equals(ptoneVariableData.getSortType())) {
      String orderColumn = null;
      String orderRule = null;
      // 不使用metricsKeyList， 因增加过滤器后分开查询会有问题
      List<String> metricsKeyList = ptoneWidgetParam.getMetricsKeyList();
      for (PtoneMetricsDimension md : ptoneWidgetParam.getMetrics()) {
        String metricsKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md);
        if (metricsKeyList.contains(metricsKey)) {
          // 默认设置第一个指标desc排列
          if (StringUtil.isBlank(orderColumn)) {
            orderColumn = metricsKey;
            orderRule = PtoneMetricsDimension.SORT_ORDER_DESC;
          }
          if (ptoneVariableData.getOrderColumn() != null
              && metricsKey.endsWith(ptoneVariableData.getOrderColumn())) {
            orderColumn = metricsKey;
            orderRule = ptoneVariableData.getOrderRule();
          }
        }
      }
      ptoneVariableData.setOrderColumn(orderColumn);
      ptoneVariableData.setOrderRule(orderRule);
    }

    // 设置用户设置的max限制
    ptoneVariableData.setMax(ptoneWidgetParam.getVariableMax());
    ptoneVariableData.setShowOthers(ptoneWidgetParam.getVariableShowOthers());

  }

  public PtoneMetricsDimension getPtoneMetricsDimension(String dsCode, String id, String type) {
    PtoneMetricsDimension item = null;
    UserConnectionSourceTableColumn column = userConnectionSourceTableColumnService
        .getAvailableColumn(id);
    item = new PtoneMetricsDimension();
    BeanUtils.copyProperties(column, item);
    item.setId(column.getColId());
    return item;
  }

  /**
   * 获取唯一维度的索引<br>
   * 只支持一个维度
   * 
   * @param dimensions
   *          维度列表
   * @param userDimensions
   *          唯一维度
   * @return
   */
  public static int getDimensionsIndex(String dimensions, String userDimensions) {
    List<String> dimensionsList = new ArrayList<String>();
    if (StringUtil.isNotBlank(dimensions) && StringUtil.isNotBlank(userDimensions)) {
      dimensionsList = StringUtil.splitToList(dimensions, ",");
    }
    return dimensionsList.indexOf(userDimensions);
  }

  /**
   * 获取维度值列表
   * 
   * @param modelData
   * @param dimensions
   * @param userDimensions
   * @return
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static List<String> getDimensionsValueList(ModelData modelData, String dimensions,
      String userDimensions) {
    List<String> list = new ArrayList<String>();
    int dimensionsIndex = getDimensionsIndex(dimensions, userDimensions);
    if (dimensions != null && !dimensions.equals(userDimensions) && dimensionsIndex > -1) {
      List<List> rows = modelData.getObjetRowList();
      for (List<Object> row : rows) {
        String dimensionsValue = String.valueOf(row.get(dimensionsIndex));
        if (StringUtil.isBlank(dimensionsValue)) {
          dimensionsValue = BLANK_DIMENSION_VALUE; // TODO: 此处需要国际化
        }
        if (!list.contains(dimensionsValue))
          list.add(dimensionsValue);
      }
    } else {
      list.add(""); // 若果用户没有设置对应的维度返回一个空串（为了保持统一处理风格）
    }
    return list;
  }

  public SysMetaLog buildCommonSysMetaLog(PtoneWidgetParam ptoneWidgetParam) {
    SysMetaLog sysMetaLog = new SysMetaLog();
    try {
      // 设置发送的数据内容SysMetaLog
      sysMetaLog.setUid(ptoneWidgetParam.getUid());
      sysMetaLog.setOperateId(UuidUtil.generateUuid());
      sysMetaLog.setTime(System.currentTimeMillis());
      sysMetaLog.setPosition(Constants.OperateLog.DATA_QUERY); // DATA_QUERY
                                                               // 用于标识数据查询
      sysMetaLog.setOperate(ptoneWidgetParam.getUserConnection().getDsCode() + "_"
          + Constants.OperateLog.DATA_QUERY); // GA_DATA_QUERY 用于标识查询数据源的数据
    } catch (Exception e) {
      log.error("buildCommonSysMetaLog error ", e);
    }
    return sysMetaLog;
  }

  public Map<String, Object> buildCommonOperateContent(PtoneWidgetParam ptoneWidgetParam,
      CommonQueryParam queryParam) {
    Map<String, Object> operateContent = new LinkedHashMap<>();
    try {
      if (ptoneWidgetParam != null) {
        operateContent.put(Constants.OperateLog.OperateLogContent.PANEL_ID,
            ptoneWidgetParam.getPanelId());
        operateContent.put(Constants.OperateLog.OperateLogContent.WIDGET_ID,
            ptoneWidgetParam.getWidgetId());
        operateContent.put(Constants.OperateLog.OperateLogContent.DATE_KEY,
            ptoneWidgetParam.getDateKey());
        operateContent.put(Constants.OperateLog.GaOperateLogContent.ACCOUNT, ptoneWidgetParam
            .getUserConnection().getName());
        for (Map.Entry<String, Object> entry : ptoneWidgetParam.getOtherInfo().entrySet()) {
          operateContent.put(entry.getKey(), entry.getValue());
        }
      }
      if (queryParam != null) {
        operateContent.put(Constants.OperateLog.OperateLogContent.START_DATE,
            queryParam.getStartDate());
        operateContent
            .put(Constants.OperateLog.OperateLogContent.END_DATE, queryParam.getEndDate());
      }
    } catch (Exception e) {
      log.error("buildCommonOperateContent error ", e);
    }
    return operateContent;
  }

  /**
   * 根据modelData获取数据源的指标总量
   * 
   * @param modelData
   * @param ptoneWidgetParam
   * @return
   * @date: 2016年7月27日
   * @author peng.xu
   */
  public Map<String, Map<String, Object>> getMetricsTotalsMap(ModelData modelData,
      PtoneWidgetParam ptoneWidgetParam, boolean isApiDs) {
    Map<String, Map<String, Object>> metricsTotalsMap = new LinkedHashMap<String, Map<String, Object>>();

    List<PtoneMetricsDimension> metricsDataList = ptoneWidgetParam.getMetrics();
    List<String> metricsKeyList = ptoneWidgetParam.getMetricsKeyList();
    List<Object> totalRowList = modelData.getTotalRowList();

    // 处理单次查询存在相同指标情况,修正mericsName
    Map<String, Integer> metricsNameMap = new HashMap<String, Integer>();
    int mIndex = 0;
    for (PtoneMetricsDimension md : metricsDataList) {
      String metricsKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md);
      if (metricsKeyList.contains(metricsKey) && totalRowList != null
          && totalRowList.size() > mIndex) {
        String metricsName = ptoneWidgetParam.getMetricsNameByKey(metricsKey);
        if (metricsNameMap.containsKey(metricsName)) {
          int index = metricsNameMap.get(metricsName) + 1;
          metricsNameMap.put(metricsName, index);
          metricsName = metricsName + " (" + index + ")";
        } else {
          metricsNameMap.put(metricsName, 1);
        }
        Object valueObj = totalRowList.get(mIndex++);
        String valueStr = valueObj != null ? String.valueOf(valueObj) : "0";
        if (valueStr.contains("%")) {
          valueStr.replaceAll("%", "");
        }
        Number value = (StringUtil.isBigDecimal(valueStr) ? new BigDecimal(valueStr) : 0);

        String dataType = md.getDataType();
        ;
        String dataFormat = md.getDataFormat();
        String unit = md.getUnit();
        String type = md.getType();
        String calculateType = md.getCalculateType();

        if (!PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equals(type)) {
          UserConnectionSourceTableColumn column = userConnectionSourceTableColumnService
              .getAvailableColumn(md.getId());
          if (column != null) {
            dataType = column.getDataType();
            dataFormat = column.getDataFormat();
            unit = column.getUnit();
            if (StringUtil.isBlank(unit)) {
              unit = CommonDataUtil.getUnit(dataType, dataFormat, calculateType);
            }
          }
        }

        // 修正计算类型的单位
        unit = CommonDataUtil.fixUnitByCalculateType(unit, calculateType);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", md.getId());
        map.put("code", md.getCode());
        map.put("name", md.getName());
        map.put("showName", metricsName);
        map.put("key", metricsKey);
        map.put("value", value);
        map.put("unit", unit);

        metricsTotalsMap.put(metricsKey, map);
      }
    }

    return metricsTotalsMap;
  }

  /**
   * 根据modelData获取Api类型数据源的指标总量(salesforce使用自己的逻辑)
   * 
   * @param modelData
   * @param ptoneWidgetParam
   * @return
   * @date: 2016年7月27日
   * @author peng.xu
   */
  public Map<String, Map<String, Object>> getApiMetricsTotalsMap(ModelData modelData,
      PtoneWidgetParam ptoneWidgetParam) {
    return this.getMetricsTotalsMap(modelData, ptoneWidgetParam, true);
  }

  /**
   * 获取modelData获取model类型数据源的指标总量
   * 
   * @param modelData
   * @param ptoneWidgetParam
   * @return
   * @date: 2016年7月27日
   * @author peng.xu
   */
  public Map<String, Map<String, Object>> getModelMetricsTotalsMap(ModelData modelData,
      PtoneWidgetParam ptoneWidgetParam) {
    return this.getMetricsTotalsMap(modelData, ptoneWidgetParam, false);
  }

  /**
   * 修正数据中的特殊符号 (for database)
   * 
   * @date: 2016年7月26日
   * @author peng.xu
   */
  public static String fixColumn(String columnName, String[] strList, String dbCode) {
    String fixColumn = columnName;
    if (strList != null) {
      for (String str : strList) {
        fixColumn = DataBaseConfig.replace(dbCode, fixColumn, str, "");
      }
    }
    return fixColumn;
  }

  /**
   * 根据数据类型修正数据中的特殊符号 (for database)
   * 
   * @date: 2016年7月26日
   * @author peng.xu
   */
  public static String fixColumnByDataType(String colName, String dataType, String dsCode,boolean isNeedFix) {
    if(!isNeedFix)
    {
      return  colName;
    }
    String fixColName = colName;
    if (PtoneMetricsDimension.DATA_TYPE_PERCENT.equals(dataType)) {
      String[] strList = { " ", "%" };
      fixColName = CommonDataUtil.fixColumn(colName, strList, dsCode);
    } else if (PtoneMetricsDimension.DATA_TYPE_CURRENCY.equals(dataType)) {
      String[] strList = { " ", ",", "$", "€", "¥", "円", "JPY", "USD", "US", "RMB", "元", "￥", "CNY" };
      fixColName = CommonDataUtil.fixColumn(colName, strList, dsCode);
    } else if (PtoneMetricsDimension.DATA_TYPE_DURATION.equals(dataType)) {
      String[] strList = { " ", ",", "s", "m", "h" };
      fixColName = CommonDataUtil.fixColumn(colName, strList, dsCode);
    } else {
      String[] strList = { " ", "," };
      fixColName = CommonDataUtil.fixColumn(colName, strList, dsCode);
    }
    return fixColName;
  }

  /**
   * 构建指标的计算方式列 (standarModelData使用自己的逻辑)
   * 
   * @return
   * @date: 2016年8月4日
   * @author peng.xu
   */
  public static String buildMetricsCalculate(String column, PtoneMetricsDimension metrics,
      String metricsKey, String dsCode, PtoneWidgetParam ptoneWidgetParam) {
    String fixColumn = column;
    if (!Constants.validate.equals(metrics.getIsContainsFunc())) {
      String calculateType = DataBaseConfig.FUNC_SUM; // 默认计算类型为sum
      String dataType = null;
      PtoneMetricsDimension md = ptoneWidgetParam.getMetricsByKey(metricsKey);
      if (md != null && StringUtil.isNotBlank(md.getCalculateType())) {
        calculateType = md.getCalculateType();
        dataType = md.getDataType();
      }

      // 如果计算百分比类型数据的方差、标准差需要对数据进行修正（转为小数）
      if (PtoneMetricsDimension.DATA_TYPE_PERCENT.equals(dataType)
          && (DataBaseConfig.FUNC_STDEV.equals(calculateType) || DataBaseConfig.FUNC_VARIANCE
              .equals(calculateType))) {
        column = DataBaseConfig.toNumber(dsCode, column);
        column = "((" + column + ")/100)";
        fixColumn = DataBaseConfig.buildCalculateColumn(dsCode, column, calculateType, false);
      } else {
        //直接将isNeedConvertData 设置为false
        fixColumn = DataBaseConfig.buildCalculateColumn(dsCode, column, calculateType, false);
      }
    }
    return fixColumn;
  }

  /**
   * model类型单数据源的复合指标column构建(所有的复合指标在此处都必须是有效的)
   * 
   * @param compoundMetrics
   * @param fixData
   *          严格模式修正数据
   * @return
   * @date: 2016年7月26日
   * @author peng.xu
   */
  public String fixCompoundMetricsColumn(UserCompoundMetricsDimensionDto compoundMetrics,
      boolean fixData) {
    String column = compoundMetrics.getQueryCode();
    String dsCode = compoundMetrics.getDsCode();

    // 使用原始指标的列替换queryCode中的formulaId
    List<UserCompoundMetricsDimensionObject> metricsList = compoundMetrics.getObjects();
    for (UserCompoundMetricsDimensionObject metrics : metricsList) {
      if (compoundMetrics.usingMetricsObject(metrics)) {
        String formulaId = metrics.getFormulaId();
        String dataType = metrics.getDataType();
        String srcColName = DataBaseConfig.encloseColumn(dsCode, metrics.getCode());

        if (fixData) {
          srcColName = CommonDataUtil.fixColumnByDataType(srcColName, dataType, dsCode,false);
          srcColName = DataBaseConfig.toNumber(dsCode, srcColName);
        }

        String queryCodeColumn = UserCompoundMetricsDimensionDto.getQueryCodeColumnKey(formulaId);
        column = column.replace(queryCodeColumn, srcColName);
      }
    }

    return column;
  }

  /**
   * 修正指标sql查询的column
   * 
   * @param colName
   * @param metrics
   * @param fixData
   *          严格模式修正数据
   * @return
   * @date: 2016年7月27日
   * @author peng.xu
   */
  public String fixMetricsSqlColumn(String colName, PtoneMetricsDimension metrics,
      PtoneWidgetParam ptoneWidgetParam, boolean fixData) {
    String fixColName = colName;
    if (PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equalsIgnoreCase(metrics.getType())) {
      String metricsId = metrics.getId();
      UserCompoundMetricsDimensionDto compoundMetrics = ptoneDsService.getCompoundMetrics(
          metricsId, ptoneWidgetParam.getCompoundMetricsMap());
      fixColName = this.fixCompoundMetricsColumn(compoundMetrics, fixData);
    } else {
      fixColName = CommonDataUtil.fixColumnByDataType(colName, metrics.getDataType(),
          ptoneWidgetParam.getDsCode(),false);
    }
    return fixColName;
  }

  /**
   * 根据dataType、dataFormat、calculateType获取单位
   * 
   * @return
   * @date: 2016年7月26日
   * @author peng.xu
   */
  public static String getUnit(String dataType, String dataFormat, String calculateType) {
    // count等没有单位
    if (DataBaseConfig.FUNC_COUNTA.equalsIgnoreCase(calculateType)
        || DataBaseConfig.FUNC_COUNTUNIQUE.equalsIgnoreCase(calculateType)
        || DataBaseConfig.FUNC_STDEV.equalsIgnoreCase(calculateType)
        || DataBaseConfig.FUNC_VARIANCE.equalsIgnoreCase(calculateType)) {
      return "";
    }
    return PtoneMetricsDimension.getUnitByDataTypeAndFormat(dataType, dataFormat);
  }

  /**
   * 根据计算类型修正单位
   * 
   * @param unit
   * @param calculateType
   * @return
   * @date: 2016年8月9日
   * @author peng.xu
   */
  public static String fixUnitByCalculateType(String unit, String calculateType) {
    // count等没有单位
    if (DataBaseConfig.FUNC_COUNTA.equalsIgnoreCase(calculateType)
        || DataBaseConfig.FUNC_COUNTUNIQUE.equalsIgnoreCase(calculateType)
        || DataBaseConfig.FUNC_STDEV.equalsIgnoreCase(calculateType)
        || DataBaseConfig.FUNC_VARIANCE.equalsIgnoreCase(calculateType)) {
      return "";
    }
    return unit;
  }

  /**
   * 修正count之类函数列的数据类型
   * 
   * @param dataType
   *          原数据类型
   * @param calculateType
   *          函数类型
   * @return
   */
  public static String getDataType(String dataType, String calculateType) {
    if (DataBaseConfig.FUNC_COUNTA.equalsIgnoreCase(calculateType)
        || DataBaseConfig.FUNC_COUNTUNIQUE.equalsIgnoreCase(calculateType)) {
      return PtoneMetricsDimension.DATA_TYPE_NUMBER;
    }
    return dataType;
  }

  /**
   * 根据MetricsData构建model类型数据源返回数据的 dataTypeMap、dataFormatMap、unitMap
   * 
   * @param dataTypeMap
   * @param dataFormatMap
   * @param unitMap
   * @date: 2016年7月27日
   * @author peng.xu
   */
  public void buildModelDataTypeFormatUnitMap(Map<String, String> dataTypeMap,
      Map<String, String> dataFormatMap, Map<String, String> unitMap,
      List<PtoneMetricsDimension> metricsData) {
    unitMap = (unitMap != null ? unitMap : new HashMap<String, String>());
    dataTypeMap = (dataTypeMap != null ? dataTypeMap : new HashMap<String, String>());
    dataFormatMap = (dataFormatMap != null ? dataFormatMap : new HashMap<String, String>());
    for (PtoneMetricsDimension md : metricsData) {
      String metricsKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md);
      String dataType = md.getDataType();
      String dataFormat = md.getDataFormat();
      String calculateType = md.getCalculateType();
      String unit = md.getUnit();
      // 非复合指标，从库中查询获取
      if (!PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equalsIgnoreCase(md.getType())) {
        UserConnectionSourceTableColumn column = userConnectionSourceTableColumnService
            .getAvailableColumn(md.getId());
        if (column != null) {
          dataType = column.getDataType();
          dataFormat = column.getDataFormat();
        }
      }

      if (!StringUtil.isBlank(dataType)) {
        dataType = CommonDataUtil.getDataType(dataType, calculateType);
      }

      unit = CommonDataUtil.getUnit(dataType, dataFormat, calculateType);

      dataTypeMap.put(metricsKey, dataType);
      dataFormatMap.put(metricsKey, dataFormat);
      unitMap.put(metricsKey, unit);
    }
  }

  /**
   * 构建时间范围列表
   * 
   * @param startDate
   * @param endDate
   * @return
   * @date: 2016年7月27日
   * @author peng.xu
   */
  public static List<String> buildDataRangeList(String startDate, String endDate) {
    List<String> dateRangeList = new ArrayList<String>();
    String dateFormat = "MM/dd";
    if (StringUtil.isNotBlank(startDate) && StringUtil.isNotBlank(endDate)) {
      dateRangeList.add(JodaDateUtil.parseDateFormate(startDate, "yyyy-MM-dd", dateFormat,
          Locale.US));
      dateRangeList
          .add(JodaDateUtil.parseDateFormate(endDate, "yyyy-MM-dd", dateFormat, Locale.US));
    } else {
      dateRangeList.add("");
      dateRangeList.add("");
    }
    return dateRangeList;
  }

  /**
   * 通过复制的方式将 PtoneMetricsDimension 转换为 PtoneMetricsDimension
   * 
   * @param d
   *          PtoneMetricsDimension
   * @return PtoneMetricsDimension
   */
  public static PtoneMetricsDimension copyUserConnectionMetricsDimensionToPtoneMetricsDimension(
      UserConnectionSourceTableColumn column) {
    PtoneMetricsDimension item = new PtoneMetricsDimension();
    BeanUtils.copyProperties(column, item);
    item.setId(column.getColId());
    return item;
  }

  /**
   * 修正model类型数据源维度列表
   * 
   * @param metricsData
   * @param metricsDimensionMap
   * @param ptoneWidgetParam
   * @return
   * @date: 2016年7月27日
   * @author peng.xu
   */
  public List<PtoneMetricsDimension> fixModelDimensionList(
      List<PtoneMetricsDimension> dimensionData, PtoneWidgetParam ptoneWidgetParam,
      PtoneMetricsDimension xAxisDateDimension,
      Map<String, PtoneMetricsDimension> metricsDimensionMap) {
    List<PtoneMetricsDimension> dimensionsList = new ArrayList<PtoneMetricsDimension>();
    List<String> dimensionsKeyList = ptoneWidgetParam.getDimensionsKeyList();
    List<String> fixDimensionsKeyList = new ArrayList<String>();
    for (PtoneMetricsDimension dd : dimensionData) {
      String dKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(dd);
      if (dimensionsKeyList.contains(dKey)) {
        UserConnectionSourceTableColumn column = userConnectionSourceTableColumnService
            .getAvailableColumn(dd.getId());
        if (column != null) {
          PtoneMetricsDimension dimensionObj = CommonDataUtil
              .copyUserConnectionMetricsDimensionToPtoneMetricsDimension(column);
          dimensionObj.setUuid(dd.getUuid());
          dimensionObj.setDatePeriod(dd.getDatePeriod());
          dimensionsList.add(dimensionObj);
          metricsDimensionMap.put(dimensionObj.getCode(), dimensionObj);
          // 如果用户没有选择时间维度
          if (dimensionsKeyList.get(0).equals(dKey)
              && PtoneMetricsDimension.isDateDimension(column.getDataType())) {
            // 设置用户设置的第一个时间维度为x轴的时间维度
            if (xAxisDateDimension == null) {
              xAxisDateDimension = dimensionObj;
            }
          }
          fixDimensionsKeyList.add(dKey);
        }
      }
    }
    ptoneWidgetParam.setDimensionsKeyList(fixDimensionsKeyList);
    return dimensionsList;
  }

  /**
   * 修正model类型数据源指标列表
   * 
   * @param metricsData
   * @param metricsDimensionMap
   * @param ptoneWidgetParam
   * @return
   * @date: 2016年7月27日
   * @author peng.xu
   */
  public List<PtoneMetricsDimension> fixModelMetricsList(List<PtoneMetricsDimension> metricsData,
      PtoneWidgetParam ptoneWidgetParam, Map<String, PtoneMetricsDimension> metricsDimensionMap) {
    List<PtoneMetricsDimension> metricsList = new ArrayList<PtoneMetricsDimension>();
    List<String> metricsKeyList = ptoneWidgetParam.getMetricsKeyList();
    List<String> fixMetricsKeyList = new ArrayList<String>();
    for (PtoneMetricsDimension md : metricsData) {
      String mKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md);
      if (metricsKeyList.contains(mKey)) {
        String type = md.getType();
        if (PtoneMetricsDimension.TYPE_COMPOUND_METRICS.equalsIgnoreCase(type)) {
          // 复合指标的处理
          String metricsId = md.getId();
          UserCompoundMetricsDimensionDto compoundMetrics = ptoneDsService.getCompoundMetrics(
              metricsId, ptoneWidgetParam.getCompoundMetricsMap());
          if (compoundMetrics != null && Constants.validateInt == compoundMetrics.getIsValidate()) {
            PtoneMetricsDimension pmd = compoundMetrics.parseToPtoneMetricsDimension();
            pmd.setUuid(md.getUuid());
            metricsList.add(pmd);
            metricsDimensionMap.put(pmd.getCode(), pmd);
            fixMetricsKeyList.add(mKey);
          }
        } else {
          UserConnectionSourceTableColumn column = userConnectionSourceTableColumnService
              .getAvailableColumn(md.getId());
          if (column != null) {
            PtoneMetricsDimension metricsObj = CommonDataUtil
                .copyUserConnectionMetricsDimensionToPtoneMetricsDimension(column);
            metricsObj.setUuid(md.getUuid());
            metricsList.add(metricsObj);
            metricsDimensionMap.put(metricsObj.getCode(), metricsObj);
            fixMetricsKeyList.add(mKey);
          }
        }
      }
    }
    ptoneWidgetParam.setMetricsKeyList(fixMetricsKeyList);
    return metricsList;
  }

  /**
   * 判断是否更根时间粒度自动生成的时间维度为x轴 （不能判断是否使用时间轴） （对应代码： GA: L173 (mulitYUseDatePeriod
   * || (!judgeMulitY && GraphType.LINE.equals(graph)))）
   * 
   * @param ptoneWidgetParam
   * @return
   * @date: 2016年7月29日
   * @author peng.xu
   */
  public static boolean useAutoDatetimeAxis(PtoneWidgetParam ptoneWidgetParam) {
    String dsCode = ptoneWidgetParam.getDsCode();
    // 只有GA和ptengine需要自动增加时间维度
    if (!DsConstants.DS_CODE_GA.equals(dsCode) && !DsConstants.DS_CODE_PTENGINE.equals(dsCode)) {
      return false;
    }

    String graphType = ptoneWidgetParam.getGraphType();
    GraphType graph = GraphType.valueOf(graphType.toUpperCase());
    List<String> dimensionsKeyList = ptoneWidgetParam.getDimensionsKeyList();
    boolean judgeMulitY = ptoneWidgetParam.isJudgeMulitY(); // 是否判断双轴
    // 未开启双轴：为line时使用datetime类型x轴
    // 开启双轴时，如果用户没有设置维度则，则时间粒度生效， 增加时间维度, 使用datetime类型x轴，选择维度则时间粒度不生效
    boolean useAutoDatetimeAxis = (judgeMulitY
        && (GraphType.LINE.equals(graph) || GraphType.COLUMN.equals(graph)) && (dimensionsKeyList == null || dimensionsKeyList
        .size() == 0)) || (!judgeMulitY && GraphType.LINE.equals(graph));

    return useAutoDatetimeAxis;
  }

  /**
   * 将过滤器操作符转换为sql对应操作符的表达式（此处不处理数据格式转换，只是拼接表达式） TODO：
   * 需要优化将model、bigquery、standerModel合过来
   * 
   * @param code
   * @return
   * @date: 2016年7月29日
   * @author peng.xu
   */
  public static String parseSqlFilterExp(String column, String opCode, String value, String dsCode) {
    StringBuilder sqlExpBuilder = new StringBuilder("");
    String op = opCode;
    if (CommonDataUtil.FILTER_OP_CONTAIN.equals(op)
        || CommonDataUtil.FILTER_OP_UPCASE_CONTAIN.equals(op)
        || CommonDataUtil.FILTER_OP_CONTAIN_SUBSTR.equals(op)) {
      sqlExpBuilder.append(" ").append(column).append(" like '%").append(value).append("%' ");
    } else if (CommonDataUtil.FILTER_OP_NOT_CONTAIN.equals(op)
        || CommonDataUtil.FILTER_OP_NOT_CONTAIN_SUB.equals(op)) {
      sqlExpBuilder.append(" ").append(column).append(" not like '%").append(value).append("%' ");
    } else if (CommonDataUtil.FILTER_OP_REGEXP_CONTAIN.equals(op)) {
      // TODO:
    } else if (CommonDataUtil.FILTER_OP_REQEXP__NOT_CONTAIN.equals(op)) {
      // TODO:
    } else if (CommonDataUtil.FILTER_OP_START.equals(op)) {
      sqlExpBuilder.append(" ").append(column).append(" like '").append(value).append("%' ");
    } else if (CommonDataUtil.FILTER_OP_NOT_START.equals(op)) {
      sqlExpBuilder.append(" ").append(column).append(" not like '").append(value).append("%' ");
    } else if (CommonDataUtil.FILTER_OP_END.equals(op)) {
      sqlExpBuilder.append(" ").append(column).append(" like '%").append(value).append("' ");
    } else if (CommonDataUtil.FILTER_OP_NOT_END.equals(op)) {
      sqlExpBuilder.append(" ").append(column).append(" not like '%").append(value).append("' ");
    } else if (CommonDataUtil.FILTER_OP_IN.equals(op)
        || CommonDataUtil.FILTER_OP_UPCASE_IN.equals(op)
        || CommonDataUtil.FILTER_OP_NOT_IN.equals(op)) {
      op = CommonDataUtil.FILTER_OP_NOT_IN.equals(op) ? " not in " : " in ";
      List<String> valueList = StringUtil.splitToList(value, ",");
      sqlExpBuilder.append(" ").append(column).append(op).append(" ( '");
      sqlExpBuilder.append(StringUtil.join(valueList, "', '"));
      sqlExpBuilder.append("' ) ");
    } else if (CommonDataUtil.FILTER_OP_EQUAL.equals(op)
        || CommonDataUtil.FILTER_OP_UPCASE_EQUAL.equals(op)
        || CommonDataUtil.FILTER_OP_EQ_2.equals(op) || CommonDataUtil.FILTER_OP_EQ_3.equals(op)) {
      op = DataBaseConfig.parseOperator(dsCode, DataBaseConfig.DB_OPERATOR_EQ);
      sqlExpBuilder.append(" ").append(column).append(op).append(" '" + value + "' ");
    } else if (CommonDataUtil.FILTER_OP_NOT_EQUAL.equals(op)
        || CommonDataUtil.FILTER_OP_UPCASE_NOT_EQUAL_CODE.equals(op)
        || CommonDataUtil.FILTER_OP_NE_3.equals(op) || CommonDataUtil.FILTER_OP_NE.equals(op)
        || CommonDataUtil.FILTER_OP_NE_2.equals(op)) {
      op = DataBaseConfig.parseOperator(dsCode, DataBaseConfig.DB_OPERATOR_NE);
      sqlExpBuilder.append(" ").append(column).append(op).append(" '" + value + "' ");
    } else { // = 、> 、 >= 、 < 、 <=
      if (CommonDataUtil.FILTER_OP_UPCASE_GREATER_THAN.equals(op)) {
        op = CommonDataUtil.FILTER_OP_GT;
      } else if (CommonDataUtil.FILTER_OP_UPCASE_GREATER_THAN_OR_EQUAL.equals(op)) {
        op = CommonDataUtil.FILTER_OP_GE;
      } else if (CommonDataUtil.FILTER_OP_UPCASE_LESS_THAN.equals(op)) {
        op = CommonDataUtil.FILTER_OP_LT;
      } else if (CommonDataUtil.FILTER_OP_UPCASE_LESS_THAN_OR_EQUAL.equals(op)) {
        op = CommonDataUtil.FILTER_OP_LE;
      }

      op = DataBaseConfig.parseOperator(dsCode, op);
      if (StringUtil.isNumber(value)) {
        sqlExpBuilder.append(" ").append(column).append(op).append(" " + value + " ");
      } else {
        sqlExpBuilder.append(" ").append(column).append(op).append(" '" + value + "' ");
      }
    }

    return sqlExpBuilder.toString();
  }

  /**
   * 判断是否为历史数据取数
   * 
   * @param dsCode
   * @param endDate
   * @return
   * @date: 2016年8月27日
   * @author peng.xu
   */
  public boolean isHistoryData(String dsCode, String endDate) {
    if (DsConstants.isOnlyHistoryDataDs(dsCode)) {
      return true;
    } else if (DsConstants.isOnlyRealtimeDataDs(dsCode)) {
      return false;
    } else {
      String today = JodaDateUtil.getCurrentDate();
      return StringUtil.isNotBlank(endDate) && !today.equals(endDate); // 非当天数据为历史数据
    }
  }

  /**
   * 从系统参数获取数据源是否使用数据缓存
   * 
   * @param dsCode
   * @param isHistoryData
   *          是否为历史数据
   * @return
   * @date: 2016年8月26日
   * @author peng.xu
   */
  public boolean isUseDsDataCache(String dsCode, boolean isHistoryData) {
    if (isHistoryData) {
      return sysConfigParamCache.isUseDsHistoryDataCache(dsCode);
    } else {
      return sysConfigParamCache.isUseDsRealtimeDataCache(dsCode);
    }
  }

  /**
   * 获取对应WidgetData的缓存时间（秒数）
   * 
   * @return
   * @date: 2016年8月27日
   * @author peng.xu
   */
  public int getDsDataCacheTime(String dsCode, boolean isHistoryData) {
    if (isHistoryData) {
      return sysConfigParamCache.getDsHistoryDataCacheTime(dsCode);
    } else {
      return sysConfigParamCache.getDsRealtimeDataCacheTime(dsCode);
    }
  }

  /**
   * 构建关系型数据，connection的缓存key字符串
   * 
   * @param userConnection
   * @return
   * @date: 2016年8月30日
   * @author peng.xu
   */
  public String buildDbConnectionKey(UserConnection userConnection) {
    if (userConnection != null) {
      StringBuilder connectionInfo = new StringBuilder();
      String connectionConfig = userConnection.getConfig();
      if (StringUtil.isNotBlank(connectionConfig)) {
        JSONObject configMap = JSON.parseObject(connectionConfig);
        Map<String, String> dbConnectionKeyMap = new LinkedHashMap<String, String>();
        dbConnectionKeyMap.put("dataBaseType", configMap.getString("dataBaseType"));
        dbConnectionKeyMap.put("host", configMap.getString("host"));
        dbConnectionKeyMap.put("port", configMap.getString("port"));
        dbConnectionKeyMap.put("user", configMap.getString("user"));
        dbConnectionKeyMap.put("dataBaseName", configMap.getString("dataBaseName"));
        dbConnectionKeyMap.put("sshHost", configMap.getString("sshHost"));
        dbConnectionKeyMap.put("sshPort", configMap.getString("sshPort"));
        dbConnectionKeyMap.put("sshUser", configMap.getString("sshUser"));
        for (String val : dbConnectionKeyMap.values()) {
          connectionInfo.append(val).append("|");
        }
        return connectionInfo.toString();
      }
    }
    return "";
  }

  /**
   * 计算某年某周的开始日期
   * 
   * @param year
   *          格式 yyyy
   * @param week
   *          1到52或者53
   * @param dayOfWeek
   *          周起始日 FIRST_DAY_OF_WEEK_SUNDAY FIRST_DAY_OF_WEEK_MONDAY
   * @return 日期，格式为yyyy-MM-dd
   */
  public static String getYearWeekFirstDay(int year, int week, int dayOfWeek) {
    return CommonDataUtil.getYearWeekFirstDay(year, week, dayOfWeek, "yyyy-MM-dd");
  }

  /**
   * 计算某年某周的开始日期
   * 
   * @param year
   *          格式 yyyy
   * @param week
   *          1到52或者53
   * @param dayOfWeek
   *          周起始日 PtoneDateUtil.FIRST_DAY_OF_WEEK_SUNDAY、
   *          FIRST_DAY_OF_WEEK_MONDAY
   * @return 日期,格式为 dateFormat
   */
  public static String getYearWeekFirstDay(int year, int week, int dayOfWeek, String dateFormat) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, 0, 0, 0, 0, 0);// 清空日月时分秒
    if (dayOfWeek == PtoneDateUtil.FIRST_DAY_OF_WEEK_MONDAY) {
      // ga:ISOWeek（根据ISOweek规则计算周数）
      cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置每周的第一天
      cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 每周的第一天为周一
      cal.setMinimalDaysInFirstWeek(4); // 设置每周最少为4天(ISO week：第一周至少有4天在1月里面。)
    } else {
      // ga:week
      cal.setFirstDayOfWeek(Calendar.SUNDAY); // 设置每周的第一天
      cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);// 每周的第一天为周日
    }
    // 上面两句代码配合，才能实现，每年度的第一个周，是包含第一个星期一的那个周。
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.WEEK_OF_YEAR, week);
    // 分别取得当前日期的年、月、日
    return new SimpleDateFormat(dateFormat).format(cal.getTime());
  }

}
