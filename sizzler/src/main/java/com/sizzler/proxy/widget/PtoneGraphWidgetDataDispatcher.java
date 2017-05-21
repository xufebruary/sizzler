package com.sizzler.proxy.widget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sizzler.cache.SysConfigParamCache;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.sys.SysConfigParam;
import com.sizzler.proxy.dispatcher.GraphType;
import com.sizzler.proxy.dispatcher.PtoneGraphWidgetDataDesc;
import com.sizzler.proxy.dispatcher.PtoneVariableData;
import com.sizzler.proxy.dispatcher.PtoneWidgetData;
import com.sizzler.system.Constants;

@Component
public class PtoneGraphWidgetDataDispatcher {

  @Autowired
  private SysConfigParamCache sysConfigParamCache;
  

  public static final int PIE_TOP_N = 100; // 9 --> 100

  public static final int COLUMN_TOP_N = 100; // 10 --> 100
  
  public static final int LINE_TOP_N = 1000;
  
  public static final int LEGEND_MAX_COUNT = 10;
  
  /**
   * 图例的排序顺序： 优先按照指标排序，然后按照下面的设置规则排序，如果不设置默认按照valueDesc排序，设置为其他值则不排序
   */
  public static final String LEGEND_SORT_TYPE_VALUE_DESC = "valueDesc";// 值总量倒序
  public static final String LEGEND_SORT_TYPE_STRING_ASC = "stringAsc"; // 字符串升序


