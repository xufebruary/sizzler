package com.sizzler.proxy.dispatcher;

import java.util.Map;

import com.sizzler.cache.CurrentUserCache;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.widget.GaWidgetInfo;
import com.sizzler.domain.widget.PtoneWidgetInfo;

public class PtoneDatasourceDesc {

  private String datasourceType;
  private PtoneWidgetInfo ptoneWidgetInfo;
  private PtoneVariableInfo ptoneVariableInfo;
  private String graphType;
  private PtoneWidgetParam ptoneWidgetParam;
  private Map<String, String> webParamMap;
  private CurrentUserCache currentUserCache;
  private GaWidgetInfo gaWidgetInfo;
  private UserConnection userConnection;

  public PtoneDatasourceDesc(String datasourceType) {
    this.datasourceType = datasourceType;
  }

  public String getDatasourceType() {
    return datasourceType;
  }

  public void setDatasourceType(String datasourceType) {
    this.datasourceType = datasourceType;
  }

  public PtoneWidgetInfo getPtoneWidgetInfo() {
    return ptoneWidgetInfo;
  }

  public void setPtoneWidgetInfo(PtoneWidgetInfo ptoneWidgetInfo) {
    this.ptoneWidgetInfo = ptoneWidgetInfo;
  }

  public PtoneVariableInfo getPtoneVariableInfo() {
    return ptoneVariableInfo;
  }

  public void setPtoneVariableInfo(PtoneVariableInfo ptoneVariableInfo) {
    this.ptoneVariableInfo = ptoneVariableInfo;
  }

  public String getGraphType() {
    return graphType;
  }

  public void setGraphType(String graphType) {
    this.graphType = graphType;
  }

  public PtoneWidgetParam getPtoneWidgetParam() {
    return ptoneWidgetParam;
  }

  public void setPtoneWidgetParam(PtoneWidgetParam ptoneWidgetParam) {
    this.ptoneWidgetParam = ptoneWidgetParam;
  }

  public Map<String, String> getWebParamMap() {
    return webParamMap;
  }

  public void setWebParamMap(Map<String, String> webParamMap) {
    this.webParamMap = webParamMap;
  }

  public CurrentUserCache getCurrentUserCache() {
    return currentUserCache;
  }

  public void setCurrentUserCache(CurrentUserCache currentUserCache) {
    this.currentUserCache = currentUserCache;
  }

  public String getKey() {
    return datasourceType + "";
  }

  public GaWidgetInfo getGaWidgetInfo() {
    return gaWidgetInfo;
  }

  public void setGaWidgetInfo(GaWidgetInfo gaWidgetInfo) {
    this.gaWidgetInfo = gaWidgetInfo;
  }

  public UserConnection getUserConnection() {
    return userConnection;
  }

  public void setUserConnection(UserConnection userConnection) {
    this.userConnection = userConnection;
  }

}
