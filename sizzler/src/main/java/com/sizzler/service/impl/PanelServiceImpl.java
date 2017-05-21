package com.sizzler.service.impl;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.SourceType;
import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.common.exception.BusinessErrorCode;
import com.sizzler.common.exception.BusinessException;
import com.sizzler.common.lock.DistributedLockConstants;
import com.sizzler.common.lock.annotation.DistributedLock;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.common.utils.UuidUtil;
import com.sizzler.dao.GaWidgetDao;
import com.sizzler.dao.PanelDao;
import com.sizzler.dao.PanelWidgetDao;
import com.sizzler.dao.WidgetDao;
import com.sizzler.domain.ds.UserCompoundMetricsDimension;
import com.sizzler.domain.panel.PanelGlobalComponent;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.panel.PtonePanelLayout;
import com.sizzler.domain.panel.dto.PanelInfoExt;
import com.sizzler.domain.panel.dto.PanelInfoWithComponents;
import com.sizzler.domain.panel.dto.PanelLayoutNode;
import com.sizzler.domain.panel.vo.PanelExtVo;
import com.sizzler.domain.panel.vo.SharePanelVerifyVo;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.space.PtoneSpaceUser;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.variable.PtoneVariableInfo;
import com.sizzler.domain.widget.GaWidgetInfo;
import com.sizzler.domain.widget.PtonePanelWidget;
import com.sizzler.domain.widget.PtoneWidgetChartSetting;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.domain.widget.PtoneWidgetInfoExtend;
import com.sizzler.domain.widget.dto.AcceptWidget;
import com.sizzler.domain.widget.vo.WidgetListVo;
import com.sizzler.service.PanelService;
import com.sizzler.service.WidgetChartSettingService;
import com.sizzler.service.WidgetExtendService;
import com.sizzler.service.WidgetService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.util.CascadeDeleteUtil;