  /**
   * 格式化table类型数据
   * 
   * @param ptoneGraphWidgetDataDesc
   * @return
   */
  public PtoneWidgetData parseTableWidgetData(PtoneGraphWidgetDataDesc ptoneGraphWidgetDataDesc) {
    PtoneWidgetData ptoneWidgetData = ptoneGraphWidgetDataDesc.getPtoneWidgetData();

    // 如果ptoneWidgetData只包含一个variable数据，则直接返回
    if (ptoneWidgetData == null || ptoneWidgetData.getData() == null
        || ptoneWidgetData.getData().size() <= 0) {
      return ptoneWidgetData;
    }

    // 合并所有变量为一个变量
    PtoneVariableData fixVarData = null;
    List<List<Object>> fixRows = new ArrayList<List<Object>>();
    List<String> dimensionsList = new ArrayList<String>();
    List<String> metricsList = new ArrayList<String>();

    List<String> metricsKeyList = new ArrayList<String>();
    List<String> metricsIdList = new ArrayList<String>();
    List<String> metricsNameList = new ArrayList<String>();

    List<Object> thList = new ArrayList<Object>();
    Map<String, List<Object>> rowKeyMap = new LinkedHashMap<String, List<Object>>();
    Map<Object, Object> tdCellMap = new HashMap<Object, Object>();
    Map<String, String> metricsUnitMap = new HashMap<String, String>();
    Map<String, String> metricsDataTypeMap = new HashMap<String, String>();
    Map<String, String> metricsDataFormatMap = new HashMap<String, String>();

    // 处理单次查询存在相同指标情况,修正metricsName
    Map<String, Integer> metricsNameMap = new HashMap<String, Integer>();

    for (Object data : ptoneWidgetData.getData()) {
      PtoneVariableData varData = (PtoneVariableData) data;
      if (fixVarData == null) {
        fixVarData = varData; // 初始化fixVarData(TODO:此处metrics、metricsId等值没有处理)
      }


      metricsKeyList.add(varData.getMetricsKey());
      metricsIdList.add(varData.getMetricsId());
      metricsNameList.add(varData.getMetricsName());

      metricsUnitMap.putAll(varData.getUnitMap());// 合并所有的单位映射map
      metricsDataTypeMap.putAll(varData.getDataTypeMap());// 合并所有的数据类型映射map
      metricsDataFormatMap.putAll(varData.getDataFormatMap());

      List<List<Object>> rows = varData.getRows();
      List<Object> thRow = null;
      for (List<Object> row : rows) {
        // 第一行为表头行,第二行开始为数据行
        if (thRow == null) {
          thRow = row;

          // 合并维度列名
          // String dimensionsId = varData.getDimensionsId();
          String dimensionsKey = varData.getDimensionsKey();
          if (StringUtil.isNotBlank(dimensionsKey) && dimensionsList.isEmpty()) {
            List<String> varDimensionsKeyList = StringUtil.splitToList(dimensionsKey, ",");
            for (int i = 0; i < varDimensionsKeyList.size() && i < thRow.size(); i++) {
              String thName = (String) thRow.get(i);
              // 每个variableData的维度信息都一样，此处可以不写
              dimensionsList.add(thName);
            }
          }

          // 合并、修正重复的指标列名
          for (int i = dimensionsList.size(); i < thRow.size(); i++) {
            String thName = (String) thRow.get(i);
            if (metricsList.contains(thName)) {
              int index = metricsNameMap.get(thName) + 1;
              metricsNameMap.put(thName, index);
              thName = thName + " (" + index + ")";
            } else {
              metricsNameMap.put(thName, 1);
            }
            thRow.set(i, thName);
            metricsList.add(thName);
          }

        } else {
          String rowKey = "";
          List<Object> rowKeyItem = new ArrayList<Object>();
          String tdCellKey = "";
          // 循环维度列
          for (int i = 0; i < dimensionsList.size(); i++) {
            // 判断是维度列还是指标列
            rowKey += row.get(i);
            rowKeyItem.add(row.get(i));
          }
          if (!rowKeyMap.containsKey(rowKey)) {
            rowKeyMap.put(rowKey, rowKeyItem);
          }
          // 循环指标列
          for (int i = dimensionsList.size(); i < row.size(); i++) {
            tdCellKey = rowKey + thRow.get(i);
            tdCellMap.put(tdCellKey, row.get(i));
          }
        }
      }
    }

    // 重新构建table数据
    thList.addAll(dimensionsList);
    thList.addAll(metricsList);
    fixRows.add(thList);
    for (Map.Entry<String, List<Object>> entry : rowKeyMap.entrySet()) {
      String rowKey = entry.getKey();
      List<Object> fixRow = new ArrayList<Object>();
      fixRow.addAll(entry.getValue());
      for (String metrics : metricsList) {
        String tdCellKey = rowKey + metrics;
        Object tdCellValue = tdCellMap.get(tdCellKey);
        fixRow.add(tdCellValue == null ? 0 : tdCellValue);
      }
      fixRows.add(fixRow);
    }
    fixVarData.setRows(fixRows);
    fixVarData.setUnitMap(metricsUnitMap);
    fixVarData.setDataTypeMap(metricsDataTypeMap);
    fixVarData.setDataFormatMap(metricsDataFormatMap);

    fixVarData.setMetricsKey(StringUtil.join(metricsKeyList, ","));
    fixVarData.setMetricsId(StringUtil.join(metricsIdList, ","));
    fixVarData.setMetricsName(StringUtil.join(metricsNameList, ","));

    List<Object> widgetData = new ArrayList<Object>();
    widgetData.add(fixVarData);

    // 构建新的 PtoneWidgetData
    PtoneWidgetData fixPtoneWidgetData = ptoneWidgetData;
    fixPtoneWidgetData.setWidgetId(ptoneWidgetData.getWidgetId());
    fixPtoneWidgetData.setData(widgetData); // 重置widget数据

    return fixPtoneWidgetData;
  }

  /**
   * 格式化Line类型数据（对应图形包括：line、area）：限制显示曲线数量 topN
   * 
   * @param ptoneGraphWidgetDataDesc
   * @return
   */
  public PtoneWidgetData parseLineWidgetData(PtoneGraphWidgetDataDesc ptoneGraphWidgetDataDesc) {
    PtoneWidgetData ptoneWidgetData = ptoneGraphWidgetDataDesc.getPtoneWidgetData();
    ptoneWidgetData = parseVariablesMetricsTopN(ptoneWidgetData, 1, LEGEND_MAX_COUNT);
    ptoneWidgetData = mergeCategories(ptoneWidgetData);
    ptoneWidgetData =
        parseVariableRowsTopN(ptoneWidgetData, 0, 1, LINE_TOP_N, false,
            ptoneGraphWidgetDataDesc.getGraphType());
    return ptoneWidgetData;
  }

