package com.sizzler.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sizzler.common.MediaType;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.widget.PtoneWidgetInfo;
import com.sizzler.domain.widget.dto.AcceptWidget;
import com.sizzler.domain.widget.vo.MetricsDimensionsAliasVo;
import com.sizzler.system.Constants;
import com.sizzler.system.OpreateConstants;
import com.sizzler.system.annotation.MethodRemark;
import com.sizzler.system.api.annotation.ApiVersion;
import com.sizzler.system.api.common.ResponseResult;
import com.sizzler.system.api.common.RestResultGenerator;

/**
 * @ClassName: WidgetController
 * @Description:.
 * @Company: Copyright (c) Ptmind
 * @version: 1.0
 * @date: 2017/1/3
 * @author: zhangli
 */
@RestController("widgetApiController")
@RequestMapping("{version}/widgets")
@Scope("prototype")
@ApiVersion(Constants.API_VERSION_1)
public class WidgetController extends BaseController {

  /**
   * 新增widget
   * @author li.zhang
   * @date 2017/1/3
   * @param widget
   * @param token
   * @return
   */
  @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Widget.API_ADD_WIDGET, domain = OpreateConstants.BusinessDomain.WIDGET)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseResult saveWidget(@RequestBody AcceptWidget widget,
      @RequestHeader(value = "token", required = false) String token) {
    // todo 返回的AcceptWidget要和前端对，新建vo对象，返回必要的字段
    AcceptWidget newWidget = serviceFactory.getWidgetService().save(widget);
    return RestResultGenerator.genResult(newWidget);
  }

  /**
   * 根据widget模板id列表，批量创建widget
   * @param templetIdList
   * @return
   * @date: 2016年8月15日
   * @author peng.xu
   */
  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(value = "templet/{spaceId}/{panelId}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  public ResponseResult addWidgetByTemplet(@RequestBody List<String> templetIdList,
      @PathVariable("spaceId") String spaceId, @PathVariable("panelId") String panelId, @RequestHeader(value = "token",
          required = false) String token, HttpServletRequest request, @RequestParam(value = "isPreview",
          defaultValue = Constants.inValidate, required = false) String isPreview) {
    PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getSessionUser(token);
    // todo 返回的AcceptWidget要和前端对，新建vo对象，返回必要的字段
    List<AcceptWidget> newWidgetList =
        serviceFactory.getWidgetService().addWidgetByTemplet(templetIdList, spaceId, panelId, loginPtoneUser.getPtId(),
            isPreview);
    return RestResultGenerator.genResult(newWidgetList);
  }

  /**
   * 查询单个widget及附属信息
   * @author li.zhang
   * @date 2017/1/4
   * @param widgetId
   * @param token
   * @return
   */
  @RequestMapping(value = "{widgetId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  public ResponseResult getWidgetById(@PathVariable("widgetId") String widgetId, @RequestHeader(value = "token",
      required = false) String token, HttpServletResponse response) {
    // todo 返回的AcceptWidget要和前端对，新建vo对象，返回必要的字段
    AcceptWidget widget = serviceFactory.getWidgetService().getWidgetById(widgetId);
    return RestResultGenerator.genResult(widget);
  }

  /**
   * 获取widget已选指标列表
   * @param widgetId
   * @return
   * @date: 2016年12月7日
   * @author peng.xu
   */
  @RequestMapping(value = "{widgetId}/metrics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  public ResponseResult getWidgetSelectedMetrics(@PathVariable("widgetId") String widgetId, @RequestHeader(
      value = "token", required = false) String token) {
    List<PtoneMetricsDimension> metrics = serviceFactory.getWidgetService().getWidgetSelectedMetrics(widgetId);
    return RestResultGenerator.genResult(metrics);
  }

  /**
   * 获取widget已选维度列表
   * @param widgetId
   * @return
   * @date: 2016年12月7日
   * @author peng.xu
   */
  @RequestMapping(value = "{widgetId}/dimensions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  public ResponseResult getWidgetSelectedDimensions(@PathVariable("widgetId") String widgetId, @RequestHeader(
      value = "token", required = false) String token) {
    List<PtoneMetricsDimension> dimensions = serviceFactory.getWidgetService().getWidgetSelectedDimensions(widgetId);
    return RestResultGenerator.genResult(dimensions);
  }

  /**
   * 更新单个widget
   * @author li.zhang
   * @date 2017/1/4
   * @param widget
   * @param token
   * @return
   */
  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
  public ResponseResult updateWidget(@RequestBody AcceptWidget widget,
      @RequestHeader(value = "token", required = false) String token) {
    AcceptWidget newWidget = serviceFactory.getWidgetService().updateWidgetWithVariables(widget);
    return RestResultGenerator.genResult(newWidget);
  }

  /**
   * 批量更新widget
   * @author li.zhang
   * @date 2017/1/7
   * @param widgets
   * @param token
   * @return
   */
  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(value = "batch", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
  public void updateWidgetList(@RequestBody List<AcceptWidget> widgets, @RequestHeader(value = "token",
      required = false) String token) {
    // todo 下沉加事物
    for (AcceptWidget newWidget : widgets) {
      serviceFactory.getWidgetService().updateWidgetWithVariables(newWidget);
    }
  }

  /**
   * 更新widget基本信息
   * @author li.zhang
   * @date 2017/1/7
   * @param ptoneWidgetInfo
   * @param token
   * @return
   */
  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(value = "base", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
  public void updateBaseWidget(@RequestBody PtoneWidgetInfo ptoneWidgetInfo, @RequestHeader(value = "token",
      required = false) String token) {
    serviceFactory.getWidgetService().updateBaseWidget(ptoneWidgetInfo);
  }

  /**
   * 更改指标或者维度别名
   * @return
   * @date: 2017年1月5日
   * @author li.zhang
   * @param aliasVo
   */
  @RequestMapping(value = "metrics-dimensions/alias", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
  @ResponseStatus(HttpStatus.CREATED)
  public void updateMetricsAlias( @RequestBody MetricsDimensionsAliasVo aliasVo, @RequestHeader(
      value = "token", required = false) String token) {
    serviceFactory.getWidgetService().updateMetricsAndAlias(aliasVo);
  }

  /**
   * @description 级联删除widget
   * @date 2016年11月17日 下午3:00:22
   * @param widgetId
   * @param request
   * @param token
   * @return
   * @modify shaoqiang.guo
   */
  @RequestMapping(value = "{widgetId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON)
  @MethodRemark(remark = OpreateConstants.Widget.DEL_WIDGET, domain = OpreateConstants.BusinessDomain.WIDGET)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteWidget(@PathVariable("widgetId") String widgetId, HttpServletRequest request,
      @RequestHeader(value = "token", required = false) String token) {
    // true 标识删除widget
    serviceFactory.getWidgetService().softDeletingWidget(widgetId, true);
  }


}
