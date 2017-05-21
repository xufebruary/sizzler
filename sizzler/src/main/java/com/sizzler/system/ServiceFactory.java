package com.sizzler.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sizzler.cache.PtoneBasicDictCache;
import com.sizzler.cache.PtoneDsInfoCache;
import com.sizzler.dao.PanelWidgetDao;
import com.sizzler.datasource.proxy.DataSourceBuild;
import com.sizzler.provider.common.EditorDataProvider;
import com.sizzler.provider.common.MetaProvider;
import com.sizzler.provider.common.UpdateDataProvider;
import com.sizzler.service.DataSourceManagerExtService;
import com.sizzler.service.DataSourceManagerService;
import com.sizzler.service.GaWidgetService;
import com.sizzler.service.MetaLogService;
import com.sizzler.service.PanelGlobalComponentService;
import com.sizzler.service.PanelLayoutService;
import com.sizzler.service.PanelService;
import com.sizzler.service.PanelWidgetService;
import com.sizzler.service.PtoneBasicService;
import com.sizzler.service.PtoneDataService;
import com.sizzler.service.PtoneDictService;
import com.sizzler.service.PtonePermissionManagerService;
import com.sizzler.service.PtoneShortUrlService;
import com.sizzler.service.PtoneUserConnectionService;
import com.sizzler.service.SysService;
import com.sizzler.service.UploadService;
import com.sizzler.service.UserService;
import com.sizzler.service.UserSettingService;
import com.sizzler.service.VariableService;
import com.sizzler.service.WidgetChartSettingService;
import com.sizzler.service.WidgetExtendService;
import com.sizzler.service.WidgetService;
import com.sizzler.service.WidgetVariableService;
import com.sizzler.service.ds.PtoneDsService;
import com.sizzler.service.ds.UserCompoundMetricsDimensionService;
import com.sizzler.service.ds.UserConnectionSourceService;
import com.sizzler.service.ds.UserConnectionSourceTableColumnService;
import com.sizzler.service.ds.UserConnectionSourceTableService;
import com.sizzler.service.space.SpaceService;
import com.sizzler.service.space.SpaceUserService;
import com.sizzler.system.listener.SessionContext;

@Component("serviceFactory")
public class ServiceFactory {

  @Autowired
  private SessionContext sessionContext;

  @Autowired
  private PtoneBasicService ptoneBasicService;

  @Autowired
  private PtoneDataService ptoneDataService;

  @Autowired
  private PtoneDictService ptoneDictService;

  @Autowired
  private SysService sysService;

  @Autowired
  private UploadService uploadService;

  @Autowired
  private PtoneShortUrlService ptoneShortUrlService;

  @Autowired
  private DataSourceBuild dataSourceBuild;
  @Autowired
  private PanelGlobalComponentService panelGlobalComponentService;
  @Autowired
  private PtoneBasicDictCache ptoneBasicDictCache;
  @Autowired
  private WidgetService widgetService;
  @Autowired
  private PtoneUserConnectionService ptoneUserConnectionService;
  @Autowired
  private UserConnectionSourceService userConnectionSourceService;
  @Autowired
  private UserConnectionSourceTableService userConnectionSourceTableService;
  @Autowired
  private UserConnectionSourceTableColumnService userConnectionSourceTableColumnService;
  @Autowired
  private UserCompoundMetricsDimensionService userCompoundMetricsDimensionService;
  @Autowired
  private PtoneDsInfoCache ptoneDsInfoCache;
  @Autowired
  private MetaProvider metaProvider;
  @Autowired
  private UpdateDataProvider updateDataProvider;
  @Autowired
  private EditorDataProvider editorDataProvider;
  @Autowired
  private DataSourceManagerService dataSourceManagerService;
  @Autowired
  private DataSourceManagerExtService dataSourceManagerExtService;
  @Autowired
  private GaWidgetService gaWidgetService;
  @Autowired
  private PanelService panelService;
  @Autowired
  private VariableService variableService;
  @Autowired
  private UserService userService;
  @Autowired
  private UserSettingService userSettingService;
  @Autowired
  private PanelLayoutService panelLayoutService;
  @Autowired
  private SpaceService spaceService;
  @Autowired
  private PtonePermissionManagerService ptonePermissionManagerService;
  @Autowired
  private PtoneDsService ptoneDsService;
  @Autowired
  private MetaLogService metaLogService;
  @Autowired
  private PanelWidgetDao panelWidgetDao;
  @Autowired
  private WidgetVariableService widgetVariableService;
  @Autowired
  private WidgetChartSettingService widgetChartSettingService;
  @Autowired
  private WidgetExtendService widgetExtendService;
  @Autowired
  private PanelWidgetService panelWidgetService;
  @Autowired
  private SpaceUserService spaceUserService;