  /**
   * 格式化Pie类型数据（对应图形包括：pie）：限制显示曲线数量 topN( top9 + others)
   * 
   * @param ptoneGraphWidgetDataDesc
   * @return
   */
  public PtoneWidgetData parsePieWidgetData(PtoneGraphWidgetDataDesc ptoneGraphWidgetDataDesc) {
    PtoneWidgetData ptoneWidgetData = ptoneGraphWidgetDataDesc.getPtoneWidgetData();
    ptoneWidgetData.setOrderType(PtoneMetricsDimension.SORT_BY_NUMBER); // 修改为按照数据量排序
    ptoneWidgetData = mergeCategories(ptoneWidgetData);
    ptoneWidgetData =
        parseVariableRowsTopN(ptoneWidgetData, 0, 1, PIE_TOP_N, true,
            ptoneGraphWidgetDataDesc.getGraphType()); // 数据索引为1
    return ptoneWidgetData;
  }

  /**
   * 格式化Column类型数据（对应图形包括：column、bar）：限制显示曲线数量 topN ( top10 )
   * 
   * @param ptoneGraphWidgetDataDesc
   * @return
   */
  public PtoneWidgetData parseColumnWidgetData(PtoneGraphWidgetDataDesc ptoneGraphWidgetDataDesc) {
    PtoneWidgetData ptoneWidgetData = ptoneGraphWidgetDataDesc.getPtoneWidgetData();
    ptoneWidgetData = parseVariablesMetricsTopN(ptoneWidgetData, 1, LEGEND_MAX_COUNT);
    ptoneWidgetData = mergeCategories(ptoneWidgetData);
    ptoneWidgetData =
        parseVariableRowsTopN(ptoneWidgetData, 0, 1, COLUMN_TOP_N, false,
            ptoneGraphWidgetDataDesc.getGraphType());
    return ptoneWidgetData;
  }

  /**
   * categories合并
   * @return
   */
  public PtoneWidgetData mergeCategories(PtoneWidgetData ptoneWidgetData) {
    List<String> categories = new ArrayList<String>();
    for (Object data : ptoneWidgetData.getData()) {
      PtoneVariableData variableData = (PtoneVariableData) data;
      for (List<Object> row : variableData.getRows()) {
        String category = (String) row.get(0);
        if (!categories.contains(category)) {
          categories.add(category);
        }
      }
    }
    ptoneWidgetData.setCategories(categories);

    return ptoneWidgetData;
  }

  /**
   * 限制ptoneWidgetData中variable数据行数 topN（总条数的topN）
   * 
   * @param ptoneGraphWidgetDataDesc
   * @param valueIndex 数据值在ptoneVariableData的rows数据row中的的索引（用于获取数据值）
   * @return
   */
  private PtoneWidgetData parseVariablesTopN(PtoneWidgetData ptoneWidgetData, final int valueIndex,
      int topN) {
    // 如果ptoneWidgetData只包含一个variable数据，则直接返回
    if (ptoneWidgetData == null || ptoneWidgetData.getData() == null) {
      // || ptoneWidgetData.getData().size() <= topN) {
      return ptoneWidgetData;
    }

    // 构建新的 PtoneWidgetData
    PtoneWidgetData fixPtoneWidgetData = ptoneWidgetData;

    List<Object> variableDataList = ptoneWidgetData.getData();
    List<Object> fixWidgetData = new ArrayList<Object>();

    // 排序variableData数据: 优先按照指标选择的顺序排序，相同指标按照维度值的字母序排列
    variableDataList = this.sortVariableDataList(variableDataList);

    // 返回数据量多的前十条记录
    for (int i = 0; i < topN && i < variableDataList.size(); i++) {
      PtoneVariableData varData = (PtoneVariableData) variableDataList.get(i);
      fixWidgetData.add(varData);
    }
    fixPtoneWidgetData.setData(fixWidgetData);// 重置widget数据

    return fixPtoneWidgetData;
  }
  
