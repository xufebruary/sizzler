package com.sizzler.service;


import java.util.List;
import java.util.Map;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.common.exception.BusinessException;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.widget.GaWidgetInfo;
import com.sizzler.domain.widget.PtonePanelWidget;
import com.sizzler.domain.widget.PtoneWidgetChartSetting;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.domain.widget.PtoneWidgetInfoExtend;
import com.sizzler.domain.widget.PtoneWidgetVariable;
import com.sizzler.domain.widget.dto.AcceptWidget;
import com.sizzler.domain.widget.vo.MetricsDimensionsAliasVo;

public interface WidgetService extends ServiceBaseInterface<PtoneWidgetInfo, String> {

  public abstract AcceptWidget getWidgetById(String widgetId);

  public abstract List<AcceptWidget> findWidget(String pid);

  public abstract AcceptWidget save(AcceptWidget widget);

  public abstract AcceptWidget updateWidgetWithVariables(AcceptWidget widget);

  public abstract void updateBaseWidget(PtoneWidgetInfo ptoneWidgetInfo);

  /**
   * 
   * @description 根据widgetId删除或恢复widget 即只改变状态，软删除（Soft Deleting）
   * @author shaoqiang.guo
   * @date 2016年11月17日 下午5:24:10
   * @param widgetId
   * @param isDelete 值是true时，标识删除widget，否则恢复widget
   */
  public abstract void softDeletingWidget(String widgetId, boolean isDelete);

  /**
   * 
   * 级联删除widget相关信息
   * @author shaoqiang.guo
   * @date 2016年11月24日 上午11:08:06
   * @param paramMap
   * @param updateMap
   */
  public abstract void deleteWidgetCorrelation(Map<String, Object[]> paramMap,
      Map<String, Map<String, String>> updateMap);

  /**
   * 根据widgetId获取widget所选指标列表
   * @param widgetId
   * @return
   * @date: 2016年7月27日
   * @author peng.xu
   */
  public abstract List<PtoneMetricsDimension> getWidgetSelectedMetrics(String widgetId);
  
  /**
   * 根据widgetId获取widget所选维度列表
   * @param widgetId
   * @return
   * @date: 2016年12月06
   * @author peng.xu
   */
  public abstract List<PtoneMetricsDimension> getWidgetSelectedDimensions(String widgetId);


  /**
   * 根据widget模板id列表，批量创建widget
   * @param templetIdList
   * @return
   * @date: 2016年8月15日
   * @author peng.xu
   */
  public abstract List<AcceptWidget> addWidgetByTemplet(List<String> templetIdList, String spaceId,
      String panelId, String uid, String isPreview);

  /**
   * 根据模板构建新的gaWidgetInfo信息
   * 
   * @param templetWidgetId
   * @param newWidgetId
   * @param newVariableId
   * @return
   * @date: 2016年7月23日
   * @author peng.xu
   */
  public abstract GaWidgetInfo buildGaWidgetInfo(String templetWidgetId,
      PtoneWidgetInfo baseWidget, List<GaWidgetInfo> gaWidgetTempletList, String newWidgetId,
      String newVariableId, String sourceType, boolean isTemplet);
  

  /**
   * 根据模板保存widgetExtend信息
   * 
   * @param templetWidgetId
   * @param newWidgetId
   * @return
   * @date: 2016年8月16日
   * @author peng.xu
   */
  public abstract PtoneWidgetInfoExtend saveWidgetExtendByTemplet(String templetWidgetId,
      String newWidgetId, List<PtoneWidgetInfoExtend> widgetExtendTempletList);

  /**
   * 根据模板保存widgetChartSetting信息
   * 
   * @param templetWidgetId
   * @param newWidgetId
   * @return
   * @date: 2016年8月16日
   * @author peng.xu
   */
  public abstract PtoneWidgetChartSetting saveWidgetChartSettingByTemplet(String templetWidgetId,
      String newWidgetId, List<PtoneWidgetChartSetting> chartSettingTempletList);

  /**
   * 根据模板保存variableInfo信息
   * @param templetWidgetId
   * @param newWidgetId
   * @return
   * @date: 2016年8月16日
   * @author peng.xu
   */
  public abstract PtoneVariableInfo saveVariableInfoByTemplet(String templetWidgetId, PtoneWidgetInfo baseWidget);

  /**
   * 修正widgetInfo信息
   * @return
   * @date: 2016年8月16日
   * @author peng.xu
   */
  public abstract PtoneWidgetInfo fixWidgetInfoByTemplet(PtoneWidgetInfo baseWidget,
      String spaceId, String panelId, String widgetId, String uid, String localLang,
      long createTime, boolean isTemplet);

  /**
   * 保存panel和widget关联关系
   * @param panelId
   * @param widgeId
   * @return
   * @date: 2016年8月16日
   * @author peng.xu
   */
  public abstract PtonePanelWidget savePanelWidgetRelation(String panelId, String widgeId);

  /**
   * 保存widget和variable的关联关系
   * @param widgetId
   * @param variableId
   * @return
   * @date: 2016年8月16日
   * @author peng.xu
   */
  public abstract PtoneWidgetVariable saveWidgetVariableRelation(String widgetId, String variableId);

  /**
   * 更改指标或者维度别名
   * @param aliasVo
   * @return
   * @date: 2017年1月5日
   * @author li.zhang
   */
  public abstract boolean updateMetricsAlias(MetricsDimensionsAliasVo aliasVo);

  /**
   * 更改指标或者维度别名
   * @param aliasVo
   * @return
   * @date: 2017年1月5日
   * @author li.zhang
   */
  public abstract void updateMetricsAndAlias(MetricsDimensionsAliasVo aliasVo)  throws BusinessException;
  
  /**
   * 保存基本widget信息
   * @param widget
   * @return
   * @date: 2017年5月6日
   * @author li.zhang
   */
  public abstract void saveBaseWidget(PtoneWidgetInfo widget);

}
