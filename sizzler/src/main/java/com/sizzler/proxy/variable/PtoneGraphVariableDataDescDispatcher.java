package com.sizzler.proxy.variable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.proxy.common.CommonDataUtil;
import com.sizzler.proxy.common.model.CommonQueryParam;
import com.sizzler.proxy.common.model.ModelData;
import com.sizzler.proxy.dispatcher.PtoneVariableData;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;
import com.sizzler.proxy.variable.model.GraphVariableDataDesc;

@Component
public class PtoneGraphVariableDataDescDispatcher {

  /**
   * Description: 转化为线型数据结构的PtoneVariableData数据<br>
   * 适用图形类型包括：line、areaspline、area、column、bar : [[name,value]] 根据图表类型组织返回数据
   * 对于line、bar、coumn、area类型图表的数据的处理：包括x轴和data, x轴：[name1、name2,..] , data:[value1,value2,...]
   */
  public List<PtoneVariableData> parseLineDataTable(GraphVariableDataDesc graphDesc,
      CommonQueryParam queryParam) {
    ModelData modelData = graphDesc.getModelData();
    PtoneWidgetParam ptoneWidgetParam = graphDesc.getPtoneWidgetParam();
    List<PtoneMetricsDimension> metricsData = ptoneWidgetParam.getMetrics();
    List<String> metricsKeyList = ptoneWidgetParam.getMetricsKeyList();
    List<PtoneVariableData> ptoneVariableDataList = new ArrayList<PtoneVariableData>();

    List<String> metricsNameList = new ArrayList<String>();
    List<String> metricsCodeList = new ArrayList<String>();
    List<String> metricsIdList = new ArrayList<String>();
    for (PtoneMetricsDimension md : metricsData) {
      String mKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md);
      if (metricsKeyList.contains(mKey)) {
        metricsIdList.add(md.getId());
        metricsNameList.add(md.getName());
        metricsCodeList.add(md.getCode());
      }
    }

    String userDimensions = queryParam.getDimensions();
    List<String> xAxisDimensionCodeList = queryParam.getXAxisDateDimensionCodeList();
    boolean useDatetimeAxis = graphDesc.getUseDatetimeAxis();

    // userDimension剔除坐标轴时间维度
    if (useDatetimeAxis && xAxisDimensionCodeList != null && !xAxisDimensionCodeList.isEmpty()
        && StringUtil.isNotBlank(userDimensions)) {
      for (String xAxisDimensionCode : xAxisDimensionCodeList) {
        List<String> userDimensionsList = StringUtil.splitToList(userDimensions, ",");
        userDimensionsList.remove(xAxisDimensionCode);
        userDimensions = StringUtil.join(userDimensionsList, ",");
      }
    }

    // column两个维度时，第一个维度为x轴，第二个为color分类
    if (userDimensions.indexOf(",") >= 0) {
      userDimensions = userDimensions.split(",")[1];
    }

    String dimensions = queryParam.getQueryDimensions();
    int minMetricsIndex = StringUtil.isNotBlank(dimensions) ? dimensions.split(",").length : 0;
    int userDimensionsIndex = this.getDimensionsIndex(dimensions, userDimensions);

    // 查询的ModelData结果数据
    List<List> dataRows =
        (modelData.getObjetRowList() == null ? new ArrayList<List>() : modelData.getObjetRowList());

    if (ptoneWidgetParam.isReturnTableDataForCompoundMetrics()) {
      // 如果是复合指标的table数据查询，则构造为table数据返回
      return this.parseCompoundMetricsTableData(dataRows, modelData.isDetail(), ptoneWidgetParam,
          queryParam);
    }

    // 线的分类维度值
    List<String> dimensionsValueList =
        this.getDimensionsValueList(modelData, dimensions, userDimensions);