@Service("panelService")
public class PanelServiceImpl extends ServiceBaseInterfaceImpl<PtonePanelInfo, String> implements
    PanelService {

  private Logger logger = LoggerFactory.getLogger(PanelServiceImpl.class);

  @Autowired
  private PanelDao panelDao;

  @Autowired
  private WidgetDao widgetDao;
  
  @Autowired
  private PanelWidgetDao panelWidgetDao;
  
  @Autowired
  private GaWidgetDao gaWidgetDao;

  @Autowired
  private ServiceFactory serviceFactory;

  @Autowired
  private WidgetService widgetService;
  
  @Autowired
  private WidgetChartSettingService widgetChartSettingService;
  
  @Autowired
  private WidgetExtendService widgetExtendService;

  @Override
  public String validateSharePanel(String panelId, String password) {
    String result = PtonePanelInfo.PANEL_STATUS_VALIDATE;
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("panelId", new Object[] {panelId});
    paramMap.put("status", new Object[] {Constants.validate});
    PtonePanelInfo panel = panelDao.getByWhere(paramMap);
    if (panel != null) {
      String shareUrl = panel.getShareUrl();
      String sharePassword = panel.getSharePassword();
      // 已关闭分享
      if (!Constants.validate.equals(shareUrl)) {
        result = PtonePanelInfo.PANEL_STATUS_SHARE_OFF;
      } else {
        // 校验密码
        if (StringUtil.isNotBlank(sharePassword)) {
          if (!sharePassword.equals(password)) {
            result = PtonePanelInfo.PANEL_STATUS_SHARE_PSWD_ERROR;
          }
        }
        // 校验空间删除
        String spaceId = panel.getSpaceId();
        PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
        if (spaceInfo == null) {
          result = PtonePanelInfo.PANEL_STATUS_SPACE_DELETE;
        }
      }
    } else {
      result = PtonePanelInfo.PANEL_STATUS_PANEL_DELETE;
    }
    return result;
  }

  @Override
  @Transactional
  public void addNewPanel(PtonePanelInfo panel, String uid) {
    panel.setIsByTemplet(Constants.inValidate);
    panel.setIsByDefault(Constants.inValidate);
    panel.setSourceType(SourceType.Panel.USER_CREATED);
    this.insert(panel);
    serviceFactory.getPanelGlobalComponentService().save(
        createDefaultTimeComponent(panel.getPanelId(), uid));
  }

  @Transactional
  @Deprecated
  public PtonePanelLayout addNewPanelAndUpdatePanelLayout(PtonePanelInfo panel, String uid,
      PtonePanelLayout panelLayout) {
    addNewPanel(panel, uid);
    if (panelLayout != null) {
      panelLayout = serviceFactory.getPanelLayoutService().updateDataVersion(panelLayout);
    }
    return panelLayout;
  }

  @Override
  @Transactional
  @DistributedLock(name = DistributedLockConstants.DISTRIBUTED_LOCK_PANEL_LAYOUT,
      interval = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_WAIT_INTERVAL,
      timeout = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_TIMEOUT)
  public PanelExtVo addPanelAndLayout(PtonePanelInfo panel, String uid, String spaceId) {
    PanelExtVo panelExtVo = null;
    if (panel != null) {
      PanelLayoutNode newPanelLayoutNode = new PanelLayoutNode(PanelLayoutNode.TYPE_PANEL);
      newPanelLayoutNode.setPanelId(panel.getPanelId());
      newPanelLayoutNode.setPanelTitle(panel.getPanelTitle());
      newPanelLayoutNode.setShareSourceId(panel.getShareSourceId());
      panelExtVo =
          serviceFactory.getPanelLayoutService().operatePanelLayout(newPanelLayoutNode, spaceId,
              PanelLayoutNode.OPERATION_ADD);
    }

    // 新增panel
    addNewPanel(panel, uid);
    
    // 获取创建后的paneld对象
    String panelId = panel.getPanelId();
    Object newPanelInfo = this.getPanelWithComponentsById(panelId, false);
    panelExtVo.setPanel(newPanelInfo);

    return panelExtVo;
  }

  @Override
  @Transactional
  @DistributedLock(name = DistributedLockConstants.DISTRIBUTED_LOCK_PANEL_LAYOUT,
      interval = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_WAIT_INTERVAL,
      timeout = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_TIMEOUT)
  public PanelExtVo addPanelByTemplet(String templetId, String spaceId, String uid)
      throws BusinessException {
    PanelExtVo panelExtVo = new PanelExtVo();
//    try {
//      PtonePanelTemplet panelTemplet = serviceFactory.getPanelTempletService().get(templetId);
//      PtoneUserBasicSetting setting = serviceFactory.getUserSettingService().get(uid);
//
//      PtonePanelInfo newPanel =
//          serviceFactory.getPanelTempletService().copyWholePanelByTemplet(panelTemplet, spaceId,
//              uid, setting.getLocale(), false);
//
//      this.saveComponents(createDefaultTimeComponent(newPanel.getPanelId(), uid));
//
//      if (newPanel != null) {
//        PanelLayoutNode newPanelLayoutNode = new PanelLayoutNode(PanelLayoutNode.TYPE_PANEL);
//        newPanelLayoutNode.setPanelId(newPanel.getPanelId());
//        newPanelLayoutNode.setPanelTitle(newPanel.getPanelTitle());
//        newPanelLayoutNode.setShareSourceId(newPanel.getShareSourceId());
//        panelExtVo =
//            serviceFactory.getPanelLayoutService().operatePanelLayout(newPanelLayoutNode, spaceId,
//                PanelLayoutNode.OPERATION_ADD);
//      }
//
//      panelExtVo.setPanel(newPanel); // panel
//
//    } catch (Exception e) {
//      String errorMsg = "create Panel by templet<" + templetId + "> error.";
//      logger.error(errorMsg, e);
//      throw new BusinessException(BusinessErrorCode.Panel.ADD_PANEL_BY_TEMPLET_ERROR, errorMsg);
//    }
    return panelExtVo;
  }

  public PanelGlobalComponent createDefaultTimeComponent(String panelId, String uid) {
    PanelGlobalComponent component = new PanelGlobalComponent();
    component.setPanelId(panelId);
    component.setUid(uid);
    component.setItemId("16");
    component.setName("GLOBAL_TIME");
    component.setCode("GLOBAL_TIME");
    component.setValue("widgetTime");
    return component;
  }

  @Override
  @Transactional
  public void deletePanel(String panelId, boolean isDelete) {
    if (StringUtil.isBlank(panelId)) {
      return;
    }
    // status 在数据库中，0标识已删除，1标识有效
    String status = CascadeDeleteUtil.setStatus(isDelete);

    Map<String, Object[]> panelIdMap = new HashMap<>();
    panelIdMap.put("panelId", new Object[] {panelId});
    PtonePanelInfo panel = new PtonePanelInfo();
    panel.setPanelId(panelId);
    panel.setStatus(status);
    // ptone_panel_info
    this.update(panel);

    Map<String, Map<String, String>> updateMap = CascadeDeleteUtil.buildParamMap(isDelete);

    Map<String, String> statusMap = updateMap.get(CascadeDeleteUtil.STATUS);
    // ptone_widget_info 更新
    serviceFactory.getWidgetService().update(panelIdMap, statusMap);
    // panel_global_component
    serviceFactory.getPanelGlobalComponentService().update(panelIdMap,
        updateMap.get(CascadeDeleteUtil.IS_DELETE));

    widgetService.deleteWidgetCorrelation(buildWidgetIdArray(panelId), updateMap);
    updatePanelShareSourceStatus(panelId, status);// 来源panel状态 0：已删除
  }


  @Override
  public Map<String, Object[]> buildWidgetIdArray(String panelId) {
    Map<String, Object[]> paramMap = new HashMap<>(1);
    List<Object> widgetList = new ArrayList<Object>();
    widgetList.add(panelId);
    List<PtoneWidgetInfo> widgetIdList = widgetDao.findWidgetByPanelId(panelId);
    if (CollectionUtil.isNotEmpty(widgetIdList)) {
      for (PtoneWidgetInfo ptoneWidgetInfo : widgetIdList) {
        widgetList.add(ptoneWidgetInfo.getWidgetId());
      }
    }
    Object[] widgetIdArray = widgetList.toArray();
    paramMap.put("widgetId", widgetIdArray);
    return paramMap;
  }

  @Deprecated
  @Transactional
  public PtonePanelLayout deletePanelAndUpdatePanelLayout(String panelId,
      PtonePanelLayout panelLayout, boolean isDelete) {
    deletePanel(panelId, isDelete);
    if (panelLayout != null) {
      panelLayout = serviceFactory.getPanelLayoutService().updateDataVersion(panelLayout);
    }
    return panelLayout;
  }

  @Override
  @Transactional
  @DistributedLock(name = DistributedLockConstants.DISTRIBUTED_LOCK_PANEL_LAYOUT,
      interval = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_WAIT_INTERVAL,
      timeout = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_TIMEOUT)
  public PanelExtVo deletePanelAndLayout(String panelId, String spaceId, boolean isDelete) {
    PanelExtVo PanelExtVo = null;
    if (StringUtil.isNotBlank(panelId)) {
      PanelLayoutNode newPanelLayoutNode = new PanelLayoutNode(PanelLayoutNode.TYPE_PANEL);
      newPanelLayoutNode.setPanelId(panelId);
      PanelExtVo =
          serviceFactory.getPanelLayoutService().operatePanelLayout(newPanelLayoutNode, spaceId,
              PanelLayoutNode.OPERATION_DELETE);
    }

    // 删除panel
    deletePanel(panelId, isDelete);

    return PanelExtVo;
  }


  @Transactional
  public void batchDeletePanels(String[] panelIds, boolean isDelete) {
    if (null != panelIds && ArrayUtils.isNotEmpty(panelIds)) {
      for (String panelId : panelIds) {
        if (StringUtil.isBlank(panelId)) {
          continue;
        }
        deletePanel(panelId, isDelete);
      }
    }
  }

  @Override
  @Deprecated
  @Transactional
  public PtonePanelLayout deleteBatchPanelsAndUpdatePanelLayout(String[] panelIds,
      PtonePanelLayout panelLayout, boolean isDelete) {
    batchDeletePanels(panelIds, isDelete);
    if (panelLayout != null) {
      panelLayout = serviceFactory.getPanelLayoutService().updateDataVersion(panelLayout);
    }
    return panelLayout;
  }

  @Override
  @Transactional
  @DistributedLock(name = DistributedLockConstants.DISTRIBUTED_LOCK_PANEL_LAYOUT,
      interval = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_WAIT_INTERVAL,
      timeout = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_TIMEOUT)
  public PanelExtVo batchDeletePanelsAndLayout(String panelFolderId, String[] panelIds,
      String spaceId, boolean isDelete) {
    PanelExtVo panelExtVo = null;
    if (StringUtil.isNotBlank(panelFolderId)) {
      PanelLayoutNode newPanelLayoutNode = new PanelLayoutNode(PanelLayoutNode.TYPE_CONTAINER);
      newPanelLayoutNode.setContainerId(panelFolderId);
      panelExtVo =
          serviceFactory.getPanelLayoutService().operatePanelLayout(newPanelLayoutNode, spaceId,
              PanelLayoutNode.OPERATION_DELETE);
    }

    // 批量删除panel
    batchDeletePanels(panelIds, isDelete);

    return panelExtVo;
  }

  @Override
  public List<PanelGlobalComponent> findGlobalComponents(List<String> panelIdList) {
    List<PanelGlobalComponent> list = new ArrayList<PanelGlobalComponent>();
    if (panelIdList != null && panelIdList.size() > 0) {
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("panelId", panelIdList.toArray());
      list = serviceFactory.getPanelGlobalComponentService().findByWhere(paramMap);
    }
    return list;
  }

  @Override
  @Transactional
  public void updateComponents(PanelGlobalComponent component) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("panelId", new Object[] {component.getPanelId()});
    serviceFactory.getPanelGlobalComponentService().update(component);
    List<PanelGlobalComponent> componentList =
        serviceFactory.getPanelGlobalComponentService().findByWhere(paramMap);
    boolean updatePanelGlobalComponentStatusFlag = true;
    for (PanelGlobalComponent com : componentList) {
      if (com.getStatus().equals(Constants.validate)) {
        updatePanelGlobalComponentStatusFlag = false;
        break;
      }
    }
    if (updatePanelGlobalComponentStatusFlag) {
      updatePanelGlobalComponentStatus(component.getPanelId(), Constants.inValidate);
    }
    /*
     * if(component.getStatus().equals(Constants.validate)){
     * updatePanelGlobalComponentStatus(component.getPanelId(),Constants.validate); }
     */

  }

  @Override
  @Transactional
  public void saveComponents(PanelGlobalComponent component) {
    updatePanelGlobalComponentStatus(component.getPanelId(), Constants.validate);
    serviceFactory.getPanelGlobalComponentService().save(component);
  }

  @Override
  public PanelGlobalComponent getComponents(Map<String, Object[]> paramMap) {
    return serviceFactory.getPanelGlobalComponentService().getByWhere(paramMap);
  }

  @Override
  public void applyPanelComponent(PanelGlobalComponent component) throws BusinessException {
    try {
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("panelId", new Object[] {component.getPanelId()});
      paramMap.put("itemId", new Object[] {component.getItemId()});
      PanelGlobalComponent dbComponent = this.getComponents(paramMap);
      if (null == dbComponent) {
        this.saveComponents(component);
      } else {
        component.setId(dbComponent.getId());
        this.updateComponents(component);
      }
    } catch (Exception e) {
      String errorMsg = "apply panel Components error:" + e.getMessage();
      logger.error(errorMsg, e);
      throw new BusinessException(BusinessErrorCode.Panel.APPLY_PANEL_COMPONENT_ERROR, errorMsg);
    }
  }

  @Override
  @Transactional
  public void cancelPanelComponent(String panelId) {
    updatePanelGlobalComponentStatus(panelId, Constants.inValidate);
    serviceFactory.getPanelGlobalComponentService().cancelPanelComponent(panelId);
  }

  public void updatePanelGlobalComponentStatus(String panelId, String status) {
    PtonePanelInfo panel = new PtonePanelInfo();
    panel.setPanelId(panelId);
    panel.setGlobalComponentStatus(status);
    /*
     * //全局控件开启，则panel只读 if(status.equals(Constants.validate)){ panel.setAccess("ONLY"); }else{
     * panel.setAccess("DEFAULT"); }
     */
    this.update(panel);
  }

  public boolean isExistsPanelInSpace(String panelId, String spaceId) throws BusinessException {

    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("panelId", new Object[] {panelId});
    paramMap.put("spaceId", new Object[] {spaceId});
    paramMap.put("status", new Object[] {Constants.validate});
    PtonePanelInfo panelInfo = this.getByWhere(paramMap);
    if (null != panelInfo) {
      return true;
    }
    paramMap = new HashMap<>();
    paramMap.put("shareSourceId", new Object[] {panelId});
    paramMap.put("spaceId", new Object[] {spaceId});
    paramMap.put("status", new Object[] {Constants.validate});
    panelInfo = this.getByWhere(paramMap);
    if (null != panelInfo) {
      return true;
    }
    return false;
  }

  @Override
  @Transactional
  public String addSharePanel(String sharePanelId, String uid, String spaceId,
      PanelGlobalComponent component) throws BusinessException {
    try {
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("panelId", new Object[] {sharePanelId});
      paramMap.put("status", new Object[] {Constants.validate});
      PtonePanelInfo sharePanel = this.getByWhere(paramMap);
      if (sharePanel == null) {
        return PtonePanelInfo.PANEL_STATUS_PANEL_DELETE;
      } else {
        if (!Constants.validate.equals(sharePanel.getShareUrl())) {
          return PtonePanelInfo.PANEL_STATUS_SHARE_OFF;
        }

        String srcSpaceId = sharePanel.getSpaceId();
        PtoneSpaceInfo shareSpaceInfo = serviceFactory.getSpaceService().get(srcSpaceId);
        if (shareSpaceInfo == null) {
          return PtoneSpaceInfo.SRC_SPACE_STATUS_DELETE;
        }
      }

      PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
      if (spaceInfo == null) {
        return PtoneSpaceInfo.TARGET_SPACE_STATUS_DELETE;
      }

      PtoneSpaceUser spaceUser = serviceFactory.getSpaceService().getSpaceUserByUid(spaceId, uid);
      if (spaceUser == null || !PtoneSpaceUser.STATUS_ACCEPTED.equals(spaceUser.getStatus())) {
        return PtoneSpaceInfo.SPACE_STATUS_NOT_IN;
      }

      if (this.isExistsPanelInSpace(sharePanelId, spaceId)) {
        return PtonePanelInfo.PANEL_STATUS_EXISTS;
      }

      PtoneUser shareUser = serviceFactory.getUserService().get(sharePanel.getCreatorId());
      paramMap = new HashMap<>();
      paramMap.put("creatorId", new Object[] {uid});
      paramMap.put("status", new Object[] {Constants.validate});
      int panelCount = this.queryCount(paramMap);
      PtonePanelInfo newPanel = new PtonePanelInfo();
      newPanel.setShareSourceId(sharePanelId);
      newPanel.setPanelId(UUID.randomUUID().toString());
      newPanel.setSpaceId(spaceId);
      newPanel.setPanelTitle(sharePanel.getPanelTitle());
      newPanel.setCreatorId(uid);
      newPanel.setCreateTime(System.currentTimeMillis());
      newPanel.setStatus(Constants.validate);
      newPanel.setLayout(sharePanel.getLayout());
      newPanel.setPanelWidth(sharePanel.getPanelWidth());
      newPanel.setAccess("ONLY");
      newPanel.setOrderNumber(++panelCount);
      newPanel.setShareSourceStatus(Constants.published);// 正常状态 2
      newPanel.setShareSourceUsername(shareUser.getUserName());
      newPanel.setIsByTemplet(Constants.inValidate);
      newPanel.setIsByDefault(Constants.inValidate);
      newPanel.setSourceType(SourceType.Panel.USER_CREATED);
      this.save(newPanel);

      component.setPanelId(newPanel.getPanelId());
      component.setUid(uid);
      component.setItemId(PanelGlobalComponent.COMPONENT_ITEM_ID_GLOBAL_TIME);
      component.setName(PanelGlobalComponent.COMPONENT_ITEM_CODE_GLOBAL_TIME);
      component.setCode(PanelGlobalComponent.COMPONENT_ITEM_CODE_GLOBAL_TIME);
      /*
       * if(null != component.getValue() && !component.getValue().equals("")){
       * component.setStatus(Constants.validate); }else{ component.setValue("widgetTime");
       * component.setStatus(Constants.inValidate); }
       */
      // 保存时间组件
      this.saveComponents(component);

      // 修改或者创建panelLayout
      serviceFactory.getPanelLayoutService().updateOrCreatePanelLayout(uid, spaceId, newPanel);
    } catch (Exception e) {
      String errorMsg = "add share panel error:" + e.getMessage();
      logger.error(errorMsg, e);
      throw new BusinessException(BusinessErrorCode.Panel.ADD_SHARE_PANEL_ERROR, errorMsg);
    }
    return Constants.JSON_VIEW_STATUS_SUCCESS;
  }

  @Override
  public void updatePanelShareSourceStatus(String sharePanelId, String status) {
    panelDao.updatePanelShareSourceStatus(sharePanelId, status);
  }

  @Override
  @Transactional
  public void updatePanelInfo(PtonePanelInfo panel) {
    this.update(panel);
    if (StringUtil.hasText(panel.getShareUrl())) {
      // 如果分享面板关闭分享，则更新所有链接此面板的状态
      if (panel.getShareUrl().equals(Constants.inValidate)) {
        updatePanelShareSourceStatus(panel.getPanelId(), Constants.validate);// 来源panel状态 0：已关闭分享
      } else if (panel.getShareUrl().equals(Constants.validate)) {// 开启分享
        updatePanelShareSourceStatus(panel.getPanelId(), Constants.published);// 来源panel状态 2: 正常
      }
    }
  }

  @Deprecated
  @Transactional
  public PtonePanelLayout updatePanelInfoAndUpdatePanelLayout(PtonePanelInfo panel,
      PtonePanelLayout panelLayout) {
    updatePanelInfo(panel);
    if (panelLayout != null) {
      panelLayout = serviceFactory.getPanelLayoutService().updateDataVersion(panelLayout);
    }
    return panelLayout;
  }

  @Override
  @Transactional
  @DistributedLock(name = DistributedLockConstants.DISTRIBUTED_LOCK_PANEL_LAYOUT,
      interval = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_WAIT_INTERVAL,
      timeout = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_TIMEOUT)
  public PanelExtVo updatePanelAndLayout(PtonePanelInfo panel, String spaceId) {
    PanelExtVo panelExtVo = null;
    if (panel != null) {
      PanelLayoutNode newPanelLayoutNode = new PanelLayoutNode(PanelLayoutNode.TYPE_PANEL);
      newPanelLayoutNode.setPanelId(panel.getPanelId());
      newPanelLayoutNode.setPanelTitle(panel.getPanelTitle());
      newPanelLayoutNode.setShareSourceId(panel.getShareSourceId());
      panelExtVo =
          serviceFactory.getPanelLayoutService().operatePanelLayout(newPanelLayoutNode, spaceId,
              PanelLayoutNode.OPERATION_UPDATE);
    }

    // 修改panel
    updatePanelInfo(panel);

    return panelExtVo;
  }

  @Override
  public List<PtonePanelInfo> getPanelListBySpaceId(String spaceId) {
    PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
    Map<String, Object[]> paramMap = new HashMap<>();
    // paramMap.put("creatorId", new Object[]{loginPtoneUser.getPtId()});
    paramMap.put("spaceId", new Object[] {spaceId});
    paramMap.put("status", new Object[] {Constants.validate});

    // datadeck用户登录时预制panel模版的顺序，根据原模版的orderNumber字段排序
    Map<String, String> orderMap = new HashMap<>();
    orderMap.put("orderNumber", "asc");

    List<PtonePanelInfo> panelList =
        this.findByWhere(paramMap, orderMap);
    for (PtonePanelInfo panel : panelList) {
      panel.setSpaceName(spaceInfo.getName());
      // 去除widget位置信息，在点击单个panel时和widget信息一起返回
      panel.setLayout(null);
    }
    return panelList;
  }

  @Override
  public PanelExtVo getPanelWithComponentsListBySpaceId(String spaceId, boolean isPanelTemplet) throws BusinessException {
    PanelExtVo panelExtVo = new PanelExtVo();
    try {
      Map<String, Object[]> paramMap = new HashMap<>();
      if (isPanelTemplet) {
//        List<PtonePanelTemplet> templetList =
//            serviceFactory.getPanelTempletService().findByWhere("status", ">",
//                new Object[] {Constants.inValidate});
//        Collections.sort(templetList, new Comparator<PtonePanelTemplet>() {
//          @Override
//          public int compare(PtonePanelTemplet o1, PtonePanelTemplet o2) {
//            Integer v1 = o1.getOrderNumber() == null ? 0 : o1.getOrderNumber();
//            Integer v2 = o2.getOrderNumber() == null ? 0 : o2.getOrderNumber();
//            return v1 - v2;
//          }
//        });
//        PtonePanelLayout panelLayout = serviceFactory.getPanelLayoutService().getByManager();
//
//        panelExtVo.setPanelList(templetList);
//        panelExtVo.setPanelLayout(panelLayout);
      } else {

        List<PanelInfoWithComponents> panelWithComponentstList = new ArrayList<>();
        List<PtonePanelInfo> panelList =
            this.getPanelListBySpaceId(spaceId);

        List<String> panelIdList = new ArrayList<String>();
        Set<String> shareSrcPanelIdList = new HashSet<String>();
        for (PtonePanelInfo panel : panelList) {
          panelIdList.add(panel.getPanelId());
          if (StringUtil.hasText(panel.getShareSourceId())) {
            shareSrcPanelIdList.add(panel.getShareSourceId());
          }
        }
        List<PanelGlobalComponent> componentList =
            this.findGlobalComponents(panelIdList);

        Map<String, PtonePanelInfo> shareSrcPanelMap = new HashMap<String, PtonePanelInfo>();
        Map<String, PtoneSpaceInfo> shareSpaceMap = new HashMap<String, PtoneSpaceInfo>();
        List<PtonePanelInfo> shareSrcPanelList = new ArrayList<PtonePanelInfo>();
        if (shareSrcPanelIdList != null && shareSrcPanelIdList.size() > 0) {
          paramMap = new HashMap<>();
          paramMap.put("panelId", shareSrcPanelIdList.toArray());
          shareSrcPanelList = this.findByWhere(paramMap);

          if (shareSrcPanelList != null && shareSrcPanelList.size() > 0) {
            Set<String> shareSpaceIdList = new HashSet<String>();
            for (PtonePanelInfo sharePanel : shareSrcPanelList) {
              shareSrcPanelMap.put(sharePanel.getPanelId(), sharePanel);
              shareSpaceIdList.add(sharePanel.getSpaceId());
            }
            if (shareSpaceIdList != null && shareSpaceIdList.size() > 0) {
              paramMap = new HashMap<>();
              paramMap.put("spaceId", shareSpaceIdList.toArray());
              paramMap.put("isDelete", new Object[] {Constants.inValidateInt});
              List<PtoneSpaceInfo> shareSpaceList =
                  serviceFactory.getSpaceService().findByWhere(paramMap);
              if (shareSpaceList != null && shareSpaceList.size() > 0) {
                for (PtoneSpaceInfo space : shareSpaceList) {
                  shareSpaceMap.put(space.getSpaceId(), space);
                }
              }
            }
          }
        }

        for (PtonePanelInfo panel : panelList) {
          PanelInfoWithComponents panelInfoWithComponents = new PanelInfoWithComponents();
          Map<String, PanelGlobalComponent> components = new TreeMap<>();
          BeanUtils.copyProperties(panel, panelInfoWithComponents);
          for (PanelGlobalComponent component : componentList) {
            if (panel.getPanelId().equals(component.getPanelId())) {
              components.put(component.getCode(), component);
            }
          }
          // 如果分享的panel过多，则要改变查询方式
          if (StringUtil.hasText(panel.getShareSourceId())) {
            PtonePanelInfo sharePanel = shareSrcPanelMap.get(panel.getShareSourceId());
            if (null != sharePanel) {
              panelInfoWithComponents.setPanelTitle(sharePanel.getPanelTitle());
              panelInfoWithComponents.setDescription(sharePanel.getDescription());
              panelInfoWithComponents.setLayout(sharePanel.getLayout());

              PtoneSpaceInfo shareSpace = shareSpaceMap.get(sharePanel.getSpaceId());
              if (shareSpace != null) {
                panelInfoWithComponents.setSpaceName(shareSpace.getName());
              } else {
                panelInfoWithComponents.setSpaceName("");
                panelInfoWithComponents
                    .setShareSourceStatus(PtonePanelInfo.SHARE_SOURCE_STATUS_SPACE_DELETE);
              }
            }
          }
          panelInfoWithComponents.setComponents(components);
          panelWithComponentstList.add(panelInfoWithComponents);
        }

        // 增加ptone_panel_layout数据信息
        PtonePanelLayout panelLayout = serviceFactory.getPanelLayoutService().getBySpaceId(spaceId);
        if (panelLayout == null || StringUtil.isBlank(panelLayout.getPanelLayout())) {
          // 如果Layout信息为空，需要创建一个panelLayout信息
          panelLayout =
              serviceFactory.getPanelLayoutService().updateOrCreatePanelLayout(panelLayout, null,
                  spaceId, panelList);
        }

        panelExtVo.setPanelList(panelWithComponentstList);
        panelExtVo.setPanelLayout(panelLayout);
      }
    } catch (Exception e) {
      String errorMsg =
          "get space<" + spaceId + "> panel with components list  error:" + e.getMessage();
      logger.error(errorMsg, e);
      throw new BusinessException(
          BusinessErrorCode.Space.GET_SPACE_PANEL_WITH_COMPONENTS_LIST_ERROR, errorMsg);
    }

    return panelExtVo;
  }
  
  @Override
  public void initDefaultPanelForUserFirstSpace(String spaceId, PtoneUser ptoneUser,
      String localLang) throws BusinessException {
    // 第一次创建空间，根据用户source设置面板
    try {
      serviceFactory.getUserService().saveTempletByUserSource(ptoneUser, localLang, spaceId);
    } catch (Exception e) {
      String errorMsg =
          "save Default panel for user<" + ptoneUser.getPtId() + "> error:" + e.getMessage();
      logger.error(errorMsg, e);
      throw new BusinessException(
          BusinessErrorCode.Panel.INIT_DEFAULT_PANEL_FOR_USER_FIRST_SPACE_ERROR, errorMsg);
    }
  }

  @Override
  @Transactional
  @DistributedLock(name = DistributedLockConstants.DISTRIBUTED_LOCK_PANEL_LAYOUT,
      interval = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_WAIT_INTERVAL,
      timeout = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_TIMEOUT)
  public PanelExtVo copyPanel(PtonePanelInfo newPanel, String srcPanelId, String spaceId)
      throws Exception {
    PanelExtVo panelExtVo = null;
    if (newPanel != null) {
      PanelLayoutNode newPanelLayoutNode = new PanelLayoutNode(PanelLayoutNode.TYPE_PANEL);
      newPanelLayoutNode.setPanelId(newPanel.getPanelId());
      newPanelLayoutNode.setPanelTitle(newPanel.getPanelTitle());
      newPanelLayoutNode.setShareSourceId(newPanel.getShareSourceId());
      panelExtVo =
          serviceFactory.getPanelLayoutService().operatePanelLayout(newPanelLayoutNode, spaceId,
              PanelLayoutNode.OPERATION_ADD);

      // 复制panel
      newPanel = this.copyWholePanelByPanel(newPanel, srcPanelId);
      this.saveComponents(
          createDefaultTimeComponent(newPanel.getPanelId(), newPanel.getCreatorId()));

      // 获取创建后的paneld对象
      String panelId = newPanel.getPanelId();
      Object newPanelInfo = this.getPanelWithComponentsById(panelId, false);
      panelExtVo.setPanel(newPanelInfo);
      
      panelExtVo.setPanel(newPanelInfo); // panelInfo
    }

    return panelExtVo;
  }

  @Override
  @DistributedLock(name = DistributedLockConstants.DISTRIBUTED_LOCK_PANEL_LAYOUT,
      interval = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_WAIT_INTERVAL,
      timeout = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_TIMEOUT)
  public PanelExtVo addPanelFolder(PtonePanelInfo panel, String spaceId) {
    PanelExtVo panelExtVo = null;
    if (panel != null) {
      PanelLayoutNode newPanelLayoutNode = new PanelLayoutNode(PanelLayoutNode.TYPE_CONTAINER);
      newPanelLayoutNode.setContainerId(panel.getPanelId());
      newPanelLayoutNode.setContainerName(panel.getPanelTitle());
      List<List<PanelLayoutNode>> columns = new ArrayList<List<PanelLayoutNode>>();
      columns.add(new ArrayList<PanelLayoutNode>());
      newPanelLayoutNode.setColumns(columns);
      panelExtVo =
          serviceFactory.getPanelLayoutService().operatePanelLayout(newPanelLayoutNode, spaceId,
              PanelLayoutNode.OPERATION_ADD);
      
      newPanelLayoutNode.setPanelId(newPanelLayoutNode.getContainerId());
      newPanelLayoutNode.setPanelTitle(newPanelLayoutNode.getContainerName());
      panelExtVo.setPanel(newPanelLayoutNode);
    }
    return panelExtVo;
  }

  @Override
  @DistributedLock(name = DistributedLockConstants.DISTRIBUTED_LOCK_PANEL_LAYOUT,
      interval = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_WAIT_INTERVAL,
      timeout = PtonePanelLayout.PANEL_LAYOUT_KEY_LOCK_TIMEOUT)
  public PanelExtVo updatePanelFolder(PtonePanelInfo panel, String spaceId) {
    PanelExtVo panelExtVo = null;
    if (panel != null) {
      PanelLayoutNode newPanelLayoutNode = new PanelLayoutNode(PanelLayoutNode.TYPE_CONTAINER);
      newPanelLayoutNode.setContainerId(panel.getPanelId());
      newPanelLayoutNode.setContainerName(panel.getPanelTitle());
      panelExtVo =
          serviceFactory.getPanelLayoutService().operatePanelLayout(newPanelLayoutNode, spaceId,
              PanelLayoutNode.OPERATION_UPDATE);
    }
    return panelExtVo;
  }

  @Override
  public PanelExtVo addPanel(PanelInfoExt panel, String uid, boolean isPanelTemplet)
      throws BusinessException {
    PanelExtVo panelExtVo = null;
    try {
      if (PanelLayoutNode.TYPE_PANEL.equals(panel.getType())) {
        // 为panel设置默认值
        long currentTime = System.currentTimeMillis();
        if(StringUtil.isBlank(panel.getPanelId())){
          panel.setPanelId(UuidUtil.generateUuid());
        }
        panel.setCreatorId(uid);
        panel.setCreateTime(currentTime);
        panel.setStatus(Constants.validate);
        panel.setOrderNumber(0);
        panel.setModel(PanelInfoExt.PANEL_MODEL_READ);
        
        if (isPanelTemplet) {
//          // 新增panelTemplet
//          PtonePanelTemplet templet = new PtonePanelTemplet();
//          BeanUtils.copyProperties(panel, templet);
//          panelExtVo =
//              serviceFactory.getPanelTempletService().addTempletAndLayout(templet,
//                  panel.getSpaceId());
        } else {
          // 新增panel
          PtonePanelInfo panelInfo = new PtonePanelInfo();
          BeanUtils.copyProperties(panel, panelInfo);
          panelExtVo = this.addPanelAndLayout(panelInfo, uid, panel.getSpaceId());
        }
      } else if (PanelLayoutNode.TYPE_CONTAINER.equals(panel.getType())) {
        // 新增panel文件夹
        
        // 为panel文件夹设置默认值
        if(StringUtil.isBlank(panel.getPanelId())){
          panel.setPanelId(UuidUtil.generateUuid());
        }
        
        PtonePanelInfo panelInfo = new PtonePanelInfo();
        BeanUtils.copyProperties(panel, panelInfo);
        panelExtVo = this.addPanelFolder(panelInfo, panelInfo.getSpaceId());
      } else {
        String errorMsg =
            "add Panel error:  type = " + panel.getType() + " is not available | "
                + JSON.toJSONString(panel);
        logger.error(errorMsg);
        throw new BusinessException(BusinessErrorCode.Panel.ADD_PANEL_ERROR, errorMsg);
      }
    } catch (Exception e) {
      String errorMsg = "add Panel error:" + e.getMessage();
      logger.error(errorMsg, e);
      throw new BusinessException(BusinessErrorCode.Panel.ADD_PANEL_ERROR, errorMsg);
    }
    return panelExtVo;
  }

  @Override
  public PanelExtVo copyPanel(PanelInfoExt panel, String srcPanelId,String uid, boolean isPanelTemplet)
      throws BusinessException {
    PanelExtVo panelExtVo = null;
    try {
      
      // 为panel设置默认值
      long currentTime = System.currentTimeMillis();
      panel.setPanelId(UuidUtil.generateUuid());
      panel.setCreatorId(uid);
      panel.setCreateTime(currentTime);
      panel.setStatus(Constants.validate);
      panel.setOrderNumber(0);
      panel.setModel(PanelInfoExt.PANEL_MODEL_READ);
      
      if (isPanelTemplet) {
        // TODO: 目前管理员账号不支持复制panel功能
      } else {
        PtonePanelInfo panelInfo = new PtonePanelInfo();
        try {
          BeanUtils.copyProperties(panel, panelInfo);
          panelExtVo = this.copyPanel(panelInfo, srcPanelId, panel.getSpaceId());
        } catch (Exception e) {
          String errorMsg = "copy Panel error | " + JSON.toJSONString(panel);
          logger.error(errorMsg);
          throw new BusinessException(BusinessErrorCode.Panel.COPY_PANEL_ERROR, errorMsg);
        }
      }
    } catch (Exception e) {
      String errorMsg = "copy Panel error:" + e.getMessage();
      logger.error(errorMsg, e);
      throw new BusinessException(BusinessErrorCode.Panel.COPY_PANEL_ERROR, errorMsg);
    }
    return panelExtVo;
  }

  @Override
  public PanelExtVo updatePanel(PanelInfoExt panelExt, String uid, boolean isPanelTemplet)
      throws BusinessException {

    PanelExtVo panelExtVo = null;
    try {
      PtonePanelInfo panelInfo = new PtonePanelInfo();
      BeanUtils.copyProperties(panelExt, panelInfo);
      if (PanelLayoutNode.TYPE_PANEL.equals(panelExt.getType())) {
        if (!panelExt.isNotUpdatePanelLayout()) { // 判断是否需要更新layout信息
          if (isPanelTemplet) {
//            // 修改panelTemplet
//            PtonePanelTemplet templet = new PtonePanelTemplet();
//            BeanUtils.copyProperties(panelExt, templet);
//            panelExtVo =
//                serviceFactory.getPanelTempletService().updateTempletAndLayout(templet,
//                    panelExt.getSpaceId());
          } else {
            // 修改panel
            panelExtVo = this.updatePanelAndLayout(panelInfo, panelExt.getSpaceId());
          }
        } else {
          if (isPanelTemplet) {
//            PtonePanelTemplet templet = new PtonePanelTemplet();
//            BeanUtils.copyProperties(panelExt, templet);
//            serviceFactory.getPanelTempletService().update(templet);
          } else {
            this.update(panelInfo);
          }
        }
        if (!isPanelTemplet) {
        }
      } else if (PanelLayoutNode.TYPE_CONTAINER.equals(panelExt.getType())) {
        // 修改panel文件夹
        panelExtVo = this.updatePanelFolder(panelInfo, panelInfo.getSpaceId());
      } else {
        String errorMsg =
            "update Panel error:  type = " + panelExt.getType() + " is not available | "
                + JSON.toJSONString(panelExt);
        logger.error(errorMsg);
        throw new BusinessException(BusinessErrorCode.Panel.UPDATE_PANEL_ERROR, errorMsg);
      }
    } catch (Exception e) {
      String errorMsg = "update Panel error:" + e.getMessage();
      logger.error(errorMsg, e);
      throw new BusinessException(BusinessErrorCode.Panel.UPDATE_PANEL_ERROR, errorMsg);
    }
    return panelExtVo;
  }

  @Override
  public PanelExtVo deletePanel(String panelId, PanelInfoExt panelExt, String uid,
      boolean isPanelTemplet) throws BusinessException {
    PanelExtVo panelExtVo = new PanelExtVo();
    try {
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("panelId", new Object[] {panelId});
      if (PanelLayoutNode.TYPE_PANEL.equals(panelExt.getType())) {
        if (isPanelTemplet) {
//          PtonePanelTemplet panel = serviceFactory.getPanelTempletService().getByWhere(paramMap);
//          panel.setStatus(Constants.inValidate);
//          panelExtVo =
//              serviceFactory.getPanelTempletService().deleteTempletAndLayout(panelId,
//                  panel.getSpaceId());
        } else {
          PtonePanelInfo panel = this.getByWhere(paramMap);
          panel.setStatus(Constants.inValidate);
          panelExtVo = this.deletePanelAndLayout(panelId, panel.getSpaceId(), true);
        }
      } else if (PanelLayoutNode.TYPE_CONTAINER.equals(panelExt.getType())) {

        //删除文件夹索引用，需删除前提前查出
        PtonePanelLayout dbPanelLayout = serviceFactory.getPanelLayoutService().getBySpaceId(panelExt.getSpaceId());
        // 删除panel文件夹
        panelExtVo = this.deletePanelFolder(panelExt, isPanelTemplet);

        PanelInfoExt delPanelExt = new PanelInfoExt();
        BeanUtils.copyProperties(panelExt,delPanelExt);
        delPanelExt.setPtonePanelLayout(dbPanelLayout);
      } else {
        String errorMsg =
            "delete Panel error:  type = " + panelExt.getType() + " is not available | "
                + JSON.toJSONString(panelExt);
        logger.error(errorMsg);
        throw new BusinessException(BusinessErrorCode.Panel.DELETE_PANEL_ERROR, errorMsg);
      }
      logger.info("uid:" + uid + " | panelId:" + panelId + " del panel success.");
    } catch (Exception e) {
      String errorMsg = "delete Panel<" + panelId + "> error:" + e.getMessage();
      logger.error(errorMsg, e);
      throw new BusinessException(BusinessErrorCode.Panel.DELETE_PANEL_ERROR, errorMsg);
    }
    return panelExtVo;
  }


  /**
   * 删除panelFolder
   * @date: 2016年12月5日
   * @author peng.xu
   */
  private PanelExtVo deletePanelFolder(PanelInfoExt panelExt, boolean isPanelTemplet) {
    PanelExtVo panelExtVo = null;
    try {
      String[] pids = panelExt.getPids();
      String panelFolderId = panelExt.getPanelId();
      if (isPanelTemplet) {
//        panelExtVo =
//            serviceFactory.getPanelTempletService().batchDeleteTempletsAndLayout(panelFolderId,
//                pids, panelExt.getSpaceId(), true);
      } else {
        boolean isHaveNot = false;// 是否有不是当前用户拥有的Panel
        if (null != pids && ArrayUtils.isNotEmpty(pids)) {
          List<PtonePanelInfo> panels = this.findByWhere("panelId", "in", pids);
          for (PtonePanelInfo panel : panels) {
            if (panel == null) {
              continue;
            }
            if (!panelExt.getSpaceId().equalsIgnoreCase(panel.getSpaceId())) {
              isHaveNot = true;
              break;
            }
          }
        }
        if (isHaveNot) {
          String errorMsg =
              "del Panel Folder failed. Folder does not belong to your panel. | "
                  + JSON.toJSONString(panelExt);
          logger.error(errorMsg);
          throw new BusinessException(BusinessErrorCode.Panel.DELETE_PANEL_ERROR, errorMsg);
        } else {
          panelExtVo =
              this.batchDeletePanelsAndLayout(panelFolderId, pids, panelExt.getSpaceId(), true);
        }
      }
    } catch (Exception e) {
      String errorMsg = "delete Folder error:" + e.getMessage();
      logger.error(errorMsg, e);
      throw new BusinessException(BusinessErrorCode.Panel.DELETE_PANEL_ERROR, errorMsg);
    }
    return panelExtVo;
  }

  @Override
  public boolean sharePanelVerifyPassword(SharePanelVerifyVo sharePanelVerifyVo)
      throws BusinessException {
    boolean validate = true;
    try {
      if (sharePanelVerifyVo == null) {
        validate = false;
      } else {
        String dashboardId = sharePanelVerifyVo.getDashboardId();
        String password = sharePanelVerifyVo.getPassword();
        if (StringUtil.isNotBlank(dashboardId) && StringUtil.isNotBlank(password)) {
          PtonePanelInfo panel = this.get(dashboardId);
          if (null == panel) {// 无此panel
            validate = false;
          } else {
            String sharePassword = panel.getSharePassword();
            if (StringUtil.isBlank(sharePassword)) {// 密码为空串或null的时候，用户已取消panel密码认证
              validate = true;
            } else {
              if (sharePassword.equals(password)) {// 验证密码通过
                validate = true;
              } else {// 验证密码未通过
                validate = false;
              }
            }
          }
        } else {
          validate = false;
        }
      }
    } catch (Exception e) {
      String errorMsg = "Share Panel Verify Password error: " + e.getMessage();
      logger.error(errorMsg, e);
      throw new BusinessException(BusinessErrorCode.Panel.SHARE_PANEL_VERIFY_PASSWORD_ERROR,
          errorMsg);
    }
    return validate;
  }

  @Override
  public Object getPanelWithComponentsById(String panelId, boolean isPanelTemplet)
      throws BusinessException {
    Object result = null;
    try {
      if (isPanelTemplet) {
//        PtonePanelTemplet panelTemplet = serviceFactory.getPanelTempletService().get(panelId);
//        // 设置spaceName
//        String spaceId = panelTemplet.getSpaceId();
//        PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
//        panelTemplet.setSpaceName(spaceInfo.getName());
//
//        result = panelTemplet;
      } else {
        PtonePanelInfo panel = this.get(panelId);

        // 设置spaceName
        String spaceId = panel.getSpaceId();
        PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
        panel.setSpaceName(spaceInfo.getName());

        Map<String, Object[]> paramMap = new HashMap<>();
        paramMap.put("panelId", new Object[] {panelId});
        paramMap.put("itemId", new Object[] {PanelGlobalComponent.COMPONENT_ITEM_ID_GLOBAL_TIME});// 时间组件id=16
        PanelGlobalComponent dbComponent = this.getComponents(paramMap);
        PanelInfoWithComponents panelInfoWithComponents = new PanelInfoWithComponents();
        BeanUtils.copyProperties(panel, panelInfoWithComponents);
        if (null != dbComponent) {
          Map<String, PanelGlobalComponent> componentMap = new TreeMap<>();
          componentMap.put(dbComponent.getCode(), dbComponent);
          panelInfoWithComponents.setComponents(componentMap);
        }
        result = panelInfoWithComponents;
      }
    } catch (Exception e) {
      String errorMsg = "get panel<" + panelId + "> with Components error:" + e.getMessage();
      logger.error(errorMsg, e);
      throw new BusinessException(BusinessErrorCode.Panel.GET_PANEL_WITH_COMPONENTS_ERROR, errorMsg);
    }
    return result;
  }

  @Override
  public WidgetListVo findPanelWidgetListWithLayout(String panelId, String uid, String password,
      boolean isPanelTemplet, boolean isMobile) throws BusinessException {
    String uiPid = panelId;
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("panelId", new Object[] {panelId});
    PtonePanelInfo panel = this.getByWhere(paramMap);
    PtonePanelInfo sharePanel = null;
    if (null != panel && StringUtil.hasText(panel.getShareSourceId())) {
      panelId = panel.getShareSourceId();
      paramMap = new HashMap<>();
      paramMap.put("panelId", new Object[] {panelId});
      // 分享来源panel
      sharePanel = this.getByWhere(paramMap);
      // 增加分享panel所在空间已删除的判断
      String shareSpaceId = sharePanel.getSpaceId();
      PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(shareSpaceId);
      if (spaceInfo == null) {
        throw new BusinessException(BusinessErrorCode.Panel.PANEL_SHARE_SPACE_DELETED_FAILED);
      }
      if (sharePanel.getStatus().equals(Constants.inValidate)) {// 分享panel已删除
        throw new BusinessException(BusinessErrorCode.Panel.PANEL_SHARE_DELETED_FAILED);
      }
      if (sharePanel.getShareUrl().equals(Constants.inValidate)) {// 分享panel已关闭
        throw new BusinessException(BusinessErrorCode.Panel.PANEL_SHARE_CLOSED_FAILED);
      }
      String sharePassword = sharePanel.getSharePassword();
      if (StringUtil.isNotBlank(sharePassword)) {
        if (StringUtil.isBlank(password) || !sharePassword.equals(password)) {
          throw new BusinessException(BusinessErrorCode.Panel.PANEL_SHARE_PASSWORD_ERROR_FAILED);
        }
      }
    }
    List<AcceptWidget> widgetsList = serviceFactory.getWidgetService().findWidget(panelId);
    // 把原panelId塞回去
    if (!uiPid.equals(panelId)) {
      for (AcceptWidget widget : widgetsList) {
        widget.setPanelId(uiPid);
      }
    }
//    // 移动端需要对widget进行排序
//    if (isMobile) {
//      widgetsList = WidgetUtil.sortAcceptWidgetByWidgetSortKey(panel.getLayout(), widgetsList);
//    }

    WidgetListVo widgetListVo = new WidgetListVo();
    if (isPanelTemplet) {
//      paramMap = new HashMap<>();
//      paramMap.put("panelId", new Object[] {panelId});
//      PtonePanelTemplet panelTemplet = serviceFactory.getPanelTempletService().getByWhere(paramMap);
//      widgetListVo.setLayout(panelTemplet.getLayout());
    } else {
      if (StringUtil.isNotBlank(panel.getShareSourceId())) {
        paramMap.put("panelId", new Object[] {panel.getShareSourceId()});
        panel = this.getByWhere(paramMap);
      }
      widgetListVo.setLayout(panel.getLayout());
    }
    widgetListVo.setWidgetList(widgetsList);
    return widgetListVo;
  }
  
  
  @Transactional
  public PtonePanelInfo copyWholePanelByPanel(PtonePanelInfo newPanel, String srcPanelId)
      throws Exception {

    // 创建新的panel
    newPanel.setStatus(Constants.validate);
    newPanel.setCreateTime(System.currentTimeMillis());
    newPanel.setIsByTemplet(Constants.inValidate);
    newPanel.setIsByDefault(Constants.inValidate);
    newPanel.setSourceType(SourceType.Panel.USER_CREATED);

    // 注册时没有spaceId
    if (StringUtil.hasText(newPanel.getSpaceId())) {
      PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(newPanel.getSpaceId());
      newPanel.setSpaceName(spaceInfo.getName());
    }

    // 设置layout信息
    PtonePanelInfo srcPanel = serviceFactory.getPanelService().get(srcPanelId);
    if (srcPanel != null) {
      newPanel.setLayout(srcPanel.getLayout());
      // copy widgets
      if (StringUtil.isNotBlank(newPanel.getLayout())) {
        this.copyWholePanelWidgets(newPanel, srcPanelId, null, false, false);
      }
    } else {
      newPanel.setLayout("");
    }

    panelDao.save(newPanel);
    return newPanel;
  }
  
  /**
   * 复制panel下的所有widget
   * @author peng.xu
   * @date 2016年11月29日 下午5:45:19
   * @param newPanel
   * @param templetPanelId
   * @param localLang
   * @param isTemplet true == 是模板 || false == 是复制panel
   * @param isDefault true == 预制Panel || false == 用户通过panel模板创建
   * @throws Exception
   */
  private void copyWholePanelWidgets(PtonePanelInfo newPanel, String templetPanelId,
      String localLang, boolean isTemplet, boolean isDefault) throws Exception {
    if (newPanel == null || StringUtil.isBlank(templetPanelId)) {
      return;
    }

    String spaceId = newPanel.getSpaceId();
    String uid = newPanel.getCreatorId();
    long createTime = newPanel.getCreateTime();
    String newLayout = URLDecoder.decode(newPanel.getLayout(), "utf8");

    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("panelId", new Object[] {templetPanelId});
    List<PtonePanelWidget> panelWidgets = panelWidgetDao.findByWhere(paramMap);
    if (CollectionUtil.isEmpty(panelWidgets)) {
      return;
    }

    List<String> templetIdList = new ArrayList<String>();
    List<PtoneWidgetInfo> templetWidgetList = new ArrayList<PtoneWidgetInfo>();
    List<PtoneWidgetChartSetting> chartSettingTempletList =
        new ArrayList<PtoneWidgetChartSetting>();
    List<PtoneWidgetInfoExtend> widgetExtendTempletList = new ArrayList<PtoneWidgetInfoExtend>();
    List<GaWidgetInfo> gaWidgetTempletList = new ArrayList<GaWidgetInfo>();
    if (CollectionUtil.isNotEmpty(panelWidgets)) {
      for (PtonePanelWidget panelWidgetRelation : panelWidgets) {
        String templetWidgetId = panelWidgetRelation.getWidgetId();
        templetIdList.add(templetWidgetId);
      }
      if (CollectionUtil.isNotEmpty(templetIdList)) {
        paramMap = new HashMap<>();
        paramMap.put("widgetId", templetIdList.toArray());
        paramMap.put("status", new Object[] {Constants.validate});
        templetWidgetList = widgetService.findByWhere(paramMap);

        paramMap = new HashMap<>();
        paramMap.put("widgetId", templetIdList.toArray());
        chartSettingTempletList = widgetChartSettingService.findByWhere(paramMap);
        widgetExtendTempletList = widgetExtendService.findByWhere(paramMap);
        gaWidgetTempletList = gaWidgetDao.findByWhere(paramMap);
      }
    }

    if (CollectionUtil.isNotEmpty(templetWidgetList)) {
      for (PtoneWidgetInfo templetWidgetInfo : templetWidgetList) {
        if (null != templetWidgetInfo) {
          String templetWidgetId = templetWidgetInfo.getWidgetId();
          // 设置baseWidget的基本属性
          PtoneWidgetInfo baseWidget = new PtoneWidgetInfo();
          BeanUtils.copyProperties(templetWidgetInfo, baseWidget);
          String newWidgetId = UUID.randomUUID().toString();
          widgetService.fixWidgetInfoByTemplet(baseWidget, spaceId, newPanel.getPanelId(),
              newWidgetId, uid, localLang, createTime, isTemplet);
          if (isTemplet) {
            // 如果是模板，则需要进一步区分是预制模板，还是手动根据模板创建
            if (isDefault) {
              // 预制模板
              baseWidget.setSourceType(SourceType.Widget.DEFAULT_TEMPLET);
            } else {
              // 手动根据模板创建
              baseWidget.setSourceType(SourceType.Widget.PANEL_TEMPLET);
            }
          } else {
            // 不是模板，是用户手动复制的
            baseWidget.setSourceType(SourceType.Widget.USER_CREATED);
          }
          widgetService.saveBaseWidget(baseWidget);

          // 替换panel的layout信息中的widgetId
          newLayout = newLayout.replace(templetWidgetInfo.getWidgetId(), newWidgetId);

          // 保存panel和widget关联关系
          widgetService.savePanelWidgetRelation(newPanel.getPanelId(), newWidgetId);

          // 根据模板保存widgetChartSetting信息
          widgetService.saveWidgetChartSettingByTemplet(templetWidgetId, newWidgetId,
              chartSettingTempletList);

          String widgetType = templetWidgetInfo.getWidgetType();
          if (PtoneWidgetInfo.WIDGET_TYPE_TOOL.equals(widgetType)) { // tool类型图表处理 or demo数据
            // 根据模板保存widgetExtend信息
            widgetService.saveWidgetExtendByTemplet(templetWidgetId, newWidgetId,
                widgetExtendTempletList);
          } else if (PtoneWidgetInfo.WIDGET_TYPE_CUSTOM.equals(widgetType)) {
            // TODO： 此处自定义widget的处理， 子widget的处理，layout信息的处理

          } else { // chart类型图表处理
            if (templetWidgetInfo.getIsDemo() == Constants.validateInt) {
              // 根据模板保存widgetExtend信息
              widgetService.saveWidgetExtendByTemplet(templetWidgetId, newWidgetId,
                  widgetExtendTempletList);
            }

            // 根据模板保存variableInfo信息
            PtoneVariableInfo newVariableInfo =
                widgetService.saveVariableInfoByTemplet(templetWidgetId, baseWidget);
            String newVariableId = newVariableInfo.getVariableId();

            // 保存widget和variable的关联关系
            widgetService.saveWidgetVariableRelation(newWidgetId, newVariableId);

            // 保存GaWidgetInfo信息
            GaWidgetInfo newGaWidgetInfo =
                widgetService.buildGaWidgetInfo(templetWidgetId, baseWidget, gaWidgetTempletList,
                    newWidgetId, newVariableId,
                    UserCompoundMetricsDimension.SOURCE_TYPE_PANEL_TEMPLATE, isTemplet);
            gaWidgetDao.save(newGaWidgetInfo);
          }
        }
      }
      if (null != newLayout) {
        newPanel.setLayout(URLEncoder.encode(newLayout.replaceAll("\n|\\s", ""), "utf8"));
      }
    }
  }
}
