package com.sizzler.controller.rest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.ptmind.common.utils.CollectionUtil;
import com.ptmind.common.utils.StringUtil;
import com.sizzler.common.Constants.JsonViewConstants;
import com.sizzler.common.MediaType;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.domain.panel.PanelGlobalComponent;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.panel.PtonePanelLayout;
import com.sizzler.domain.panel.dto.PanelInfoExt;
import com.sizzler.domain.panel.dto.PanelInfoWithComponents;
import com.sizzler.domain.panel.dto.PanelLayoutNode;
import com.sizzler.domain.panel.vo.PanelExtVo;
import com.sizzler.domain.pmission.PtoneSysRole;
import com.sizzler.domain.session.dto.PtoneSession;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.user.PtoneUserBasicSetting;
import com.sizzler.domain.widget.dto.AcceptWidget;
import com.sizzler.system.Constants;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.annotation.MethodRemark;

@Controller
@Scope("prototype")
@RequestMapping("/panels")
public class PanelController {

  private Logger logger = LoggerFactory.getLogger(PanelController.class);

  @Autowired
  private ServiceFactory serviceFactory;

  @RequestMapping(value = "single/{panelId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getSinglePanel(@RequestParam(value = "sid", required = false) String sid,
      @PathVariable("panelId") String panelId, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("panelId", new Object[] {panelId});
      PtonePanelInfo panel = serviceFactory.getPanelService().getByWhere(paramMap);
      jsonView.successPack(panel);
    } catch (Exception e) {
      jsonView.errorPack("getSinglePanel error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "panel/{spaceId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getPanel(@RequestParam(value = "sid", required = false) String sid,
      @RequestParam(value = "isCreateSpace", required = false) String isCreateSpace, @RequestParam(
          value = "localLang", required = false) String localLang,
      @PathVariable("spaceId") String spaceId, HttpServletRequest request) {
    Map<String, Object[]> paramMap = new HashMap<>();
    // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
    // PtoneUser loginPtoneUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      long startTime = System.currentTimeMillis();
      PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
      List<PtoneSysRole> sysRoles = session.getSysRoles();
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      // if (loginPtoneUser.getAccess().contains(Constants.managerAccess)) {
//      if (PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER)) {
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
//        PtonePanelLayo//        PtonePanelLayout panelLayout = serviceFactory.getPanelLayoutService().getByManager();
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        resultMap.put("resultList", templetList);
//        resultMap.put("resultLayout", panelLayout);
//        jsonView.successPack(resultMap);ut panelLayout = serviceFactory.getPanelLayoutService().getByManager();
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        resultMap.put("resultList", templetList);
//        resultMap.put("resultLayout", panelLayout);
//        jsonView.successPack(resultMap);
//      } else {

        // 第一次创建空间，根据用户source设置面板
        if (StringUtil.hasText(isCreateSpace) && isCreateSpace.equals(Constants.inValidate)) {
          try {
            serviceFactory.getUserService().saveTempletByUserSource(loginPtoneUser, localLang,
                spaceId);
          } catch (UnsupportedEncodingException e) {
            logger.error("save Default Templet error.", e);
          }
        }

        List<PanelInfoWithComponents> resultList = new ArrayList<>();
        List<PtonePanelInfo> panelList =
            serviceFactory.getPanelService().getPanelListBySpaceId(spaceId);

        List<String> panelIdList = new ArrayList<String>();
        Set<String> shareSrcPanelIdList = new HashSet<String>();
        for (PtonePanelInfo panel : panelList) {
          panelIdList.add(panel.getPanelId());
          if (StringUtil.hasText(panel.getShareSourceId())) {
            shareSrcPanelIdList.add(panel.getShareSourceId());
          }
        }
        List<PanelGlobalComponent> componentList =
            serviceFactory.getPanelService().findGlobalComponents(panelIdList);

        Map<String, PtonePanelInfo> shareSrcPanelMap = new HashMap<String, PtonePanelInfo>();
        Map<String, PtoneSpaceInfo> shareSpaceMap = new HashMap<String, PtoneSpaceInfo>();
        List<PtonePanelInfo> shareSrcPanelList = new ArrayList<PtonePanelInfo>();
        if (shareSrcPanelIdList != null && shareSrcPanelIdList.size() > 0) {
          paramMap = new HashMap<>();
          paramMap.put("panelId", shareSrcPanelIdList.toArray());
          shareSrcPanelList = serviceFactory.getPanelService().findByWhere(paramMap);

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
          resultList.add(panelInfoWithComponents);
        }
        // 增加ptone_panel_layout数据信息
        PtonePanelLayout panelLayout = serviceFactory.getPanelLayoutService().getBySpaceId(spaceId);
        if (panelLayout == null || StringUtil.isBlank(panelLayout.getPanelLayout())) {
          // 需要创建一个panelLayout内容
          panelLayout =
              serviceFactory.getPanelLayoutService().updateOrCreatePanelLayout(panelLayout,
                  loginPtoneUser.getPtId(), spaceId, panelList);
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("resultList", resultList);
        resultMap.put("resultLayout", panelLayout);
        jsonView.successPack(resultMap);
//      }
      long endTime = System.currentTimeMillis();
      Map<String, Object> operateInfo = new HashMap<>();
      operateInfo.put(OpreateConstants.EXECUTE_TIME, endTime - startTime);
      LogMessage logMessage = new LogMessage();
      if (loginPtoneUser != null) {
        operateInfo.put(OpreateConstants.USER_EMAIL, loginPtoneUser.getUserEmail());
        logMessage.setUid(loginPtoneUser.getPtId());
      }
      logMessage.setOperate(OpreateConstants.Panel.GET_LIST);
      logMessage.setOperateInfo(operateInfo);
      logger.info(logMessage.toString());
    } catch (Exception e) {
      jsonView.errorPack(" query Panel error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "templet/tags/{pid}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getTempletTags(@PathVariable("pid") String pid, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      List<PtoneTagPanelTemplate> tags =
//          serviceFactory.getPanelTempletService().queryTagsByPanelId(pid);
//      jsonView.successPack(tags);
//    } catch (Exception e) {
//      jsonView.errorPack("query  Panel templet tags error.", e);
//    }
    return jsonView;
  }

  /**
   * 前端未使用
   */
  // @RequestMapping(value = "updatePanels", method = RequestMethod.POST,
  // produces = MediaType.APPLICATION_JSON)
  // @ResponseBody
  // @Transactional
  // @Deprecated
  // public JsonView updatePanelList(@RequestBody List<PtonePanelInfo> panels,
  // HttpServletRequest request, @RequestParam(value = "sid", required = false) String sid) {
  // JsonView jsonView = JsonViewFactory.createJsonView();
  // try {
  // // HttpSession session = serviceFactory.getSessionContext().getSession(sid, request);
  // // PtoneUser loginPtoneUser = (PtoneUser) session.getAttribute(Constants.Current_Ptone_User);
  // PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
  // PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
  // List<PtoneSysRole> sysRoles = session.getSysRoles();
  //
  // for (PtonePanelInfo panel : panels) {
  // // if (loginPtoneUser.getAccess().contains(Constants.managerAccess)) {
  // PtonePanelTemplet templet = new PtonePanelTemplet();
  // if (PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER)) {
  // BeanUtils.copyProperties(panel, templet);
  // serviceFactory.getPanelTempletService().update(templet);
  // } else {
  // serviceFactory.getPanelService().update(panel);
  // }
  // }
  // jsonView.messagePack("update panel list success.");
  // } catch (Exception e) {
  // jsonView.errorPack(" update panel list error.", e);
  // }
  // return jsonView;
  // }



  @RequestMapping(value = "templet/defaultPublished", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getDefaultPublishedPanelTemplet() {
    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      List<PtonePanelTemplet> panelList =
//          serviceFactory.getPanelTempletService().findDefaultPublishedPanelTemplet();
//      jsonView.successPack(panelList);
//    } catch (Exception e) {
//      jsonView.errorPack(" query default published panel templet error.", e);
//    }
    return jsonView;
  }


  @RequestMapping(value = "templet/published", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getPublishedPanelTemplet(HttpServletRequest request, @RequestParam(value = "sid",
      required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
//      List<PanelTempletWithDsInfo> panelList =
//          serviceFactory.getPanelTempletService().findPublishedPanelTemplet(loginPtoneUser.getPtId());
//      jsonView.successPack(panelList);
//    } catch (Exception e) {
//      jsonView.errorPack(" query published panel templet error.", e);
//    }
    return jsonView;
  }

  @RequestMapping(value = "templet/unpublished", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getUnpublishedPanelTemplet() {
    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      List<PtonePanelTemplet> panelList =
//          serviceFactory.getPanelTempletService().findUnpublishedPanelTemplet();
//      jsonView.successPack(panelList);
//    } catch (Exception e) {
//      jsonView.errorPack(" query unpublished panel templet error.", e);
//    }
    return jsonView;
  }

  @RequestMapping(value = "getPanel/{panelId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getPanelById(@PathVariable("panelId") String panelId,
      @RequestParam(value = "sid", required = false) String sid,
      @RequestParam(value = "accessToken", required = false) String accessToken) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      boolean isAdmin = false;
      PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
      
//      // 分享页面会传递accessToken， 如果是分享页面则不需要校验是否admin账号（目前admin不支持分享）
//      if(session != null && StringUtil.isBlank(accessToken)){
//        List<PtoneSysRole> sysRoles = session.getSysRoles();
//        isAdmin = PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER);
//      }
      
      if (isAdmin) {
//        PtonePanelTemplet panelTemplet = serviceFactory.getPanelTempletService().get(panelId);
//        // 设置spaceName
//        String spaceId = panelTemplet.getSpaceId();
//        PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
//        panelTemplet.setSpaceName(spaceInfo.getName());
//
//        jsonView.successPack(panelTemplet);
      } else {
        PtonePanelInfo panel = serviceFactory.getPanelService().get(panelId);

        // 设置spaceName
        String spaceId = panel.getSpaceId();
        PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(spaceId);
        panel.setSpaceName(spaceInfo.getName());

        Map<String, Object[]> paramMap = new HashMap<>();
        paramMap.put("panelId", new Object[] {panelId});
        paramMap.put("itemId", new Object[] {PanelGlobalComponent.COMPONENT_ITEM_ID_GLOBAL_TIME});// 时间组件id=16
        PanelGlobalComponent dbComponent = serviceFactory.getPanelService().getComponents(paramMap);
        PanelInfoWithComponents panelInfoWithComponents = new PanelInfoWithComponents();
        BeanUtils.copyProperties(panel, panelInfoWithComponents);
        if (null != dbComponent) {
          Map<String, PanelGlobalComponent> componentMap = new TreeMap<>();
          componentMap.put(dbComponent.getCode(), dbComponent);
          panelInfoWithComponents.setComponents(componentMap);
        }
        jsonView.successPack(panelInfoWithComponents);
      }
    } catch (Exception e) {
      jsonView.errorPack(" get panel<" + panelId + "> error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "component/apply", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView componentApply(@RequestParam(value = "sid", required = false) String sid,
      @RequestBody PanelGlobalComponent component, HttpServletRequest request) {
    Map<String, Object[]> paramMap = new HashMap<>();
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      component.setUid(loginPtoneUser.getPtId());
      paramMap.put("panelId", new Object[] {component.getPanelId()});
      paramMap.put("itemId", new Object[] {component.getItemId()});
      // paramMap.put("uid", new Object[]{component.getUid()});
      PanelGlobalComponent dbComponent = serviceFactory.getPanelService().getComponents(paramMap);
      if (null == dbComponent) {
        serviceFactory.getPanelService().saveComponents(component);
      } else {
        component.setId(dbComponent.getId());
        serviceFactory.getPanelService().updateComponents(component);
      }
      jsonView.messagePack(" component apply success.");
    } catch (Exception e) {
      jsonView.errorPack(" component apply error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "component/cancel/{pid}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView componentCancel(@RequestParam(value = "sid", required = false) String sid,
      @PathVariable("pid") String pid, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      serviceFactory.getPanelService().cancelPanelComponent(pid);
      jsonView.messagePack(" cancel apply success.");
    } catch (Exception e) {
      jsonView.errorPack(" cancel apply error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "share/add/{pid}/{spaceId}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView addSharePanel(@RequestParam(value = "sid", required = false) String sid,
      @PathVariable("pid") String pid, @PathVariable("spaceId") String spaceId,
      @RequestBody PanelGlobalComponent component, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      String result =
          serviceFactory.getPanelService().addSharePanel(pid, loginPtoneUser.getPtId(), spaceId,
              component);
      jsonView.messagePack(result);
    } catch (Exception e) {
      logger.error(" add Share Panel error.", e);
      jsonView.errorPack(" add Share Panel error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "share/exists/{pid}/{spaceId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView existsSharePanel(@RequestParam(value = "sid", required = false) String sid,
      @PathVariable("pid") String pid, @PathVariable("spaceId") String spaceId,
      HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      boolean isExists = serviceFactory.getPanelService().isExistsPanelInSpace(pid, spaceId);
      if (isExists) {
        jsonView.messagePack("inValidate_status");
      } else {
        jsonView.messagePack("validate_status");
      }
    } catch (Exception e) {
      jsonView.errorPack(" exists Share Panel error.", e);
    }
    return jsonView;
  }

  @Deprecated
  @RequestMapping(value = "layout/update", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView layoutUpdate(@RequestBody PtonePanelLayout panelLayout, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      jsonView.successPack(serviceFactory.getPanelLayoutService().updateDataVersion(panelLayout));
    } catch (Exception e) {
      jsonView.errorPack(" set Panel layout error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "layout/version/{dataVersion}/{spaceId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @Deprecated
  public JsonView layoutVersion(@PathVariable("dataVersion") Integer dataVersion,
      @PathVariable("spaceId") String spaceId, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtonePanelLayout panelLayout = serviceFactory.getPanelLayoutService().getBySpaceId(spaceId);
      if (null == panelLayout) {
        jsonView.failedPack(" Panel layout empty");
      } else {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (panelLayout.getDataVersion() - dataVersion != 0) {
          // 版本已变更，返回最新版本号，并返回最新panelLayout
          resultMap.put("resultList",
              serviceFactory.getPanelService().getPanelListBySpaceId(spaceId));
          resultMap.put("resultLayout", panelLayout);
          jsonView.successPack(resultMap);
        } else {
          panelLayout.setPanelLayout("");
          resultMap.put("resultList", null);
          resultMap.put("resultLayout", panelLayout);
          jsonView.successPack(resultMap);
        }
      }
    } catch (Exception e) {
      jsonView.errorPack(" check Panel layout version error.", e);
    }
    return jsonView;
  }

  // /////////////////////////////////////////////////////////////

//  /**
//   * 新增panel、panelTemplet、panelFolder
//   * @date: 2016年12月5日
//   * @author peng.xu
//   */
//  @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
//  @ResponseBody
//  @MethodRemark(remark = OpreateConstants.Panel.ADD_PANEL,
//      domain = OpreateConstants.BusinessDomain.PANEL)
//  public JsonView addPanel(@RequestBody PanelInfoExt panel, HttpServletRequest request,
//      @RequestParam(value = "sid", required = true) String sid) {
//    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
//      PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
//      List<PtoneSysRole> sysRoles = session.getSysRoles();
//      PanelExtVo panelExtVo = null;
//      if (PanelLayoutNode.TYPE_PANEL.equals(panel.getType())) {
//        if (PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER)) {
//          // 新增panelTemplet
//          PtonePanelTemplet templet = new PtonePanelTemplet();
//          BeanUtils.copyProperties(panel, templet);
//          panelExtVo =
//              serviceFactory.getPanelTempletService().addTempletAndLayout(templet,
//                  panel.getSpaceId());
//        } else {
//          // 新增panel
//          PtonePanelInfo panelInfo = new PtonePanelInfo();
//          BeanUtils.copyProperties(panel, panelInfo);
//          panelExtVo =
//              serviceFactory.getPanelService().addPanelAndLayout(panelInfo,
//                  loginPtoneUser.getPtId(), panel.getSpaceId());
//        }
//        jsonView = this.buildPanelLayoutJsonView(panelExtVo);
//      } else if (PanelLayoutNode.TYPE_CONTAINER.equals(panel.getType())) {
//        // 新增panel文件夹
//        PtonePanelInfo panelInfo = new PtonePanelInfo();
//        BeanUtils.copyProperties(panel, panelInfo);
//        panelExtVo =
//            serviceFactory.getPanelService().addPanelFolder(panelInfo, panelInfo.getSpaceId());
//        jsonView = this.buildPanelLayoutJsonView(panelExtVo);
//      } else {
//        logger.error(JSON.toJSONString(panel) + " | add Panel error:  type = " + panel.getType()
//            + " is not available");
//        jsonView.errorPack(JSON.toJSONString(panel) + " | add Panel error: type  = "
//            + panel.getType() + " is not available");
//      }
//    } catch (Exception e) {
//      logger.error(JSON.toJSONString(panel) + " | add Panel error.", e);
//      jsonView.errorPack(JSON.toJSONString(panel) + " | add Panel error.", e);
//    }
//    return jsonView;
//  }

  /**
   * 复制panel
   * @date: 2016年12月5日
   * @author peng.xu
   */
  @RequestMapping(value = "{srcPanelId}/copy", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark = OpreateConstants.Panel.COPY_PANEL,
      domain = OpreateConstants.BusinessDomain.PANEL)
  public JsonView copyPanel(@RequestBody PanelInfoExt panelExt, HttpServletRequest request,
      @PathVariable("srcPanelId") String srcPanelId,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
      List<PtoneSysRole> sysRoles = session.getSysRoles();
      PanelExtVo panelExtVo = null;

//      if (PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER)) {
//        // TODO: 目前管理员账号不支持复制panel功能
//      } else {
        PtonePanelInfo panelInfo = new PtonePanelInfo();
        BeanUtils.copyProperties(panelExt, panelInfo);
        panelExtVo =
            serviceFactory.getPanelService()
                .copyPanel(panelInfo, srcPanelId, panelExt.getSpaceId());
//      }
      jsonView = this.buildPanelLayoutJsonView(panelExtVo);
    } catch (Exception e) {
      logger.error(JSON.toJSONString(panelExt) + " | copy Panel error.", e);
      jsonView.errorPack(JSON.toJSONString(panelExt) + " | copy Panel error.", e);
    }
    return jsonView;
  }

  /**
   * 根据模板创建panel
   * @date: 2016年12月5日
   * @author peng.xu
   */
  @RequestMapping(value = "create-by-templet", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView createByTemplet(@RequestBody PanelInfoExt panelExt, HttpServletRequest request,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);

      PanelExtVo panelExtVo =
          serviceFactory.getPanelService().addPanelByTemplet(panelExt.getTempletId(), panelExt.getSpaceId(),
              loginPtoneUser.getPtId());
      
      PtonePanelInfo newPanel = (PtonePanelInfo) panelExtVo.getPanel();
      List<AcceptWidget> widgetsList = null;
      if (newPanel != null) {
        widgetsList = serviceFactory.getWidgetService().findWidget(newPanel.getPanelId());
      }
      
      // 兼容以前代码： 将panelExtVo转为resultMap
      Map<String, Object> resultMap = new HashMap<String, Object>();
      resultMap.put("panel", panelExtVo.getPanel());
      resultMap.put("resultList", panelExtVo.getPanelList());
      resultMap.put("resultLayout", panelExtVo.getPanelLayout());
      resultMap.put("status", panelExtVo.getStatus());
      
      resultMap.put("widgetsList", widgetsList);
      jsonView.successPack(resultMap);
    } catch (Exception e) {
      logger.error(panelExt.getTempletId() + " | create Panel by templet  error.", e);
      jsonView.errorPack(panelExt.getTempletId() + " | create Panel by templet error.", e);
    }
    return jsonView;
  }

//  /**
//   * 修改panel、panelTemplet、panelFolder
//   * @date: 2016年12月5日
//   * @author peng.xu
//   */
//  @RequestMapping(value = "{panelId}", method = RequestMethod.PUT,
//      produces = MediaType.APPLICATION_JSON)
//  @MethodRemark(remark = OpreateConstants.Panel.UPDATE_PANEL,
//      domain = OpreateConstants.BusinessDomain.PANEL)
//  @ResponseBody
//  public JsonView updatePanel(@RequestBody PanelInfoExt panelExt, HttpServletRequest request,
//      @PathVariable("panelId") String panelId,
//      @RequestParam(value = "sid", required = false) String sid) {
//    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
//      List<PtoneSysRole> sysRoles = session.getSysRoles();
//      PanelExtVo panelExtVo = null;
//
//      if (PanelLayoutNode.TYPE_PANEL.equals(panelExt.getType())) {
//        if (!panelExt.isNotUpdatePanelLayout()) { // 判断是否需要更新layout信息
//          if (PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER)) {
//            // 修改panelTemplet
//            PtonePanelTemplet templet = new PtonePanelTemplet();
//            BeanUtils.copyProperties(panelExt, templet);
//            panelExtVo =
//                serviceFactory.getPanelTempletService().updateTempletAndLayout(templet,
//                    panelExt.getSpaceId());
//          } else {
//            // 修改panel
//            PtonePanelInfo panelInfo = new PtonePanelInfo();
//            BeanUtils.copyProperties(panelExt, panelInfo);
//            panelExtVo =
//                serviceFactory.getPanelService().updatePanelAndLayout(panelInfo,
//                    panelExt.getSpaceId());
//          }
//          jsonView = this.buildPanelLayoutJsonView(panelExtVo);
//        } else {
//          if (PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER)) {
//            PtonePanelTemplet templet = new PtonePanelTemplet();
//            BeanUtils.copyProperties(panelExt, templet);
//            serviceFactory.getPanelTempletService().update(templet);
//          } else {
//            PtonePanelInfo panelInfo = new PtonePanelInfo();
//            BeanUtils.copyProperties(panelExt, panelInfo);
//            serviceFactory.getPanelService().update(panelInfo);
//          }
//          jsonView.successPack("update panelInfo success !");
//        }
//      } else if (PanelLayoutNode.TYPE_CONTAINER.equals(panelExt.getType())) {
//        // 修改panel文件夹
//        PtonePanelInfo panelInfo = new PtonePanelInfo();
//        BeanUtils.copyProperties(panelExt, panelInfo);
//        panelExtVo =
//            serviceFactory.getPanelService().updatePanelFolder(panelInfo, panelInfo.getSpaceId());
//        jsonView = this.buildPanelLayoutJsonView(panelExtVo);
//      } else {
//        logger.error(JSON.toJSONString(panelExt) + " | update Panel error:  type = "
//            + panelExt.getType() + " is not available");
//        jsonView.errorPack(JSON.toJSONString(panelExt) + " | update Panel error: type  = "
//            + panelExt.getType() + " is not available");
//      }
//    } catch (Exception e) {
//      logger.error(JSON.toJSONString(panelExt) + " | update Panel error.", e);
//      jsonView.errorPack(JSON.toJSONString(panelExt) + " | update Panel error.", e);
//    }
//    return jsonView;
//  }

  /**
   * 
   * @return
   * @date: 2016年11月15日
   * @author peng.xu
   */
//  @RequestMapping(value = "{panelId}", method = RequestMethod.DELETE,
//      produces = MediaType.APPLICATION_JSON)
//  @ResponseBody
//  @MethodRemark(remark = OpreateConstants.Panel.DEL_PANEL,
//      domain = OpreateConstants.BusinessDomain.PANEL)
//  public JsonView deletePanel(@RequestBody PanelInfoExt panelExt,
//      @PathVariable("panelId") String panelId, HttpServletRequest request, @RequestParam(
//          value = "sid", required = true) String sid) {
//    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      Map<String, Object[]> paramMap = new HashMap<>();
//      paramMap.put("panelId", new Object[] {panelId});
//      PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
//      List<PtoneSysRole> sysRoles = session.getSysRoles();
//      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
//      boolean isAdmin = PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER);
//      PanelExtVo panelExtVo = null;
//      
//      if (PanelLayoutNode.TYPE_PANEL.equals(panelExt.getType())) {
//        if (isAdmin) {
//          PtonePanelTemplet panel = serviceFactory.getPanelTempletService().getByWhere(paramMap);
//          panel.setStatus(Constants.inValidate);
//          panelExtVo =
//              serviceFactory.getPanelTempletService().deleteTempletAndLayout(panelId,
//                  panel.getSpaceId());
//        } else {
//          PtonePanelInfo panel = serviceFactory.getPanelService().getByWhere(paramMap);
//          panel.setStatus(Constants.inValidate);
//          panelExtVo =
//              serviceFactory.getPanelService().deletePanelAndLayout(panelId, panel.getSpaceId(),
//                  true);
//        }
//        jsonView = this.buildPanelLayoutJsonView(panelExtVo);
//      } else if (PanelLayoutNode.TYPE_CONTAINER.equals(panelExt.getType())) {
//        // 删除panel文件夹
//        jsonView = this.deletePanelFolder(panelExt, isAdmin);
//      } else {
//        logger.error(JSON.toJSONString(panelExt) + " | delete Panel error:  type = "
//            + panelExt.getType() + " is not available");
//        jsonView.errorPack(JSON.toJSONString(panelExt) + " | delete Panel error: type  = "
//            + panelExt.getType() + " is not available");
//      }
//      logger.info("uid:" + loginPtoneUser.getPtId() + " | panelId:" + panelId
//          + " del panel success.");
//    } catch (Exception e) {
//      logger.error(panelId + " | delete Panel error.", e);
//      jsonView.errorPack(panelId + " | delete Panel error.", e);
//    }
//    return jsonView;
//  }

  /**
   * 删除panelFolder
   * @date: 2016年12月5日
   * @author peng.xu
   */
//  private JsonView deletePanelFolder(PanelInfoExt infoExt, boolean isAdmin) {
//    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      String[] pids = infoExt.getPids();
//      String panelFolderId = infoExt.getPanelId();
//      PanelExtVo panelExtVo = null;
//      if (isAdmin) {
//        panelExtVo =
//            serviceFactory.getPanelTempletService().batchDeleteTempletsAndLayout(panelFolderId,
//                pids, infoExt.getSpaceId(), true);
//      } else {
//        boolean isHaveNot = false;// 是否有不是当前用户拥有的Panel
//        if (null != pids && ArrayUtils.isNotEmpty(pids)) {
//          List<PtonePanelInfo> panels =
//              serviceFactory.getPanelService().findByWhere("panelId", "in", pids);
//          for (PtonePanelInfo panel : panels) {
//            if (panel == null) {
//              continue;
//            }
//            if (!infoExt.getSpaceId().equalsIgnoreCase(panel.getSpaceId())) {
//              isHaveNot = true;
//              break;
//            }
//          }
//        }
//        if (isHaveNot) {
//          ServiceException se =
//              new ServiceException(" del Panel failed. Folder does not belong to your panel.");
//          throw se;
//        } else {
//          panelExtVo =
//              serviceFactory.getPanelService().batchDeletePanelsAndLayout(panelFolderId, pids,
//                  infoExt.getSpaceId(), true);
//        }
//      }
//      jsonView = this.buildPanelLayoutJsonView(panelExtVo);
//    } catch (Exception e) {
//      logger.error(" | del Folder error.", e);
//      jsonView.errorPack(" | del Folder error.", e);
//    }
//    return jsonView;
//  }

  @RequestMapping(value = "layout", method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView updatePanelLayout(@RequestBody PtonePanelLayout panelLayout,
      HttpServletRequest request, @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
      List<PtoneSysRole> sysRoles = session.getSysRoles();
      boolean isPanelTemplet = false;

      PanelExtVo panelExtVo = 
          serviceFactory.getPanelLayoutService().updateDataVersion(panelLayout,
              panelLayout.getSpaceId(), isPanelTemplet);
      if (panelExtVo != null
          && JsonViewConstants.JSON_VIEW_STATUS_SUCCESS.equals(panelExtVo.getStatus())) {
        jsonView.successPack(panelExtVo.getPanelLayout());
      } else {
        jsonView = this.buildPanelLayoutJsonView(panelExtVo);
      }
    } catch (Exception e) {
      logger.error(" update Panel layout error.", e);
      jsonView.errorPack(" update Panel layout error.", e);
    }
    return jsonView;
  }

  /**
   * 验证panel分享的密码是否正确
   * @date: 2016年12月5日
   * @author li.zhang
   */
  @RequestMapping(value = "share/verification", method = RequestMethod.POST,
          produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView sharePanelVerifyPassword(@RequestBody Map<String,String> paramMap, HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      boolean validate = true;
      if(CollectionUtil.isEmpty(paramMap)){
        validate = false;
      }else{
        if(paramMap.containsKey("dashboardId") && paramMap.containsKey("password")){
          String dashboardId = paramMap.get("dashboardId");
          String password = paramMap.get("password");
          if(StringUtil.isNotBlank(dashboardId) && StringUtil.isNotBlank(password)){
            PtonePanelInfo panel = serviceFactory.getPanelService().get(dashboardId);
            if(null == panel){//无此panel
              validate = false;
            }else{
              String sharePassword = panel.getSharePassword();
                if(StringUtil.isBlank(sharePassword)){//密码为空串或null的时候，用户已取消panel密码认证
                  validate = true;
                }else{
                  if(sharePassword.equals(password)){//验证密码通过
                    validate = true;
                  }else {//验证密码未通过
                    validate = false;
                  }
                }
            }
          }else{
            validate = false;
          }
        }else{
          validate = false;
        }
      }
      jsonView.successPack(validate);
    } catch (Exception e) {
      jsonView.errorPack(" share panel verify password error.", e);
    }
    return jsonView;
  }

  private JsonView buildPanelLayoutJsonView(PanelExtVo panelExtVo) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    Map<String, Object> resultMap = new HashMap<String, Object>();
    
    // 兼容以前代码： 将panelExtVo转为resultMap
    if (panelExtVo != null) {
      resultMap.put("panel", panelExtVo.getPanel());
      resultMap.put("panelInfo", panelExtVo.getPanel());
      resultMap.put("resultList", panelExtVo.getPanelList());
      resultMap.put("resultLayout", panelExtVo.getPanelLayout());
      resultMap.put("status", panelExtVo.getStatus());
    }
    
    if (JsonViewConstants.JSON_VIEW_STATUS_SUCCESS.equals(resultMap.get("status"))) {
      jsonView.successPack(resultMap);
    } else if (JsonViewConstants.JSON_VIEW_STATUS_FAILED.equals(resultMap.get("status"))) {
      // jsonView.failedPack("Panel layout version is update !", resultMap);
      logger.warn("Panel layout version is update !");
      jsonView.successPack(resultMap);
    } else {
      logger.error("update panel layout error !");
      jsonView.errorPack("update panel layout error !");
    }
    return jsonView;
  }
  
}
