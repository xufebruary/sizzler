package com.sizzler.domain.panel.vo;

import java.io.Serializable;
import java.util.List;

import com.sizzler.domain.panel.PtonePanelLayout;

public class PanelExtVo implements Serializable {

  private static final long serialVersionUID = -5906744181334023644L;

  private Object panel; // PanelInfoWithComponents || PtonePanelTemplet || PanelLayoutNode, 对应原panel panelInfo, panelFolder
  @SuppressWarnings("rawtypes")
  private List panelList; // List<PtonePanelInfo> || List<PtonePanelTemplet>， 对应原 resultList
  private PtonePanelLayout panelLayout; // 对应原: resultLayout
//  private List<AcceptWidget> widgetList; // 对应原： widgetsList :  // 确认是否前端已经不使用(create-by-templet)
  private String status;

  public Object getPanel() {
    return panel;
  }

  public void setPanel(Object panel) {
    this.panel = panel;
  }

  @SuppressWarnings("rawtypes")
  public List getPanelList() {
    return panelList;
  }

  @SuppressWarnings("rawtypes")
  public void setPanelList(List panelList) {
    this.panelList = panelList;
  }

  public PtonePanelLayout getPanelLayout() {
    return panelLayout;
  }

  public void setPanelLayout(PtonePanelLayout panelLayout) {
    this.panelLayout = panelLayout;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
