package com.sizzler.proxy.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.ObjectUtil;
import com.sizzler.common.utils.UuidUtil;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.widget.GaWidgetInfo;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.domain.widget.dto.SegmentData;
import com.sizzler.proxy.dispatcher.PtoneDatasourceDesc;
import com.sizzler.proxy.dispatcher.PtoneVariableData;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;

public abstract class CommonHandler {

  /**
   * 授权验证，根据不同的数据源可以有不同的处理
   * @param userConnection
   * @param ptoneWidgetParam
   * @return
   */
  protected abstract PtoneWidgetParam checkAuth(UserConnection userConnection,
      PtoneWidgetParam ptoneWidgetParam);

  /**
   * 获取数据列表
   * @param ptoneWidgetInfo
   * @param gaWidgetInfo
   * @param ptoneVariableInfo
   * @param ptoneWidgetParam
   * @param webParamMap
   * @return
   */
  protected abstract List<PtoneVariableData> getTmpPtoneVariableDataList(
      PtoneWidgetInfo ptoneWidgetInfo, GaWidgetInfo gaWidgetInfo,
      PtoneVariableInfo ptoneVariableInfo, PtoneWidgetParam ptoneWidgetParam,
      Map<String, String> webParamMap);

  protected abstract String parseSegments(SegmentData segments,
      PtoneDatasourceDesc ptoneDatasourceDesc);

  protected abstract String parseFilters(SegmentData filters,
      PtoneDatasourceDesc ptoneDatasourceDesc);

