package com.sizzler.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.dao.WidgetDao;
import com.sizzler.domain.widget.PtonePanelWidget;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.system.Constants;

@Repository("widgetDao")
public class WidgetDaoImpl extends DaoBaseInterfaceImpl<PtoneWidgetInfo, String> implements
    WidgetDao {

  @Override
  public PtoneWidgetInfo get(String widgetId) {
    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("widgetId", new Object[] { widgetId });
    return getByWhere(paramMap);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PtoneWidgetInfo> findById(String pid) {
    // String sql =
    // "SELECT w.* FROM ptone_widget_info w "
    // +
    // "        WHERE  w.Widget_ID IN  ( SELECT Widget_ID FROM ptone_panel_widget WHERE Panel_ID = :Panel_ID) and w.status = 1 and w.is_Template = 0";
    String sql = "SELECT  w.* FROM ptone_widget_info w INNER JOIN ptone_panel_widget pw ON w.Widget_ID = pw.Widget_ID "
        + " WHERE pw.Panel_ID = :Panel_ID and w.status = 1 and w.is_Template = 0 and pw.is_delete = :is_delete";
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("Panel_ID", pid);
    paramMap.put("is_delete", Constants.inValidate);
    return (List<PtoneWidgetInfo>) DataOperationUtils.queryForList(sql, paramMap,
        PtoneWidgetInfo.class);
  }

  @Override
  public void savePanelWidgetRelation(PtonePanelWidget relation) {
    jdbcDao.insert(relation);
  }

  @Override
  public void deleteWidget(String panelId) {
    String sql = "UPDATE ptone_widget_info SET status = 0 WHERE panel_Id = ?";
    Object param[] = { panelId };
    DataOperationUtils.insert(sql, param);
  }

  @Override
  public List<PtoneWidgetInfo> findChildWidgetById(String widgetId) {
    String sql = "SELECT w.Widget_ID  FROM ptone_widget_info w WHERE w.parent_id = :widgetId";
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("widgetId", widgetId);
    return (List<PtoneWidgetInfo>) DataOperationUtils.queryForList(sql, paramMap,
        PtoneWidgetInfo.class);
  }

  @Override
  public List<PtoneWidgetInfo> findWidgetByPanelId(String panelId) {
    String sql = "SELECT w.Widget_ID  FROM ptone_widget_info w WHERE w.Panel_ID = :panelId";
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("panelId", panelId);
    return (List<PtoneWidgetInfo>) DataOperationUtils.queryForList(sql, paramMap,
        PtoneWidgetInfo.class);
  }

}
