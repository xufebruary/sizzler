package com.sizzler.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.ptmind.common.utils.StringUtil;
import com.sizzler.common.MediaType;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.pmission.PtoneSysRole;
import com.sizzler.domain.session.dto.PtoneSession;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.domain.widget.dto.AcceptWidget;
import com.sizzler.domain.widget.vo.MetricsDimensionsAliasVo;
import com.sizzler.system.Constants;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.annotation.MethodRemark;

@Controller
@Scope("prototype")
@RequestMapping("/widgets")
public class WidgetController {

  private Logger log = LoggerFactory.getLogger(WidgetController.class);

  @Autowired
  private ServiceFactory serviceFactory;

  @RequestMapping(value = "getOne/{widgetId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getWidgetById(@PathVariable("widgetId") String widgetId,
      HttpServletResponse response) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    long start = System.currentTimeMillis();
    try {
      AcceptWidget widget = serviceFactory.getWidgetService().getWidgetById(widgetId);
      jsonView.successPack(widget);
    } catch (Exception e) {
      jsonView.errorPack(" get widget<" + widgetId + "> error.", e);
    }
    long end = System.currentTimeMillis();
    log.info(">>> end get widget<" + widgetId + "> info, cost: " + (end - start));
    return jsonView;
  }

  @RequestMapping(value = "templet/t/{wid}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getWidgetTemplet(@PathVariable("wid") String wid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      AcceptWidgetTemplet templet = serviceFactory.getWidgetService().getTempletInfo(wid);
//      jsonView.successPack(templet);
//    } catch (Exception e) {
//      jsonView.errorPack(" get widget templet error.", e);
//    }
    return jsonView;
  }

  /**
   * 返回widget数据的同时返回当前layout信息
   * @param pid
   * @param request
   * @param sid
   * @author li.zhang
   * @date 2016/11/16
   * @return
   */
  @RequestMapping(value = "widgetWithLayout/{pid}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark = OpreateConstants.Widget.GET_LIST_WITH_LAYOUT,
      domain = OpreateConstants.BusinessDomain.WIDGET)
  public JsonView getWidgetWithLayout(@PathVariable("pid") String pid,
                                      HttpServletRequest request, @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      jsonView = getWidgetList(pid, sid, false,request);
      if (jsonView.getStatus().equals("success")) {
        fillLayoutInfo(jsonView, pid, sid);
      } else {
        jsonView.setMessage("get widget with layout error,cause by get widget list error.");
      }
    } catch (Exception e) {
      jsonView.errorPack("get widget with layout error.", e);
      e.printStackTrace();
    }
    return jsonView;
  }

  /**
   * 返回widget数据的同时返回当前layout信息(mobile)
   * @param pid
   * @param request
   * @param sid
   * @author li.zhang
   * @date 2016/11/16
   * @return
   */
  @RequestMapping(value = "widgetWithLayout/mobile/{pid}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark = OpreateConstants.Widget.GET_MOBILE_LIST_WITH_LAYOUT,
      domain = OpreateConstants.BusinessDomain.WIDGET)
  public JsonView getMobileWidgetWithLayout(@PathVariable("pid") String pid,
                                            HttpServletRequest request, @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      jsonView = getWidgetList(pid, sid, true,request);
      if (jsonView.getStatus().equals("success")) {
        fillLayoutInfo(jsonView, pid, sid);
      } else {
        jsonView.setMessage("get mobile widget with layout error,cause by get widget list error.");
      }
    } catch (Exception e) {
      jsonView.errorPack("get mobile widget with layout error.", e);
      e.printStackTrace();
    }
    return jsonView;
  }

  public void fillLayoutInfo(JsonView jsonView, String pid, String sid) {
    List<AcceptWidget> widgetList = new ArrayList<>();
    if (null != jsonView.getContent() && !jsonView.getContent().equals("")) {
      widgetList = (List<AcceptWidget>) jsonView.getContent();
    }
    Map<String, Object> resultMap = new HashMap<>();

    PtoneSession session = serviceFactory.getSessionContext().getSession(sid);
    boolean isAdmin = false;
//    if (session != null) {
//      List<PtoneSysRole> sysRoles = session.getSysRoles();
//      isAdmin = PermissionUtil.hasSysRole(sysRoles, Constants.Permission.ADMIN_USER);
//    }

    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("panelId", new Object[] {pid});

    if (isAdmin) {
//      PtonePanelTemplet panelTemplet = serviceFactory.getPanelTempletService().getByWhere(paramMap);
//      resultMap.put("layout", panelTemplet.getLayout());
    } else {
      PtonePanelInfo panel = serviceFactory.getPanelService().getByWhere(paramMap);
      if (StringUtil.isNotBlank(panel.getShareSourceId())) {
        paramMap.put("panelId", new Object[] {panel.getShareSourceId()});
        panel = serviceFactory.getPanelService().getByWhere(paramMap);
      }
      resultMap.put("layout", panel.getLayout());
    }
    resultMap.put("widgetList", widgetList);
    jsonView.setContent(resultMap);
  }

  @RequestMapping(value = "widget/{pid}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getWidget(@PathVariable("pid") String pid, HttpServletRequest request,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = getWidgetList(pid, sid, false, request);
    return jsonView;
  }

  @RequestMapping(value = "widget/mobile/{pid}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getMobileWidget(@PathVariable("pid") String pid, HttpServletRequest request,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = getWidgetList(pid, sid, true, request);
    return jsonView;
  }

  /**
   * 
   * @param pid
   * @param sid
   * @param isMobile 代表是否为移动端的请求，如果是移动端的请求，则会对widget进行排序
   * @return
   */
  private JsonView getWidgetList(String pid, String sid, boolean isMobile,HttpServletRequest request) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    String uiPid = pid;
    try {
      long startTime = System.currentTimeMillis();
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("panelId", new Object[] {pid});
      PtonePanelInfo panel = serviceFactory.getPanelService().getByWhere(paramMap);
      PtonePanelInfo sharePanel = null;
      if (null != panel && StringUtil.hasText(panel.getShareSourceId())) {
        pid = panel.getShareSourceId();
        paramMap = new HashMap<>();
        paramMap.put("panelId", new Object[] {pid});
        sharePanel = serviceFactory.getPanelService().getByWhere(paramMap);
        // 增加分享panel所在空间已删除的判断
        String shareSpaceId = sharePanel.getSpaceId();
        PtoneSpaceInfo spaceInfo = serviceFactory.getSpaceService().get(shareSpaceId);
        if (spaceInfo == null) {
          jsonView.messagePack("space_del");
          return jsonView;
        }
        if (sharePanel.getStatus().equals(Constants.inValidate)) {// 分享panel已删除
          jsonView.messagePack("panel_del");
          return jsonView;
        }
        if (sharePanel.getShareUrl().equals(Constants.inValidate)) {// 分享panel已关闭
          jsonView.messagePack("panel_close");
          return jsonView;
        }
        String sharePassword = sharePanel.getSharePassword();
        if(StringUtil.isNotBlank(sharePassword)){
          String password = request.getParameter("password");
          if(StringUtil.isBlank(password)){
            jsonView.messagePack("password_error");
            return jsonView;
          }else {
            if(!sharePassword.equals(password)){
              jsonView.messagePack("password_error");
              return jsonView;
            }
          }
        }
      }
      List<AcceptWidget> widgetsList = serviceFactory.getWidgetService().findWidget(pid);
      // 把原panelId塞回去
      if (!uiPid.equals(pid)) {
        for (AcceptWidget widget : widgetsList) {
          widget.setPanelId(uiPid);
        }
      }
      long endTime = System.currentTimeMillis();
      Map<String, Object> operateInfo = new HashMap<>();
      operateInfo.put(OpreateConstants.EXECUTE_TIME, endTime - startTime);
      if (loginPtoneUser != null) {
        operateInfo.put(OpreateConstants.USER_EMAIL, loginPtoneUser.getUserEmail());
      }
      operateInfo.put("isMobile", isMobile);

      // 移动端需要对widget进行排序
      if (isMobile) {
        long sortStartTime = System.currentTimeMillis();
//        widgetsList = WidgetUtil.sortAcceptWidgetByWidgetSortKey(panel.getLayout(), widgetsList);
        long sortEndTime = System.currentTimeMillis();
        operateInfo.put("widgetSortTime", sortEndTime - sortStartTime);
      }
      jsonView.successPack(widgetsList);
    } catch (Exception e) {
      jsonView.errorPack(" query widget error.", e);
    }
    return jsonView;
  }



  @RequestMapping(value = "add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark = OpreateConstants.Widget.ADD_WIDGET,
      domain = OpreateConstants.BusinessDomain.WIDGET)
  public JsonView saveWidget(@RequestBody AcceptWidget widget, @RequestParam(value = "sid",
      required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      long startTime = System.currentTimeMillis();
      AcceptWidget newWidget = serviceFactory.getWidgetService().save(widget);
      long endTime = System.currentTimeMillis();
      Map<String, Object> operateInfo = new HashMap<>();
      operateInfo.put(OpreateConstants.EXECUTE_TIME, endTime - startTime);
      operateInfo.put(OpreateConstants.USER_EMAIL, loginPtoneUser.getUserEmail());
      LogMessage logMessage = new LogMessage();
      logMessage.setUid(loginPtoneUser.getPtId());
      logMessage.setOperate(OpreateConstants.Widget.ADD_WIDGET);
      logMessage.setOperateInfo(operateInfo);
      log.info(logMessage.toString());
      jsonView.successPack(newWidget);
    } catch (Exception e) {
      jsonView.errorPack(JSON.toJSONString(widget) + " | save widget error.", e);
    }
    return jsonView;
  }

  /**
   * 根据widget模板id列表，批量创建widget
   * @param templetIdList
   * @return
   * @date: 2016年8月15日
   * @author peng.xu
   */
  @RequestMapping(value = "addByTemplet/{spaceId}/{panelId}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView addWidgetByTemplet(
      @RequestBody List<String> templetIdList,
      @PathVariable("spaceId") String spaceId,
      @PathVariable("panelId") String panelId,
      HttpServletRequest request,
      @RequestParam(value = "sid", required = true) String sid,
      @RequestParam(value = "isPreview", defaultValue = Constants.inValidate, required = false) String isPreview) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      List<AcceptWidget> newWidgetList =
          serviceFactory.getWidgetService().addWidgetByTemplet(templetIdList, spaceId, panelId,
              loginPtoneUser.getPtId(), isPreview);
      jsonView.successPack(newWidgetList);
    } catch (Exception e) {
      jsonView.errorPack(JSON.toJSONString(templetIdList) + " | add widget by templet error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "update", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView updateWidget(@RequestBody AcceptWidget widget) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      AcceptWidget newWidget = serviceFactory.getWidgetService().updateWidgetWithVariables(widget);
      jsonView.successPack(newWidget);
    } catch (Exception e) {
      jsonView.errorPack(JSON.toJSONString(widget) + " | update widget error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "updateWidgets", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @Transactional
  public JsonView updateWidgetList(@RequestBody List<AcceptWidget> widgets) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      for (AcceptWidget newWidget : widgets) {
        serviceFactory.getWidgetService().updateWidgetWithVariables(newWidget);
      }
      jsonView.messagePack("update widget list success.");
    } catch (Exception e) {
      jsonView.errorPack(" update widget list error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "updateBaseWidget", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView updateBaseWidget(@RequestBody PtoneWidgetInfo ptoneWidgetInfo) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      serviceFactory.getWidgetService().updateBaseWidget(ptoneWidgetInfo);
      jsonView.messagePack("update PtoneWidgetInfo success.");
    } catch (Exception e) {
      jsonView
          .errorPack(JSON.toJSONString(ptoneWidgetInfo) + " | update PtoneWidgetInfo error.", e);
    }
    return jsonView;
  }

  /**
   * 
   * @description 级联删除widget
   * @date 2016年11月17日 下午3:00:22
   * @param wid
   * @param request
   * @param sid
   * @return
   * @modify shaoqiang.guo
   */
  @RequestMapping(value = "del/{wid}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @Transactional
  @MethodRemark(remark = OpreateConstants.Widget.DEL_WIDGET,
      domain = OpreateConstants.BusinessDomain.WIDGET)
  public JsonView deleteWidget(@PathVariable("wid") String wid, HttpServletRequest request,
      @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      // true 标识删除widget
      serviceFactory.getWidgetService().softDeletingWidget(wid, true);
      jsonView.messagePack(" del widget success.");
    } catch (Exception e) {
      jsonView.errorPack(wid + " | del widget error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "selectedMetrics/{widgetId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getWidgetSelectedMetricsOld(@PathVariable("widgetId") String widgetId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneMetricsDimension> metrics =
          serviceFactory.getWidgetService().getWidgetSelectedMetrics(widgetId);
      jsonView.successPack(metrics);
    } catch (Exception e) {
      log.error("get widget<" + widgetId + "> selectedMetrics error.", e);
      jsonView.errorPack("get widget<" + widgetId + "> selectedMetrics error.", e);
    }
    return jsonView;
  }
  
  /**
   * 获取widget已选指标列表
   * @param widgetId
   * @return
   * @date: 2016年12月7日
   * @author peng.xu
   */
  @RequestMapping(value = "{widgetId}/metrics", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getWidgetSelectedMetrics(@PathVariable("widgetId") String widgetId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneMetricsDimension> metrics =
          serviceFactory.getWidgetService().getWidgetSelectedMetrics(widgetId);
      jsonView.successPack(metrics);
    } catch (Exception e) {
      log.error("get widget<" + widgetId + "> selected metrics error.", e);
      jsonView.errorPack("get widget<" + widgetId + "> selected metrics error.", e);
    }
    return jsonView;
  }

  /**
   * 获取widget已选维度列表
   * @param widgetId
   * @return
   * @date: 2016年12月7日
   * @author peng.xu
   */
  @RequestMapping(value = "{widgetId}/dimensions", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getWidgetSelectedDimensions(@PathVariable("widgetId") String widgetId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneMetricsDimension> dimensions =
          serviceFactory.getWidgetService().getWidgetSelectedDimensions(widgetId);
      jsonView.successPack(dimensions);
    } catch (Exception e) {
      log.error("get widget<" + widgetId + "> selected dimensions error.", e);
      jsonView.errorPack("get widget<" + widgetId + "> selected dimensions error.", e);
    }
    return jsonView;
  }

  /**
   * 更改指标或者维度别名
   * @return
   * @date: 2017年1月5日
   * @author li.zhang
   */
  @RequestMapping(value = "metrics-dimensions/alias", method = RequestMethod.PUT,
          produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView updateMetricsAlias( @RequestBody MetricsDimensionsAliasVo aliasVo,BindingResult result) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      if(result.hasErrors()){
        jsonView.failedPack("update metrics alias failed,caused by some field is null");
        return jsonView;
      }
      boolean re = serviceFactory.getWidgetService().updateMetricsAlias(aliasVo);
      if(re){
        jsonView.messagePack("update metrics alias success");
      }else{
        jsonView.failedPack("update metrics alias failed");
      }
    } catch (Exception e) {
      jsonView.errorPack("update metrics alias error.", e);
    }
    return jsonView;
  }

}
