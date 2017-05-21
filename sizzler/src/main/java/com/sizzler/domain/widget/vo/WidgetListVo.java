package com.sizzler.domain.widget.vo;

import java.io.Serializable;
import java.util.List;

import com.sizzler.domain.widget.dto.AcceptWidget;

public class WidgetListVo implements Serializable {

  private static final long serialVersionUID = -7673308301537868387L;

  private List<AcceptWidget> widgetList;
  private String layout;

  public List<AcceptWidget> getWidgetList() {
    return widgetList;
  }

  public void setWidgetList(List<AcceptWidget> widgetList) {
    this.widgetList = widgetList;
  }

  public String getLayout() {
    return layout;
  }

  public void setLayout(String layout) {
    this.layout = layout;
  }
}