  public MetaLogService getMetaLogService() {
    return metaLogService;
  }

  public PtonePermissionManagerService getPtonePermissionManagerService() {
    return ptonePermissionManagerService;
  }

  public SessionContext getSessionContext() {
    return sessionContext;
  }

  public PtoneUserConnectionService getPtoneUserConnectionService() {
    return ptoneUserConnectionService;
  }

  public DataSourceManagerService getDataSourceManagerService() {
    return dataSourceManagerService;
  }

  public PtoneBasicService getPtoneBasicService() {
    return ptoneBasicService;
  }

  public PtoneDataService getPtoneDataService() {
    return ptoneDataService;
  }

  public PtoneDsService getPtoneDsService() {
    return ptoneDsService;
  }

  public PtoneDictService getPtoneDictService() {
    return ptoneDictService;
  }

  public WidgetService getWidgetService() {
    return widgetService;
  }

  public UserService getUserService() {
    return userService;
  }

  public PanelService getPanelService() {
    return panelService;
  }

  public SysService getSysService() {
    return sysService;
  }

  public PanelLayoutService getPanelLayoutService() {
    return panelLayoutService;
  }

  public SpaceService getSpaceService() {
    return spaceService;
  }

  public UploadService getUploadService() {
    return uploadService;
  }

  public PtoneShortUrlService getPtoneShortUrlService() {
    return ptoneShortUrlService;
  }

  public DataSourceBuild getDataSourceBuild() {
    return dataSourceBuild;
  }

  public PanelGlobalComponentService getPanelGlobalComponentService() {
    return panelGlobalComponentService;
  }

  public PtoneBasicDictCache getPtoneBasicDictCache() {
    return ptoneBasicDictCache;
  }

  public UserConnectionSourceService getUserConnectionSourceService() {
    return userConnectionSourceService;
  }

  public UserConnectionSourceTableService getUserConnectionSourceTableService() {
    return userConnectionSourceTableService;
  }

  public UserConnectionSourceTableColumnService getUserConnectionSourceTableColumnService() {
    return userConnectionSourceTableColumnService;
  }

  public UserCompoundMetricsDimensionService getUserCompoundMetricsDimensionService() {
    return userCompoundMetricsDimensionService;
  }

  public PtoneDsInfoCache getPtoneDsInfoCache() {
    return ptoneDsInfoCache;
  }

  public MetaProvider getMetaProvider() {
    return metaProvider;
  }

  public UpdateDataProvider getUpdateDataProvider() {
    return updateDataProvider;
  }

  public EditorDataProvider getEditorDataProvider() {
    return editorDataProvider;
  }

  public DataSourceManagerExtService getDataSourceManagerExtService() {
    return dataSourceManagerExtService;
  }

  public GaWidgetService getGaWidgetService() {
    return gaWidgetService;
  }

  public VariableService getVariableService() {
    return variableService;
  }

  public PanelWidgetDao getPanelWidgetDao() {
    return panelWidgetDao;
  }

  public WidgetVariableService getWidgetVariableService() {
    return widgetVariableService;
  }

  public WidgetChartSettingService getWidgetChartSettingService() {
    return widgetChartSettingService;
  }

  public WidgetExtendService getWidgetExtendService() {
    return widgetExtendService;
  }

  public PanelWidgetService getPanelWidgetService() {
    return panelWidgetService;
  }

  public SpaceUserService getSpaceUserService() {
    return spaceUserService;
  }

  public UserSettingService getUserSettingService() {
    return userSettingService;
  }

}
