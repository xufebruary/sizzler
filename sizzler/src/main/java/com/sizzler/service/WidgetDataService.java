/**
 * Project Name:ptone-ui-backgroud File Name:WidgetDataService.java Package
 * Name:com.ptmind.ptone.rest.service Date:2015年4月20日下午12:38:33 Copyright (c) 2015,
 * peng.xu@ptthink.com All Rights Reserved.
 * 
 */

package com.sizzler.service;

import java.util.Map;

import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.widget.dto.AcceptWidget;
import com.sizzler.proxy.dispatcher.PtoneWidgetChartData;
import com.sizzler.proxy.dispatcher.PtoneWidgetData;

/**
 * 获取widget数据 
 */
public interface WidgetDataService {

  /**
   * 获取Widget的数据：根据图表控件类型转换好的json串
   */
  public PtoneWidgetChartData getPtoneWidgetChartData(String widgetId,
      Map<String, String> webParamMap);

  /**
   * 获取Widget的数据：直接返回原始数据结构 <br>
   * 1、 从数据库中查询widget配置信息<br>
   * 2、从前台widget编辑器传递的参数获取widget配置信息
   */
  public PtoneWidgetData getPtoneWidgetData(String widgetId, Map<String, String> webParamMap,
      AcceptWidget widget);

  /**
   * 根据AcceptWidget获取对应的spaceInfo
   */
  public PtoneSpaceInfo getWidgetSpaceInfo(AcceptWidget widget);

}