  /**
   * 限制ptoneWidgetData中variable数据行数 topN(按指标的topN)
   * 
   * @param ptoneGraphWidgetDataDesc
   * @param valueIndex 数据值在ptoneVariableData的rows数据row中的的索引（用于获取数据值）
   * @return
   */
  private PtoneWidgetData parseVariablesMetricsTopN(PtoneWidgetData ptoneWidgetData,
      final int valueIndex, int defaultTopN) {
    // 如果ptoneWidgetData只包含一个variable数据，则直接返回
    if (ptoneWidgetData == null || ptoneWidgetData.getData() == null) {
      return ptoneWidgetData;
    }

    Integer topN = ptoneWidgetData.getMax();
    if (topN == null) {
      // TODO: 此处需要统一处理
      return parseVariablesTopN(ptoneWidgetData, 1, defaultTopN); // 兼容历史数据（只返回top10）
//      topN = defaultTopN;
    }

    // 构建新的 PtoneWidgetData
    PtoneWidgetData fixPtoneWidgetData = ptoneWidgetData;

    List<Object> variableDataList = ptoneWidgetData.getData();
    List<Object> fixWidgetData = new ArrayList<Object>();


    // 排序variableData数据: 优先按照指标选择的顺序排序，相同指标按照维度值的字母序排列
    variableDataList = this.sortVariableDataList(variableDataList);
    
    // 返回每个指标 topN
    Map<String, Integer> countMap = new LinkedHashMap<String, Integer>();
    for (int i = 0; i < variableDataList.size(); i++) {
      PtoneVariableData varData = (PtoneVariableData) variableDataList.get(i);
      String key = varData.getMetricsKey();
      Integer value = countMap.get(key) == null ? 0 : countMap.get(key);
      if (value < topN && value < defaultTopN) {
        fixWidgetData.add(varData);
      } else {

      }
      countMap.put(key, value + 1);
    }
    fixPtoneWidgetData.setData(fixWidgetData);// 重置widget数据

    return fixPtoneWidgetData;
  }
  