    // 处理单次查询存在相同指标情况,修正variableName
    Map<String, Integer> metricsNameMap = new HashMap<String, Integer>();
    for (int mIndex = 0; mIndex < metricsCodeList.size(); mIndex++) {
      String metricsKey = metricsKeyList.get(mIndex);
      String metricsId = metricsIdList.get(mIndex);
      String metricsName = metricsNameList.get(mIndex);
      String metricsCode = metricsCodeList.get(mIndex);
      int metricsIndex = minMetricsIndex + mIndex;

      // 处理单次查询存在相同指标情况,修正variableName
      if (metricsNameMap.containsKey(metricsName)) {
        int index = metricsNameMap.get(metricsName) + 1;
        metricsNameMap.put(metricsName, index);
        metricsName = metricsName + " (" + index + ")";
      } else {
        metricsNameMap.put(metricsName, 1);
      }

      for (String dimensionsValue : dimensionsValueList) {
        PtoneVariableData ptoneVariableData = new PtoneVariableData();
        ptoneVariableData.setGraphType(graphDesc.getGraphType());
        List<List<Object>> dataTableRowList = new ArrayList<List<Object>>();
        Double totals = 0d;
        for (List<Object> row : dataRows) {
          // 如果是多维度情况，判断该行数据是否是当前维度变量的数据
          // 如果当前row中
          // 不包含当前循环的维度值，直接跳过。比如来源维度中dimensionsValue=google，则跳过来源为baidu等其他维度的行
          if (userDimensionsIndex > 0
              && !"".equals(dimensionsValue)
              && (!dimensionsValue.equals(String.valueOf(row.get(userDimensionsIndex))) && !(StringUtil
                  .isBlank(String.valueOf(row.get(userDimensionsIndex))) && CommonDataUtil.BLANK_DIMENSION_VALUE
                  .equals(dimensionsValue)))) {
            continue;
          }
          int i = 0;
          String category = "";
          if (metricsIndex > 0 && StringUtil.isNotBlank(dimensions)) {
            category = String.valueOf(row.get(0));
          } else {
            category = metricsName;// 如果没有维度，名称设为指标名
          }
          for (i = 1; i < minMetricsIndex; i++) {
            if (i == userDimensionsIndex) {
              continue;
            }
            category += "-" + row.get(i);
          }

          Object valueObj = row.get(metricsIndex);
          String valueStr = valueObj != null ? String.valueOf(valueObj) : "0";
          Number value = (StringUtil.isBigDecimal(valueStr) ? new BigDecimal(valueStr) : 0);
          List<Object> dataTableRow = new ArrayList<Object>();
          dataTableRow.add(category);
          dataTableRow.add(value);
          dataTableRowList.add(dataTableRow);
          totals += (value == null ? 0 : value).doubleValue();
        }

        // 修正数据时间排序
        if (graphDesc.getUseDatetimeAxis()) {
          Collections.sort(dataTableRowList, new Comparator<List<Object>>() {
            @Override
            public int compare(List<Object> o1, List<Object> o2) {
              String category1 = (String) o1.get(0);
              String category2 = (String) o2.get(0);
              return category1.compareTo(category2);
            }
          });
        }

        // 处理堆图stack
        String stack = metricsName;
        ptoneVariableData.setMetricsId(metricsId);
        ptoneVariableData.setMetricsCode(metricsCode);
        ptoneVariableData.setMetricsName(metricsName);
        ptoneVariableData.setMetricsKey(metricsKey);
        ptoneVariableData.setStack(stack);
        ptoneVariableData.setDimensionsId("");
        ptoneVariableData.setDimensions(userDimensions);
        ptoneVariableData.setRows(dataTableRowList);
        ptoneVariableData.setDetail(modelData.isDetail());
        ptoneVariableData.getTotals().put(metricsKey, totals);
        ptoneVariableData.setVariableName(dimensionsValue);
        ptoneVariableData.setxAxisDateDimensionList(queryParam.getQueryXAxisDateDimension());
        ptoneVariableDataList.add(ptoneVariableData);
      }
    }

