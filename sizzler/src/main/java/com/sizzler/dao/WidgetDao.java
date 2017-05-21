package com.sizzler.dao;

import java.util.List;

import com.sizzler.common.base.dao.DaoBaseInterface;
import com.sizzler.domain.widget.PtonePanelWidget;
import com.sizzler.domain.widget.PtoneWidgetInfo;

public interface WidgetDao extends DaoBaseInterface<PtoneWidgetInfo, String> {

  public abstract void savePanelWidgetRelation(PtonePanelWidget relation);

  public abstract void deleteWidget(String panelId);
  
  /**
   * 
   * @description 根据widgetId查询子widget（只返回widgetId）
   * @author shaoqiang.guo
   * @date 2016年11月18日 下午4:28:24
   * @param widgetId
   * @return
   */
  public abstract List<PtoneWidgetInfo> findChildWidgetById(String widgetId);
  /**
   * 
   * @description 根据panelId查询widget（只返回widgetId）
   * @author shaoqiang.guo
   * @date 2016年11月21日16:33:05
   * @param panelId
   * @return
   */
  public abstract List<PtoneWidgetInfo> findWidgetByPanelId(String panelId);
  
}