  /**
   * 排序variableData数据: 优先按照指标选择的顺序排序，相同指标按照维度值的字母序排列
   * @param variableDataList
   * @return
   * @date: 2017年1月5日
   * @author peng.xu
   */
  private List<Object> sortVariableDataList(List<Object> variableDataList) {
    List<Object> fixVariableDataList = new ArrayList<Object>();
    // 按指标分组varData
    Map<String, List<Object>> groupVariableDataListMap = new LinkedHashMap<String, List<Object>>();
    for (Object obj : variableDataList) {
      PtoneVariableData varData = (PtoneVariableData) obj;
      String metricsName = varData.getMetricsName();
      if (groupVariableDataListMap.containsKey(metricsName)) {
        groupVariableDataListMap.get(metricsName).add(varData);
      } else {
        List<Object> tmpVariableDataList = new ArrayList<Object>();
        tmpVariableDataList.add(varData);
        groupVariableDataListMap.put(metricsName, tmpVariableDataList);
      }
    }

    // 从系统参数中获取legend的排序方式配置
    String sortType = sysConfigParamCache.getValue(SysConfigParam.WIDGET_LEGEND_SORT_TYPE);

    // 如果不设置，默认按照值倒序排序
    if (StringUtil.isBlank(sortType)) {
      sortType = LEGEND_SORT_TYPE_VALUE_DESC;
    }
    
    // 针对各个分组排序varData
    // 图例的排序顺序： 优先按照指标排序，然后按照下面的设置规则排序，如果不设置默认按照valueDesc排序，设置为其他值则不排序
    for (List<Object> tmpVariableDataList : groupVariableDataListMap.values()) {
      if (CollectionUtil.isNotEmpty(tmpVariableDataList)) {
        
        if(LEGEND_SORT_TYPE_STRING_ASC.equalsIgnoreCase(sortType)){
          // 排序variableData数据: 相同指标按照变量名排序（指标名—维度值）
          Collections.sort(tmpVariableDataList, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
              PtoneVariableData p1 = (PtoneVariableData) o1;
              PtoneVariableData p2 = (PtoneVariableData) o2;
              String variableName1 = p1.getVariableName();
              String variableName2 = p2.getVariableName();
              return variableName1.compareTo(variableName2);
            }
          });
        }else if(LEGEND_SORT_TYPE_VALUE_DESC.equalsIgnoreCase(sortType)){
          // 排序variableData数据
          Collections.sort(tmpVariableDataList, new Comparator<Object>() {
            // 按照数据总量排序
            public int compare(Object o1, Object o2) {
              PtoneVariableData p1 = (PtoneVariableData) o1;
              PtoneVariableData p2 = (PtoneVariableData) o2;
              Double v1 = 0d;
              for (Double v : p1.getTotals().values()) {
                v1 += v;
              }
              Double v2 = 0d;
              for (Double v : p2.getTotals().values()) {
                v2 += v;
              }
              return -1 * v1.compareTo(v2); // 倒序排列
            }
          });
        }else{
          // 默认不排序(设置为其他非LEGEND_SORT_TYPE_STRING_ASC、LEGEND_SORT_TYPE_VALUE_DESC)
        }
        fixVariableDataList.addAll(tmpVariableDataList);
      }
    }
    return fixVariableDataList;
  }

  /**
   * 限制ptoneVariableData中的数据rows行数 topN,按数据量排序数据
   * 
   * @param ptoneGraphWidgetDataDesc
   * @param valueIndex 数据值在ptoneVariableData的rows数据row中的的索引（用于获取数据值）
   * @return
   */
  private PtoneWidgetData parseVariableRowsTopN(PtoneWidgetData ptoneWidgetData,
      final int nameIndex, final int valueIndex, int defaultTopN, boolean showOthers, GraphType graphType) {

    // 如果ptoneWidgetData返回无数据，直接返回
    if (ptoneWidgetData == null || ptoneWidgetData.getData() == null
        || ptoneWidgetData.getData().isEmpty()) {
      return ptoneWidgetData;
    }

    // 设置用户设置的sort
    PtoneVariableData variableData = (PtoneVariableData) ptoneWidgetData.getData().get(0);
    String sortType = variableData.getSortType();
    String orderType = variableData.getOrderType();
    String orderRule = variableData.getOrderRule();
    String orderColumn = variableData.getOrderColumn();
    String orderColumnDataType = variableData.getOrderDimensionDataType();
    String orderColumnDataFormat = variableData.getOrderDimensionDataFormat();

    // 设置用户设置的topN
    Integer topN = variableData.getMax();
    if (topN == null) {
      topN = defaultTopN;
    }

    // 设置用户设置的showOthers
    if (variableData.getShowOthers() != null) {
      showOthers = Constants.validate.equals(variableData.getShowOthers());
    }

    ArrayList<String> dimensionsValueList = new ArrayList<String>();
    if (PtoneMetricsDimension.SORT_TYPE_DIMENSION_VALUE.equals(sortType)) {
      dimensionsValueList.addAll(ptoneWidgetData.getCategories());
      if (PtoneMetricsDimension.SORT_BY_NUMBER.equals(orderType)) {
        final int orderRuleValue =
            PtoneMetricsDimension.SORT_ORDER_DESC.equalsIgnoreCase(orderRule) ? -1 : 1; // 默认升序
        Collections.sort(dimensionsValueList, new Comparator<String>() {
          public int compare(String v1, String v2) {
            if (!StringUtil.isNumber(v1)) {
              return 1;
            } else if (!StringUtil.isNumber(v2)) {
              return -1;
            }
            return orderRuleValue * (Double.valueOf(v1)).compareTo(Double.valueOf(v2));
          }
        });
      } else if (PtoneMetricsDimension.SORT_BY_STRING.equals(orderType)) {
        final int orderRuleValue =
            PtoneMetricsDimension.SORT_ORDER_DESC.equalsIgnoreCase(orderRule) ? -1 : 1; // 默认升序
        Collections.sort(dimensionsValueList, new Comparator<String>() {
          public int compare(String v1, String v2) {
            v1 = v1 == null ? "" : v1;
            v2 = v2 == null ? "" : v2;
            return orderRuleValue * v1.compareTo(v2);
          }
        });
      } else if (PtoneMetricsDimension.SORT_BY_DATE.equals(orderType)) {
        final int orderRuleValue =
            PtoneMetricsDimension.SORT_ORDER_DESC.equalsIgnoreCase(orderRule) ? -1 : 1; // 默认升序
        if (false && StringUtil.isNotBlank(orderColumnDataFormat)) {
          final String dateFormat = orderColumnDataFormat;
          Collections.sort(dimensionsValueList, new Comparator<String>() {
            public int compare(String v1, String v2) {
              if (!StringUtil.isDateStr(v1, dateFormat)) {
                return 1;
              } else if (!StringUtil.isDateStr(v2, dateFormat)) {
                return -1;
              } else {
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                Date d1 = new Date();
                Date d2 = new Date();
                try {
                  // 设置lenient为false.
                  // 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
                  // format.setLenient(false);
                  d1 = format.parse(v1);
                  d2 = format.parse(v2);
                } catch (Exception e) {
                }
                return orderRuleValue * d1.compareTo(d2);
              }
            }
          });
        } else {
          Collections.sort(dimensionsValueList, new Comparator<String>() {
            public int compare(String v1, String v2) {
              v1 = v1 == null ? "" : v1;
              v2 = v2 == null ? "" : v2;
              return orderRuleValue * v1.compareTo(v2);
            }
          });
        }
      }
    } else if (PtoneMetricsDimension.SORT_TYPE_METRICS_VALUE.equals(sortType)
        && StringUtil.isNotBlank(orderColumn)) {
      Map<String, Double> countMap = new LinkedHashMap<String, Double>();
      ArrayList<Map.Entry<String, Double>> countMapList =
          new ArrayList<Map.Entry<String, Double>>();
      for (Object data : ptoneWidgetData.getData()) {
        PtoneVariableData varData = (PtoneVariableData) data;
        List<List<Object>> rows = varData.getRows();
        if (orderColumn.equals(varData.getMetricsKey())) {
          for (List<Object> row : rows) {
            String name = (String) row.get(nameIndex);
            Double value = ((Number) row.get(valueIndex)).doubleValue();
            value += countMap.get(name) == null ? 0 : countMap.get(name); // metrics的总数据量
            countMap.put(name, value);
          }
        }
      }

      for (Map.Entry<String, Double> entry : countMap.entrySet()) {
        countMapList.add(entry);
      }

      final int orderRuleValue = PtoneMetricsDimension.SORT_ORDER_ASC.equalsIgnoreCase(orderRule) ? 1 : -1; // 默认降序
      Collections.sort(countMapList, new Comparator<Map.Entry<String, Double>>() {
        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
          Double v1 = o1.getValue();
          Double v2 = o2.getValue();
          v1 = v1 == null ? 0 : v1;
          v2 = v2 == null ? 0 : v2;
          return orderRuleValue * v1.compareTo(v2); // 倒序排列
        }
      });

      for (int i = 0; i < countMapList.size(); i++) {
        dimensionsValueList.add(countMapList.get(i).getKey());
      }
    } else {
      dimensionsValueList.addAll(ptoneWidgetData.getCategories());
    }

    ArrayList<String> validateDimensionsValueList = new ArrayList<String>();
    for (int i = 0; i < dimensionsValueList.size() && i < topN && i < defaultTopN; i++) {
      validateDimensionsValueList.add(dimensionsValueList.get(i));
    }

    // 构建新的 PtoneWidgetData
    boolean containOthers = false;
    List<Object> widgetData = new ArrayList<Object>();
    for (Object data : ptoneWidgetData.getData()) {
      PtoneVariableData varData = (PtoneVariableData) data;
      List<List<Object>> rows = varData.getRows();

      List<List<Object>> fixRows = new ArrayList<List<Object>>();
      Double othersValue = 0d;
      Map<String, List<Object>> valueMap = new HashMap<String, List<Object>>();
      for (List<Object> row : rows) {
        String name = (String) row.get(nameIndex);
        if (validateDimensionsValueList.contains(name)) {
          valueMap.put(name, row);
        } else {
          Double value = ((Number) row.get(valueIndex)).doubleValue();
          othersValue += (value == null ? 0 : value);
          containOthers = true;
        }
      }
      for (String name : validateDimensionsValueList) {
        if (valueMap.get(name) != null) {
          fixRows.add(valueMap.get(name));
        }
      }

      if (showOthers && containOthers) {
        List<Object> otherRow = new ArrayList<Object>();
        otherRow.add("Others");
        otherRow.add(othersValue);
        fixRows.add(otherRow);
      }

      varData.setRows(fixRows); // 重置variable数据
      widgetData.add(varData);
    }

    if (showOthers && containOthers) {
      validateDimensionsValueList.add("Others");
    }

    PtoneWidgetData fixPtoneWidgetData = ptoneWidgetData;
    fixPtoneWidgetData.setData(widgetData);
    fixPtoneWidgetData.setCategories(validateDimensionsValueList);

    return fixPtoneWidgetData;
  }

}