    return ptoneVariableDataList;
  }

  /**
   * 返回复合指标拆分查询的tableData数据
   * @date: 2016年7月25日
   * @author peng.xu
   */
  private List<PtoneVariableData> parseCompoundMetricsTableData(List<List> dataRows,
      boolean isDetail, PtoneWidgetParam ptoneWidgetParam, CommonQueryParam queryParam) {

    List<PtoneVariableData> ptoneVariableDataList = new ArrayList<PtoneVariableData>();
    List<List<Object>> dataTableRowList = new ArrayList<List<Object>>();
    List<Object> thList = new ArrayList<Object>();

    int xAxisDimensionCount = 0; // 时间维度数量
    List<String> xAxisDimensionCodeList = queryParam.getXAxisDateDimensionCodeList();
    List<String> queryDimensions = queryParam.getQueryDimensionsList();
    List<String> queryMetrics = queryParam.getMetricsCodeList();

    // 组装表头
    if (xAxisDimensionCodeList != null && !xAxisDimensionCodeList.isEmpty()) {
      xAxisDimensionCount = xAxisDimensionCodeList.size();
      thList.add("AutoBuildDate");// 如果有时间维度，则增加一时间列占位
      if (queryDimensions != null && !queryDimensions.isEmpty()) {
        for (String dCode : queryDimensions) {
          if (!xAxisDimensionCodeList.contains(dCode)) {
            thList.add(dCode);
          }
        }
      }
    } else {
      if (queryDimensions != null && !queryDimensions.isEmpty()) {
        thList.addAll(queryDimensions);
      }
    }
    if (queryMetrics != null && !queryMetrics.isEmpty()) {
      thList.addAll(queryMetrics);
    }
    dataTableRowList.add(thList); // 首先添加表头数据

    // 组装数据
    for (List<List> row : dataRows) {
      List<Object> newRow = new ArrayList<Object>();
      StringBuilder dateStr = null;
      for (int i = 0; i < row.size(); i++) {
        if (i < xAxisDimensionCount) {
          if (dateStr == null) {
            dateStr = new StringBuilder(String.valueOf(row.get(i)));
          } else {
            dateStr.append("-").append(String.valueOf(row.get(i)));
          }
          if (i == xAxisDimensionCount - 1) {
            newRow.add(dateStr.toString());
          }
        } else {
          newRow.add(row.get(i));
        }
      }
      dataTableRowList.add(newRow);
    }

    // 构建VariableData
    PtoneVariableData ptoneVariableData = new PtoneVariableData();
    ptoneVariableData.setRows(dataTableRowList);
    ptoneVariableData.setDetail(isDetail);
    ptoneVariableDataList.add(ptoneVariableData);
    ptoneVariableData.setxAxisDateDimensionList(queryParam.getQueryXAxisDateDimension());

    return ptoneVariableDataList;
  }

  /**
   * Description: 转化为饼图数据结构的PtoneVariableData数据<br>
   * 适用图形类型包括：pie、hollowpie : [[name1,value1]、[name2,value2],...]
   */
  public List<PtoneVariableData> parsePieDataTable(GraphVariableDataDesc graphDesc,
      CommonQueryParam queryParam) {

    List<PtoneVariableData> ptoneVariableDataList = new ArrayList<PtoneVariableData>();
    PtoneVariableData ptoneVariableData = new PtoneVariableData();
    ptoneVariableData.setGraphType(graphDesc.getGraphType());
    List<List<Object>> dataTableRowList = new ArrayList<List<Object>>();
    Double totals = 0d;

    ModelData modelData = graphDesc.getModelData();
    PtoneWidgetParam ptoneWidgetParam = graphDesc.getPtoneWidgetParam();
    List<List> dataRows =
        (modelData.getObjetRowList() == null ? new ArrayList<List>() : modelData.getObjetRowList());

    if (ptoneWidgetParam.isReturnTableDataForCompoundMetrics()) {
      // 如果是复合指标的table数据查询，则构造为table数据返回
      return this.parseCompoundMetricsTableData(dataRows, modelData.isDetail(), ptoneWidgetParam,
          queryParam);
    }

    // Pie只支持一个指标、最多一个维度
    String metricsKey = ptoneWidgetParam.getMetricsKeyList().get(0);
    PtoneMetricsDimension metrics = ptoneWidgetParam.getMetrics().get(0);
    String metricsId = metrics.getId();
    String metricsName = metrics.getName();
    String metricsCode = metrics.getCode();
    List<String> dimensionsKeyList = ptoneWidgetParam.getDimensionsKeyList();
    String dimensionsId = "";
    String dimensions = "";
    if (dimensionsKeyList.size() > 0) {
      PtoneMetricsDimension d = ptoneWidgetParam.getDimensions().get(0);
      dimensionsId = d.getId();
      dimensions = d.getCode();
    }

    // 对于pie类型图表的数据处理：[[name1,value1]、[name2,value2],...]
    int mIndex = dimensionsKeyList.size(); // 指标的起始列索引
    for (List<Object> row : dataRows) {
      int i = 0;
      String category = "";
      if (mIndex > 0) {
        category = String.valueOf(row.get(0));
        if (StringUtil.isBlank(category)) {
          category = CommonDataUtil.BLANK_DIMENSION_VALUE;
        }
      } else {
        category = metricsName;// 如果没有维度，名称设为指标名
      }
      for (i = 1; i < mIndex; i++) {
        category += "-" + row.get(i);
      }

      Object valueObj = row.get(mIndex);
      String valueStr = valueObj != null ? String.valueOf(valueObj) : "0";
      Number value = (StringUtil.isBigDecimal(valueStr) ? new BigDecimal(valueStr) : 0);
      List<Object> dataTableRow = new ArrayList<Object>();
      dataTableRow.add(category);
      dataTableRow.add(value);
      dataTableRowList.add(dataTableRow);
      totals += (value == null ? 0 : value).doubleValue();
    }

    ptoneVariableData.setRows(dataTableRowList);
    ptoneVariableData.setDetail(modelData.isDetail());
    ptoneVariableData.setMetricsId(metricsId);
    ptoneVariableData.setMetricsCode(metricsCode);
    ptoneVariableData.setMetricsName(metricsName);
    ptoneVariableData.setMetricsKey(metricsKey);
    ptoneVariableData.getTotals().put(metricsKey, totals);
    ptoneVariableData.setDimensionsId(dimensionsId);
    ptoneVariableData.setDimensions(dimensions);
    ptoneVariableData.setVariableName("");

    ptoneVariableDataList.add(ptoneVariableData);

    return ptoneVariableDataList;
  }

  /**
   * Description: 转化为simplenumber、百分比图形数据结构的PtoneVariableData数据<br>
   * 适用图形类型包括：simplenumber、circlepercent、progressbar 注意：simplenumber没有目标值
   */
  public List<PtoneVariableData> parseSimpleNumberDataTable(GraphVariableDataDesc graphDesc,
      CommonQueryParam queryParam) {

    List<PtoneVariableData> ptoneVariableDataList = new ArrayList<PtoneVariableData>();
    PtoneVariableData ptoneVariableData = new PtoneVariableData();
    ptoneVariableData.setGraphType(graphDesc.getGraphType());
    List<List<Object>> dataTableRowList = new ArrayList<List<Object>>();

    ModelData modelData = graphDesc.getModelData();
    PtoneWidgetParam ptoneWidgetParam = graphDesc.getPtoneWidgetParam();
    Number goals = graphDesc.getGoals();

    String metricsKey = ptoneWidgetParam.getMetricsKeyList().get(0);
    PtoneMetricsDimension metrics = ptoneWidgetParam.getMetrics().get(0);
    String metricsId = metrics.getId();
    String metricsName = metrics.getName();
    String metricsCode = metrics.getCode();

    // 对于number类型图表的数据处理：[[name1,value1]、[name2,value2],...],目标值统一设置在widget上
    Object valueObj = modelData.getTotalRowList().get(0);
    String valueStr = valueObj != null ? String.valueOf(valueObj) : "0";
    valueStr = StringUtil.scientificNotationToString(valueStr);
    Number totals = (StringUtil.isBigDecimal(valueStr) ? new BigDecimal(valueStr) : 0); // 本期的值
    List<Object> dataTableRow = new ArrayList<Object>();
    dataTableRow.add(graphDesc.getVariableName());
    dataTableRow.add(StringUtil.scientificNotationToString(totals + ""));
    dataTableRow.add(goals); // 目标值
    dataTableRowList.add(dataTableRow);
    ptoneVariableData.setRows(dataTableRowList);
    ptoneVariableData.getTotals().put(metricsKey, totals == null ? 0 : totals.doubleValue());
    ptoneVariableData.setDetail(modelData.isDetail());

    ptoneVariableData.setMetricsId(metricsId);
    ptoneVariableData.setMetricsCode(metricsCode);
    ptoneVariableData.setMetricsName(metricsName);
    ptoneVariableData.setMetricsKey(metricsKey);
    ptoneVariableData.setDimensionsId("");
    ptoneVariableData.setDimensions("");
    ptoneVariableData.setVariableName("");
    ptoneVariableDataList.add(ptoneVariableData);

    return ptoneVariableDataList;
  }

  /**
   * Description: 转化为number图形数据结构的PtoneVariableData数据<br>
   * 适用图形类型包括：number <br>
   * 注意：number的目标值为环比上一期的值
   */
  public List<PtoneVariableData> parseQoqNumberDataTable(GraphVariableDataDesc graphDesc,
      CommonQueryParam queryParam) {

    List<PtoneVariableData> ptoneVariableDataList = new ArrayList<PtoneVariableData>();
    PtoneVariableData ptoneVariableData = new PtoneVariableData();
    ptoneVariableData.setGraphType(graphDesc.getGraphType());

    ModelData modelData = graphDesc.getModelData();
    PtoneWidgetParam ptoneWidgetParam = graphDesc.getPtoneWidgetParam();

    String metricsKey = ptoneWidgetParam.getMetricsKeyList().get(0);
    PtoneMetricsDimension metrics = ptoneWidgetParam.getMetrics().get(0);
    String metricsId = metrics.getId();
    String metricsName = metrics.getName();
    String metricsCode = metrics.getCode();

    // 对于number类型图表的数据处理: number类型的取数只取总数,和上一期数比较
    // 根据dateKey计算上一期的开始日期和结束日期： 请求的时间格式为 yyyy-MM-dd
    ModelData lastModelData = graphDesc.getLastModelData();

    // 对于number类型图表的数据处理：[[name1,value1]、[name2,value2],...],目标值统一设置在widget上
    Object valueObj = modelData.getTotalRowList().get(0);
    String valueStr = valueObj != null ? String.valueOf(valueObj) : "0";
    valueStr = StringUtil.scientificNotationToString(valueStr);
    Number totals = (StringUtil.isBigDecimal(valueStr) ? new BigDecimal(valueStr) : 0); // 本期的值

    Object lastValueObj = lastModelData != null ? lastModelData.getTotalRowList().get(0) : null;
    String lastValueStr = lastValueObj != null ? String.valueOf(lastValueObj) : "0";
    lastValueStr = StringUtil.scientificNotationToString(lastValueStr);
    Number lastTotals = (StringUtil.isBigDecimal(lastValueStr) ? new BigDecimal(lastValueStr) : 0); // 上一期的值

    List<List<Object>> dataTableRowList = new ArrayList<List<Object>>();
    List<Object> dataTableRow = new ArrayList<Object>();
    dataTableRow.add(metricsName);
    dataTableRow.add(StringUtil.scientificNotationToString(totals + ""));
    dataTableRow.add(StringUtil.scientificNotationToString(lastTotals + "")); // 目标值：上期值
    dataTableRowList.add(dataTableRow);
    ptoneVariableData.setRows(dataTableRowList);
    ptoneVariableData.getTotals().put(metricsKey, totals == null ? 0 : totals.doubleValue());
    ptoneVariableData.setDetail(modelData.isDetail());

    ptoneVariableData.setMetricsId(metricsId);
    ptoneVariableData.setMetricsCode(metricsCode);
    ptoneVariableData.setMetricsName(metricsName);
    ptoneVariableData.setMetricsKey(metricsKey);
    ptoneVariableData.setDimensionsId("");
    ptoneVariableData.setDimensions("");
    ptoneVariableData.setVariableName("");
    ptoneVariableDataList.add(ptoneVariableData);

    return ptoneVariableDataList;
  }

  /**
   * Description: 转化为table型数据结构的PtoneVariableData数据<br>
   * 适用图形类型包括：table、map
   */
  public List<PtoneVariableData> parseTableDataTable(GraphVariableDataDesc graphDesc,
      CommonQueryParam queryParam) {
    List<PtoneVariableData> ptoneVariableDataList = new ArrayList<PtoneVariableData>();

    PtoneVariableData ptoneVariableData = new PtoneVariableData();
    ptoneVariableData.setGraphType(graphDesc.getGraphType());
    List<List<Object>> dataTableRowList = new ArrayList<List<Object>>();

    ModelData modelData = graphDesc.getModelData();
    PtoneWidgetParam ptoneWidgetParam = graphDesc.getPtoneWidgetParam();
    List<PtoneMetricsDimension> metricsData = ptoneWidgetParam.getMetrics();
    List<String> metricsKeyList = ptoneWidgetParam.getMetricsKeyList();
    List<PtoneMetricsDimension> dimensionData = ptoneWidgetParam.getDimensions();
    List<String> dimensionsKeyList = ptoneWidgetParam.getDimensionsKeyList();

    // table表头构建
    List<Object> thList = new ArrayList<Object>();
    List<String> dimensionsIdList = new ArrayList<String>();
    List<String> dimensionsCodeList = new ArrayList<String>();
    List<String> dimensionsNameList = new ArrayList<String>();
    List<String> metricsIdList = new ArrayList<String>();
    List<String> metricsNameList = new ArrayList<String>();
    List<String> metricsCodeList = new ArrayList<String>();

    // Map类型图表修正dimension
    List<PtoneMetricsDimension> fixedDimensionsList = queryParam.getFixedDimensionsList();
    if (fixedDimensionsList != null && fixedDimensionsList.size() > 0) {
      for (PtoneMetricsDimension d : fixedDimensionsList) {
        dimensionsIdList.add(d.getId());
        dimensionsNameList.add(d.getName());
        dimensionsCodeList.add(d.getCode());
        dimensionsKeyList.add(d.getCode() + "-" + d.getId());
      }
    }

    for (PtoneMetricsDimension dd : dimensionData) {
      String dKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(dd);
      if (dimensionsKeyList.contains(dKey)) {
        dimensionsIdList.add(dd.getId());
        dimensionsNameList.add(dd.getName());
        dimensionsCodeList.add(dd.getCode());
      }
    }

    for (PtoneMetricsDimension md : metricsData) {
      String mKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md);
      if (metricsKeyList.contains(mKey)) {
        metricsIdList.add(md.getId());
        metricsNameList.add(md.getName());
        metricsCodeList.add(md.getCode());
      }
    }
    thList.addAll(dimensionsNameList);
    thList.addAll(metricsNameList);
    dataTableRowList.add(thList); // 首先添加表头数据

    // 设置数据行
    List<List> dataRows =
        (modelData.getObjetRowList() == null ? new ArrayList<List>() : modelData.getObjetRowList());
    for (List row : dataRows) {
      dataTableRowList.add(row);
    }

    ptoneVariableData.setRows(dataTableRowList);
    ptoneVariableData.setDetail(modelData.isDetail());

    ptoneVariableData.setMetricsId(StringUtil.join(metricsIdList, ","));
    ptoneVariableData.setMetricsCode(StringUtil.join(metricsCodeList, ","));
    ptoneVariableData.setMetricsKey(StringUtil.join(metricsKeyList, ","));
    ptoneVariableData.setDimensionsKey(StringUtil.join(dimensionsKeyList, ","));
    ptoneVariableData.setMetricsName(StringUtil.join(metricsNameList, ","));
    ptoneVariableData.setDimensionsId(StringUtil.join(dimensionsIdList, ","));
    ptoneVariableData.setDimensions(StringUtil.join(dimensionsCodeList, ","));
    ptoneVariableDataList.add(ptoneVariableData);

    return ptoneVariableDataList;
  }

  /**
   * 获取唯一维度的索引<br>
   * 只支持一个维度<br>
   * 
   * @see 子类可以根据自己的规则重写该方法
   * @param dimensions
   * @param userDimensions
   * @return
   */
  private int getDimensionsIndex(String dimensions, String userDimensions) {
    return CommonDataUtil.getDimensionsIndex(dimensions, userDimensions);
  }

  /**
   * 获取唯一维度的索引<br>
   * 只支持一个维度<br>
   * 
   * @see 子类可以根据自己的规则重写该方法
   * @param dimensions
   * @param userDimensions
   * @return
   */
  private List<String> getDimensionsValueList(ModelData modelData, String dimensions,
      String userDimensions) {
    return CommonDataUtil.getDimensionsValueList(modelData, dimensions, userDimensions);
  }

}
