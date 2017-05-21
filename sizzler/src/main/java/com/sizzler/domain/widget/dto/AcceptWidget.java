package com.sizzler.domain.widget.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sizzler.domain.variable.dto.PtVariables;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.domain.widget.PtoneWidgetInfoExtend;

/**
 * Created by li.zhang on 2015/4/8.
 */
public class AcceptWidget implements Serializable {

  private static final long serialVersionUID = -5697608547400658800L;

  private String panelId;
  private PtoneWidgetInfo baseWidget;
  private PtoneWidgetInfoExtend toolData; // 工具型图表数据
  private PtoneWidgetChartSettingDto chartSetting;
  private List<PtVariables> variables;
  private List<Long> tags;
  private List<AcceptWidget> children; // 子widget列表
  private Object layout; // 自定义widget的布局信息, 值持久化到ptone_widget_info_extend表的layout字段中
  private Map<String, Object> _ext = new HashMap<String, Object>();

  public PtoneWidgetChartSettingDto getChartSetting() {
    return chartSetting;
  }

  public void setChartSetting(PtoneWidgetChartSettingDto chartSetting) {
    this.chartSetting = chartSetting;
  }

  public PtoneWidgetInfoExtend getToolData() {
    return toolData;
  }

  public void setToolData(PtoneWidgetInfoExtend toolData) {
    this.toolData = toolData;
  }

  public List<Long> getTags() {
    return tags;
  }

  public void setTags(List<Long> tags) {
    this.tags = tags;
  }

  public List<PtVariables> getVariables() {
    return variables;
  }

  public void setVariables(List<PtVariables> variables) {
    this.variables = variables;
  }

  public String getPanelId() {
    return panelId;
  }

  public void setPanelId(String panelId) {
    this.panelId = panelId;
  }

  public PtoneWidgetInfo getBaseWidget() {
    return baseWidget;
  }

  public void setBaseWidget(PtoneWidgetInfo baseWidget) {
    this.baseWidget = baseWidget;
  }

  public Map<String, Object> get_ext() {
    return _ext;
  }

  public void set_ext(Map<String, Object> _ext) {
    this._ext = _ext;
  }

  public List<AcceptWidget> getChildren() {
    return children;
  }

  public void setChildren(List<AcceptWidget> children) {
    this.children = children;
  }

  public Object getLayout() {
    return layout;
  }

  public void setLayout(Object layout) {
    this.layout = layout;
  }

}
