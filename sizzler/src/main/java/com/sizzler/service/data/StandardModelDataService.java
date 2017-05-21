/**
 * Project Name:ptone-ui-backgroud File Name:DataService.java Package
 * Name:com.ptmind.ptone.rest.service Date:2015年4月20日下午12:38:33 Copyright (c) 2015,
 * peng.xu@ptthink.com All Rights Reserved.
 *
 */

package com.sizzler.service.data;

import java.util.List;
import java.util.Map;

import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.widget.GaWidgetInfo;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.proxy.dispatcher.PtoneVariableData;
import com.sizzler.proxy.dispatcher.PtoneWidgetParam;

/**
 * 从数据源取数service类 <br/>
 * 
 * @author peng.xu
 * @version
 * @since JDK 1.6
 * @see
 */
public interface StandardModelDataService {

  public List<PtoneVariableData> getData(PtoneWidgetInfo ptoneWidgetInfo,
      GaWidgetInfo gaWidgetInfo, PtoneVariableInfo ptoneVariableInfo,
      PtoneWidgetParam ptoneWidgetParam, Map<String, String> webParamMap);
}