  protected List<PtoneVariableData> commonHandle(PtoneDatasourceDesc ptoneDatasourceDesc, Logger log) {

    List<PtoneVariableData> ptoneVariableDataList = new ArrayList<PtoneVariableData>();
    PtoneWidgetInfo ptoneWidgetInfo = ptoneDatasourceDesc.getPtoneWidgetInfo();
    PtoneVariableInfo ptoneVariableInfo = ptoneDatasourceDesc.getPtoneVariableInfo();
    PtoneWidgetParam ptoneWidgetParam = ptoneDatasourceDesc.getPtoneWidgetParam();
    Map<String, String> webParamMap = ptoneDatasourceDesc.getWebParamMap();
    GaWidgetInfo gaWidgetInfo = ptoneDatasourceDesc.getGaWidgetInfo();
    UserConnection userConnection = ptoneDatasourceDesc.getUserConnection();
    try {
      ptoneWidgetParam = checkAuth(userConnection, ptoneWidgetParam);

      List<PtoneMetricsDimension> metricsDataList = ptoneWidgetParam.getMetrics();
      List<String> metricsKeyList = ptoneWidgetParam.getMetricsKeyList();
      List<PtoneMetricsDimension> dimensionsDataList = ptoneWidgetParam.getDimensions();

      // 处理metrics
      if (metricsDataList != null && !metricsDataList.isEmpty()) {
        // 判断是否存在指标级别的filter、segment
        boolean hasMetricsFilterOrSegment =
            hasMetricsFilterOrSegment(metricsDataList, ptoneWidgetParam.getFilters(),
                ptoneWidgetParam.getSegment());

        // 根据segment（从metrics中获取segment信息）将gaWidgetInfo按照查询次数拆分为多个gaWidgetInfo
        List<GaWidgetInfo> tmpGaWidgetInfoList = new ArrayList<GaWidgetInfo>();
        if (hasMetricsFilterOrSegment) {
          for (PtoneMetricsDimension md : metricsDataList) {
            String mKey = PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md);
            if (metricsKeyList.contains(mKey)) {
              GaWidgetInfo newGaWidgetInfo = (GaWidgetInfo) ObjectUtil.byteClone(gaWidgetInfo);
              newGaWidgetInfo.setMetricsId(md.getId());
              if(DsConstants.DS_CODE_GA.equals(ptoneWidgetParam.getDsCode()) || 
                  DsConstants.DS_CODE_PTENGINE.equals(ptoneWidgetParam.getDsCode())){
                newGaWidgetInfo.setSegment(parseSegments(md.getSegment(), ptoneDatasourceDesc));
              }else{
                newGaWidgetInfo.setFilters(parseFilters(md.getSegment(), ptoneDatasourceDesc));
              }
              
              // TODO: 如果同时支持全局filter、segment与指标级别的filter、segment，需要将两个and合并处理

              tmpGaWidgetInfoList.add(newGaWidgetInfo);
            }
          }
        } else {
          GaWidgetInfo newGaWidgetInfo = (GaWidgetInfo) ObjectUtil.byteClone(gaWidgetInfo);
          newGaWidgetInfo.setSegment(parseSegments(ptoneWidgetParam.getSegment(),
              ptoneDatasourceDesc));
          newGaWidgetInfo.setFilters(parseFilters(ptoneWidgetParam.getFilters(),
              ptoneDatasourceDesc));
          tmpGaWidgetInfoList.add(newGaWidgetInfo);
        }

        // 处理拆分查询存在相同指标情况,修正metricsName
        Map<String, Integer> metricsNameMap = new HashMap<String, Integer>();
        Map<String, String> metricsDataKeyToName = new HashMap<String, String>();

        for (int metricsIndex = 0; metricsIndex < tmpGaWidgetInfoList.size(); metricsIndex++) {
          GaWidgetInfo tmpGaWidgetInfo = tmpGaWidgetInfoList.get(metricsIndex);

          List<String> tmpMetricsKeyList = new ArrayList<String>();
          if (tmpGaWidgetInfoList.size() > 1) {
            PtoneMetricsDimension md = metricsDataList.get(metricsIndex);
            tmpMetricsKeyList.add(PtoneMetricsDimension.getSelectedMetricsOrDimensionKey(md));
          } else {
            tmpMetricsKeyList = metricsKeyList;
          }

          PtoneWidgetParam tmpPtoneWidgetParam =
              (PtoneWidgetParam) ObjectUtil.byteClone(ptoneWidgetParam);
          tmpPtoneWidgetParam.setMetricsKeyList(tmpMetricsKeyList);
          tmpPtoneWidgetParam.setDataKey(UuidUtil.generateUuid()); // 为每次查询生成一个uuid

          List<PtoneVariableData> tmpVariableDataList =
              getTmpPtoneVariableDataList(ptoneWidgetInfo, tmpGaWidgetInfo, ptoneVariableInfo,
                  tmpPtoneWidgetParam, webParamMap);

          // 处理拆分查询存在相同指标情况,修正metricsName
          for (int i = 0; i < tmpVariableDataList.size(); i++) {
            // 拆分查询回来后处理指标名重名问题
            if (tmpGaWidgetInfoList.size() > 1) {
              String variableName = tmpVariableDataList.get(i).getVariableName();
              String metricsName = tmpVariableDataList.get(i).getMetricsName();
              String metricsNameKey = metricsName + "-" + tmpVariableDataList.get(i).getDataKey();
              String fixMetricsName = metricsName;
              if (metricsNameMap.containsKey(fixMetricsName)) {
                if (metricsDataKeyToName.containsKey(metricsNameKey)) {
                  fixMetricsName = metricsDataKeyToName.get(metricsNameKey);
                } else {
                  int index = metricsNameMap.get(fixMetricsName) + 1;
                  metricsNameMap.put(fixMetricsName, index);
                  fixMetricsName = fixMetricsName + " (" + index + ")";
                  metricsDataKeyToName.put(metricsNameKey, fixMetricsName);
                }
              } else {
                metricsNameMap.put(fixMetricsName, 1);
                metricsDataKeyToName.put(metricsNameKey, fixMetricsName);
              }
              tmpVariableDataList.get(i).setMetricsName(fixMetricsName);
              if (tmpVariableDataList.size() > 1) {
                tmpVariableDataList.get(i).setVariableName(fixMetricsName + "-" + variableName);
              } else {
                if (metricsName != null && metricsName.equals(variableName)) {
                  tmpVariableDataList.get(i).setVariableName(fixMetricsName);
                } else {
                  tmpVariableDataList.get(i).setVariableName(fixMetricsName + "-" + variableName);
                }
              }
            }
          }

          // 查询数据源，获取数据
          ptoneVariableDataList.addAll(tmpVariableDataList);
        }
      } else if (dimensionsDataList != null
          && !dimensionsDataList.isEmpty()
          && (DsConstants.DS_TYPE_MODEL.equalsIgnoreCase(ptoneWidgetParam.getDsType())
              || DsConstants.DS_TYPE_STANDARDMODEL.equalsIgnoreCase(ptoneWidgetParam.getDsType()) || DsConstants.DS_CODE_BIGQUERY
                .equals(ptoneWidgetParam.getDsCode()))) {

        GaWidgetInfo newGaWidgetInfo = (GaWidgetInfo) ObjectUtil.byteClone(gaWidgetInfo);
        newGaWidgetInfo
            .setSegment(parseSegments(ptoneWidgetParam.getSegment(), ptoneDatasourceDesc));
        newGaWidgetInfo
            .setFilters(parseFilters(ptoneWidgetParam.getFilters(), ptoneDatasourceDesc));

        PtoneWidgetParam tmpPtoneWidgetParam =
            (PtoneWidgetParam) ObjectUtil.byteClone(ptoneWidgetParam);
        tmpPtoneWidgetParam.setMetricsKeyList(new ArrayList<String>());
        tmpPtoneWidgetParam.setDataKey(UuidUtil.generateUuid()); // 为每次查询生成一个uuid

        List<PtoneVariableData> tmpVariableDataList =
            getTmpPtoneVariableDataList(ptoneWidgetInfo, newGaWidgetInfo, ptoneVariableInfo,
                tmpPtoneWidgetParam, webParamMap);
        ptoneVariableDataList.addAll(tmpVariableDataList);
      }

    } catch (ServiceException se) {
      log.warn(se.getMessage(), se);
      throw se;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    }

    return ptoneVariableDataList;
  }

  /**
   * 判断是否存在指标级别的filter、segment
   * @param metricsDataList
   * @return
   * @date: 2016年6月30日
   * @author peng.xu
   */
  public boolean hasMetricsFilterOrSegment(List<PtoneMetricsDimension> metricsDataList, SegmentData filters,
      SegmentData segment) {
    boolean useMetricsFilterOrSegment = (segment == null && filters == null);
    if (useMetricsFilterOrSegment && metricsDataList != null && metricsDataList.size() > 0) {
      for (PtoneMetricsDimension md : metricsDataList) {
        if (md != null && md.getSegment() != null) {
          return true;
        }
      }
    }
    return false;
  }

}
