package com.sizzler.controller.rest;

import java.util.Enumeration;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sizzler.common.Constants.JsonViewConstants;
import com.sizzler.common.MediaType;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.panel.PanelGlobalComponent;
import com.sizzler.domain.panel.PtonePanelInfo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.widget.dto.AcceptWidget;
import com.sizzler.service.DataService;
import com.sizzler.system.Constants;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.ServiceFactory;
import com.sizzler.system.annotation.MethodRemark;

/**
 * 获取widget图表数据
 * 
 * @author peng.xu
 */
@Controller
@Scope("prototype")
@RequestMapping("/data")
public class DataController {

  private Logger log = LoggerFactory.getLogger(DataController.class);

  @Autowired
  private ServiceFactory serviceFactory;

  @Autowired
  private DataService dataService;

  /**
   * 根据前台传递的AcceptWidget获取widget数据
   * @return
   */
  @RequestMapping(value = "widgetData/{widgetId}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getWidgetData(HttpServletRequest request, @RequestParam(value = "sid",
      required = false) String sid, HttpServletResponse response,
      @PathVariable("widgetId") String widgetId, @RequestBody AcceptWidget widget) {

    JsonView jsonView = JsonViewFactory.createJsonView();

    PtoneUser loginPtoneUser = null;
    try {
      loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
    } catch (Exception e) {
      // 分享中可能取不到当前的登录用户
      log.error("get loginPtoneUser error: " + e.getMessage(), e);
    }

    // 获取前台传递的参数，通过map向下传递
    Map<String, String> webParamMap = new HashMap<String, String>();
    Enumeration<String> enu = request.getParameterNames();
    while (enu.hasMoreElements()) {
      String paramName = (String) enu.nextElement();
      String paramValue = request.getParameter(paramName);
      webParamMap.put(paramName, paramValue);
    }
    jsonView = this.sendWidgetDataRequest(widgetId, webParamMap, widget, loginPtoneUser);
    return jsonView;

  }

  /**
   * 根据panelId获取panel下所有widget的数据
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(value = "batchWidgetData/{panelId}/{sign}/{dataVersion}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getBatchWidgetData(HttpServletRequest request, @RequestParam(value = "sid",
      required = false) String sid, HttpServletResponse response,
      @PathVariable("panelId") String panelId, @PathVariable("sign") String sign,
      @PathVariable("dataVersion") String dataVersion) {

    JsonView jsonView = JsonViewFactory.createJsonView();
    String logMsg = " send batchWidgetData<panelId=" + panelId + "> request ";
    LogMessage logMessage = new LogMessage();
    logMessage.setOperate("getBatchWidgetData");
    logMessage.addOperateInfo("panelId", panelId);
    logMessage.addOperateInfo("sign", sign);
    logMessage.addOperateInfo("dataVersion", dataVersion);

    PtoneUser loginPtoneUser = null;
//    try {
//      loginPtoneUser = serviceFactory.getPtoneSessionContext().getLoginUser(sid);
//      if (loginPtoneUser != null) {
//        logMessage.setUid(loginPtoneUser.getPtId());
//        logMessage.addOperateInfo("queryUid", loginPtoneUser.getPtId());
//        logMessage.addOperateInfo("queryEmail", loginPtoneUser.getUserEmail());
//      }
//    } catch (Exception e) {
//      // 分享中可能取不到当前的登录用户
//      log.error("get loginPtoneUser error: " + e.getMessage(), e);
//    }

    Map<String, String> webParamMap = new HashMap<String, String>();
    webParamMap.put("sign", sign);
    webParamMap.put("dataVersion", dataVersion);
    // TODO： 目前分享不支持批量取数，暂时直接设置requestSource = datadeck-product
    webParamMap.put("requestSource", "datadeck-product");

    String uiPanelId = panelId;
    List<AcceptWidget> widgetsList = null;
    try {
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("panelId", new Object[] {panelId});
      PtonePanelInfo panel = serviceFactory.getPanelService().getByWhere(paramMap);

      // 设置全局时间
      String panelGlobalTime = null;
      if (panel != null) {
        paramMap = new HashMap<>();
        paramMap.put("panelId", new Object[] {panelId});
        paramMap.put("itemId", new Object[] {PanelGlobalComponent.COMPONENT_ITEM_ID_GLOBAL_TIME});// 时间组件id=16
        PanelGlobalComponent dbComponent = serviceFactory.getPanelService().getComponents(paramMap);
        if (dbComponent != null
            && Constants.validate.equals(dbComponent.getStatus())
            && !PanelGlobalComponent.COMPONENT_GLOBAL_TIME_DEFAULT_VALUE.equals(dbComponent
                .getValue())) {
          panelGlobalTime = dbComponent.getValue();
        }
      }

      // 判断是否为分享的panel
      PtonePanelInfo sharePanel = null;
      if (null != panel && StringUtil.isNotBlank(panel.getShareSourceId())) {
        panelId = panel.getShareSourceId();
        paramMap = new HashMap<>();
        paramMap.put("panelId", new Object[] {panel.getShareSourceId()});
        sharePanel = serviceFactory.getPanelService().getByWhere(paramMap);
        if (sharePanel.getStatus().equals(Constants.inValidate)
            || sharePanel.getShareUrl().equals(Constants.inValidate)) {

          logMessage.addOperateInfo("status", "failed");
          logMessage.addOperateInfo("failedMsg", "sharePanel is closed or delete");
          log.info(logMessage.toString());

          jsonView.messagePack("panel_close");// 分享的panel删除或已关闭分享
          return jsonView;
        }
      }

      widgetsList = serviceFactory.getWidgetService().findWidget(panelId);

      // 发送widget取数请求
      if (CollectionUtil.isNotEmpty(widgetsList)) {
        for (AcceptWidget widget : widgetsList) {
          // 修正panelId， 把原panelId设置到widget上
          if (!uiPanelId.equals(panelId)) {
            widget.setPanelId(uiPanelId);
          }
          if (widget != null && widget.getBaseWidget() != null) {
            if (StringUtil.isNotBlank(panelGlobalTime)) {
              widget.getBaseWidget().setDateKey(panelGlobalTime);
            }
            this.sendWidgetDataRequest(widget.getBaseWidget().getWidgetId(), webParamMap, widget,
                loginPtoneUser);
          }
        }
      }

      logMessage.addOperateInfo("status", "success");
      log.info(logMessage.toString());
      jsonView.successPack(logMsg + "success");
    } catch (Exception e) {
      logMessage.addOperateInfo("status", "error");
      logMessage.addOperateInfo("errorMsg", e.getMessage());
      log.error(logMessage.toString(), e);
      jsonView.errorPack(logMsg + "request error:" + e.getMessage(), e);
    }
    return jsonView;
  }


  private JsonView sendWidgetDataRequest(String widgetId, Map<String, String> webParamMap,
      AcceptWidget widget, PtoneUser loginPtoneUser) {

    JsonView jsonView = JsonViewFactory.createJsonView();

    String loginUid = null;
    String loginUserEmail = null;
    if (loginPtoneUser != null) {
      loginUid = loginPtoneUser.getPtId();
      loginUserEmail = loginPtoneUser.getUserEmail();
      webParamMap.put(Constants.PT_LOGIN_USER_ID, loginUid);
      webParamMap.put(Constants.PT_LOGIN_USER_EMAIL, loginUserEmail);
    }

//    /**
//     * 启动一个新线程，发送取数请求
//     */
//    final String fWidgetId = widgetId;
//    final Map<String, String> fWebParamMap = webParamMap;
//    final AcceptWidget fWidget = widget;
//    Thread t = new Thread(new Runnable() {
//      public void run() {
//        if (ThreadPoolConstants.useThreadPool) {
//          /**
//           * 使用线程池限制widget取数请求队列
//           */
//          dataService.addDataTask(fWidgetId, fWebParamMap, fWidget);
//        } else {
//          serviceFactory.getPtoneDataService()
//              .getWidgetData(fWidgetId, fWebParamMap, fWidget, null);
//        }
//      }
//    });
//    t.start();

    if (webParamMap.containsKey(JsonViewConstants.PARAM_DATA_VERSION)) {
      jsonView.setDataVersion(webParamMap.get(JsonViewConstants.PARAM_DATA_VERSION));
    }
    jsonView.successPack("success");

    log.info(">>> send widgetData request success , widgetId::" + widgetId + ", queryUid::"
        + loginUid + ", queryUserEmail:" + loginUserEmail);

    return serviceFactory.getPtoneDataService()
        .getWidgetData(widgetId, webParamMap, widget, null);
  }

  /**
   * 
   * @param request
   * @param response
   * @param widgetId 如果非编辑器中，根据widgetId从数据库中查询配置信息
   * @param widget 如果是编辑器中取数，根据前台传递的AcceptWidget获取配置信息
   * @return
   */
  @RequestMapping(value = "csvWidgetData/{widgetId}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  @MethodRemark(remark = OpreateConstants.Widget.EXPORT_WIDGET_CSV,
      domain = OpreateConstants.BusinessDomain.WIDGET)
  public JsonView getCsvWidgetData(HttpServletRequest request, @RequestParam(value = "sid",
      required = false) String sid, HttpServletResponse response,
      @PathVariable("widgetId") String widgetId, @RequestBody AcceptWidget widget) {

    // 获取前台传递的参数，通过map向下传递

    Map<String, String> webParamMap = new HashMap<String, String>();
    Enumeration<String> enu = request.getParameterNames();
    while (enu.hasMoreElements()) {
      String paramName = (String) enu.nextElement();
      String paramValue = request.getParameter(paramName);
      webParamMap.put(paramName, paramValue);
    }
    // 通过这里传递图形固定为table
    webParamMap.put("csvGraphName", "table");

    JsonView jsonView =
        serviceFactory.getPtoneDataService().getWidgetData(widgetId, webParamMap, widget, null);
    if (webParamMap.containsKey(JsonViewConstants.PARAM_DATA_VERSION)) {
      jsonView.setDataVersion(webParamMap.get(JsonViewConstants.PARAM_DATA_VERSION));
    }

    return jsonView;
  }

}
