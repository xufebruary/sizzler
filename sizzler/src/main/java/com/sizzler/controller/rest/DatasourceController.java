package com.sizzler.controller.rest;


import com.sizzler.common.MediaType;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.domain.ds.PtoneDsInfo;
import com.sizzler.domain.ds.UserCompoundMetricsDimension;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.domain.ds.dto.UserCompoundMetricsDimensionDto;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.system.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
@RequestMapping("/ds")
public class DatasourceController {
  
  private Logger log = LoggerFactory.getLogger(DatasourceController.class);
  
  @Autowired
  private ServiceFactory serviceFactory;

  /**
   * 根据dscode获取单个ds info
   */
  @RequestMapping(value = "info/{code}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getDatasourceByCode(HttpServletRequest request,
      @PathVariable("code") String code, @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneDsInfo dsInfo = serviceFactory.getPtoneDsService().getDsInfoByDsCode(code);
      jsonView.successPack(dsInfo);
    } catch (Exception e) {
      jsonView.errorPack(" get ds info by code error.", e);
    }
    return jsonView;
  }
  
  /**
   * @Description: 得到用户有权限的数据源.
   * @date: 2015/12/25
   * @author: zhangli
   */
  @RequestMapping(value = "owns/{spaceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getOpenDatasource(HttpServletRequest request,@PathVariable("spaceId") String spaceId, @RequestParam(value = "sid",
      required = false) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneDsInfo> dsList = serviceFactory.getPtoneDsService().getAllDsInfoList();
      jsonView.successPack(dsList);
    } catch (Exception e) {
      jsonView.errorPack(" get datasource list error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "metrics/{dsId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getMetrics(@PathVariable("dsId") long dsId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    return jsonView;
  }

  @RequestMapping(value = "dimension/{dsId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getDimension(@PathVariable("dsId") long dsId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    return jsonView;
  }


  /**
   * 获取segment指标、维度列表
   * @param dsId
   * @param scope: 指标、维度的级别 0： user || 1：session
   */
  @RequestMapping(value = "segmentList/{dsId}/{scope}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getSegmentList(@PathVariable("dsId") long dsId,
      @PathVariable("scope") String scope) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    return jsonView;
  }

  /**
   * 获取过滤器指标维度列表
   */
  @RequestMapping(value = "filterList/{dsId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getFilterList(@PathVariable("dsId") long dsId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
//    try {
//      // GA 返回二级结构， 其他返回一级
//      if (DsConstants.DS_ID_GA == dsId) {
//        List<PtoneMetricsDimensionWithCategory> filterList =
//            new ArrayList<PtoneMetricsDimensionWithCategory>();
//        List<PtoneMetricsDimensionWithCategory> dimensionList =
//            serviceFactory.getPtoneDsService().getDimensionByDsId(dsId);
//        List<PtoneMetricsDimensionWithCategory> metricsList =
//            serviceFactory.getPtoneDsService().getMetricsByDsId(dsId);
//
//        List<PtoneMetricsDimensionWithCategory> fixDimensionList =
//            new ArrayList<PtoneMetricsDimensionWithCategory>();
//        for (PtoneMetricsDimensionWithCategory dimensionCategory : dimensionList) {
//          List<PtoneMetricsDimension> tmpList = new ArrayList<>();
//          for (PtoneMetricsDimension dimension : dimensionCategory.getDimensionList()) {
//            if (dimension.getAllowFilter() == Constants.validateInt) {
//              tmpList.add(dimension);
//            }
//          }
//          if (tmpList.size() > 0) {
//            dimensionCategory.setItemList(tmpList);
//            fixDimensionList.add(dimensionCategory);
//          }
//        }
//
//        List<PtoneMetricsDimensionWithCategory> fixMetricsList =
//            new ArrayList<PtoneMetricsDimensionWithCategory>();
//        for (PtoneMetricsDimensionWithCategory metricsCategory : metricsList) {
//          List<PtoneMetricsDimension> tmpList = new ArrayList<PtoneMetricsDimension>();
//          for (PtoneMetricsDimension metrics : metricsCategory.getMetricsList()) {
//            if (metrics.getAllowFilter() == Constants.validateInt) {
//              tmpList.add(metrics);
//            }
//          }
//          if (tmpList.size() > 0) {
//            metricsCategory.setItemList(tmpList);
//            fixMetricsList.add(metricsCategory);
//          }
//        }
//
//        filterList.addAll(fixDimensionList);
//        // spliter
//        if (fixDimensionList.size() > 0 && fixMetricsList.size() > 0) {
//          PtoneMetricsDimensionWithCategory spliterCategory =
//              new PtoneMetricsDimensionWithCategory(PtoneMetricsDimension.TYPE_SPLITER);
//          filterList.add(spliterCategory);
//        }
//        filterList.addAll(fixMetricsList);
//
//        jsonView.successPack(filterList);
//
//      } else {
//
//        List<PtoneMetricsDimension> filterList =
//            serviceFactory.getPtoneDsService().getFilterListByDsId(dsId);
//        jsonView.successPack(filterList);
//
//      }
//
//    } catch (Exception e) {
//      jsonView.errorPack(" get filter list error.", e);
//    }
    return jsonView;
  }

  @RequestMapping(value = "userMetrics/{dsId}/{tableId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getUserMetrics(@PathVariable("dsId") long dsId,
      @PathVariable("tableId") String tableId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneMetricsDimension> metricsList =
          serviceFactory.getDataSourceManagerService().getUserMetricsDimensionList(
              dsId,
              tableId,
              new String[] {PtoneMetricsDimension.TYPE_METRICS,
                  PtoneMetricsDimension.TYPE_DIMENSION});
      jsonView.successPack(metricsList);
    } catch (Exception e) {
      jsonView.errorPack(
          " get user metrics < dsId:" + dsId + " , tableId:" + tableId + " > error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "userDimension/{dsId}/{tableId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getUserDimension(@PathVariable("dsId") long dsId,
      @PathVariable("tableId") String tableId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneMetricsDimension> dimensionList =
          serviceFactory.getDataSourceManagerService().getUserMetricsDimensionList(
              dsId,
              tableId,
              new String[] {PtoneMetricsDimension.TYPE_METRICS,
                  PtoneMetricsDimension.TYPE_DIMENSION});

      jsonView.successPack(dimensionList);
    } catch (Exception e) {
      jsonView.errorPack(" get user dimension < dsId:" + dsId + " , tableId:" + tableId
          + " > error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "userMetricsAndDimensions/{dsId}/{tableId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getUserMetricsAndDimensions(@PathVariable("dsId") long dsId,
      @PathVariable("tableId") String tableId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneMetricsDimension> list =
          serviceFactory.getDataSourceManagerService().getUserMetricsDimensionList(
              dsId,
              tableId,
              new String[] {PtoneMetricsDimension.TYPE_METRICS,
                  PtoneMetricsDimension.TYPE_DIMENSION});
      jsonView.successPack(list);
    } catch (Exception e) {
      jsonView.errorPack(" get user metrics and dimensions < dsId:" + dsId + " , tableId:"
          + tableId + " > error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "compoundTempletKey/{type}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getCompoundTempletMetricsDimensionKey(@PathVariable("type") String type) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    return jsonView;
  }

  @RequestMapping(value = "compoundTemplet/{type}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getCompoundTempletMetricsDimension(@PathVariable("type") String type) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    return jsonView;
  }

  @RequestMapping(value = "addCategory/{type}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView savePanel(@RequestBody Map<String, String> category,
      @PathVariable("type") String type) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    
    return jsonView;
  }

  @RequestMapping(value = "buildCompound/{type}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView buildCompoundMetricsDimension(@PathVariable("type") String type) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    jsonView.successPack(null);
    return jsonView;
  }

  /**
   * 新增复合指标
   * @param sid
   * @return
   * @date: 2016年7月14日
   * @author peng.xu
   */
  @RequestMapping(value = "addCalculatedValue", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView addCompoundMetrics(
          @RequestBody UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto, @RequestParam(
          value = "sid", required = true) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      userCompoundMetricsDimensionDto.setUid(loginPtoneUser.getPtId());
      userCompoundMetricsDimensionDto.setUserEmail(loginPtoneUser.getUserEmail());
      userCompoundMetricsDimensionDto.setCreatorId(loginPtoneUser.getPtId());
      userCompoundMetricsDimensionDto.setModifierId(loginPtoneUser.getPtId());
      userCompoundMetricsDimensionDto
              .setSourceType(UserCompoundMetricsDimension.SOURCE_TYPE_USER_CREATE);
      userCompoundMetricsDimensionDto.setType(PtoneMetricsDimension.TYPE_COMPOUND_METRICS);

      userCompoundMetricsDimensionDto =
              serviceFactory.getPtoneDsService().addUserCompoundMetricsDimension(
                      userCompoundMetricsDimensionDto);

      jsonView.successPack(userCompoundMetricsDimensionDto);
    } catch (Exception e) {
      jsonView.errorPack("add CompoundMetrics error.", e);
    }
    return jsonView;
  }

  /**
   * 修改复合指标
   * @param sid
   * @return
   * @date: 2016年7月14日
   * @author peng.xu
   */
  @RequestMapping(value = "updateCalculatedValue", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView updateCompoundMetrics(
          @RequestBody UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto, @RequestParam(
          value = "sid", required = true) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);
      userCompoundMetricsDimensionDto.setModifierId(loginPtoneUser.getPtId());

      userCompoundMetricsDimensionDto =
              serviceFactory.getPtoneDsService().updateUserCompoundMetricsDimension(
                      userCompoundMetricsDimensionDto);

      jsonView.successPack(userCompoundMetricsDimensionDto);
    } catch (Exception e) {
      jsonView.errorPack("update CompoundMetrics error.", e);
    }
    return jsonView;
  }


  /**
   * 删除复合指标
   */
  @RequestMapping(value = "deleteCalculatedValue/{id}", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView deleteCompoundMetrics(@PathVariable("id") String id, @RequestParam(value = "sid",
          required = true) String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      PtoneUser loginPtoneUser = serviceFactory.getSessionContext().getLoginUser(sid);

      serviceFactory.getPtoneDsService().deleteUserCompoundMetricsDimension(id,
              loginPtoneUser.getPtId());

      jsonView.successPack("delete CompoundMetrics success");
    } catch (Exception e) {
      jsonView.errorPack("delete CompoundMetrics error.", e);
    }
    return jsonView;
  }

  /**
   * 获取空间下、某个数据源下的复合指标列表
   */
  @RequestMapping(value = "calculatedValueList/{spaceId}/{dsId}/{tableId}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView findCompoundMetricsList(@PathVariable("spaceId") String spaceId,
                                          @PathVariable("dsId") String dsId, @PathVariable("tableId") String tableId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<UserCompoundMetricsDimensionDto> list =
              serviceFactory.getPtoneDsService().findUserCompoundMetricsDimensionList(spaceId, dsId,
                      tableId, new String[] {PtoneMetricsDimension.TYPE_COMPOUND_METRICS});
      jsonView.successPack(list);
    } catch (Exception e) {
      jsonView.errorPack("find CompoundMetrics List error.", e);
    }
    return jsonView;
  }

  /**
   * 校验复合指标是否有效
   */
  @RequestMapping(value = "validateCalculatedValue", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView validateCompoundMetrics(
          @RequestBody UserCompoundMetricsDimensionDto userCompoundMetricsDimensionDto) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      boolean isValidate =
              serviceFactory.getPtoneDsService().validateCompoundMetrics(
                      userCompoundMetricsDimensionDto, true);
      jsonView.successPack(isValidate);
    } catch (Exception e) {
      jsonView.errorPack("find CompoundMetrics List error.", e);
    }
    return jsonView;
  }


  /**
   * 获取当前使用复合指标的widget数量
   */
  @RequestMapping(value = "getUseCalculatedValueCount/{id}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getUseCompoundMetricsWidgetCount(@PathVariable("id") String id) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      long count = serviceFactory.getPtoneDsService().getUseCompoundMetricsWidgetCount(id);
      jsonView.successPack(count);
    } catch (Exception e) {
      jsonView.errorPack("get CompoundMetrics Widget Count error.", e);
    }
    return jsonView;
  }


  /**
   * 获取Category列表及子列表.
   * 
   */
  @RequestMapping(value = "categorys/{dsId}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getObjects(@PathVariable("dsId") String dsId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    return jsonView;
  }

  /**
   * 获取指定Category下的指标维度
   * 
   */
  @RequestMapping(value = "metricsAndDimensionsByCategory/{dsId}/{profileId}/{connectionId}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getObjects(@PathVariable("dsId") String dsId,
      @PathVariable("profileId") String profileId,
      @PathVariable("connectionId") String connectionId, @RequestParam("sid") String sid) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    return jsonView;
  }

  /**
   * 远程获取档案列表/账户列表，根据链接ID，获取到auth信息，dsCode
   */
  @RequestMapping(value = "profileApiRemote/{connectionId}/{dsCode}/{email:.+}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView profileApiRemote(@PathVariable("connectionId") String connectionId,
      @PathVariable("email") String email, @PathVariable("dsCode") String dsCode) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    return jsonView;
  }
  
  /**
   * 远程获取档案列表/账户列表，根据链接ID，获取到auth信息
   */
  @RequestMapping(value = "profileApiRemote/{connectionId}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView profileApiRemote(@PathVariable("connectionId") String connectionId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    return jsonView;
  }

  /**
   * 远程获取数据源指标、维度
   */
  @RequestMapping(
      value = "metricsDimensionApiRemote/{connectionId}/{dsCode}/{email}/{profileId}/{type}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getMetricsDimensionApiRemote(@PathVariable("connectionId") String connectionId,
      @PathVariable("dsCode") String dsCode, @PathVariable("email") String email,
      @PathVariable("profileId") String profileId, @PathVariable("type") String type) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    return jsonView;
  }

  /**
   * 获取数据源指定账号下档案列表
   */
  @RequestMapping(value = "profiles/{dsCode}/{connectionId}/{email:.+}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  public @ResponseBody
  JsonView getAccounts(HttpServletRequest request, HttpServletResponse response,
      @PathVariable("dsCode") String dsCode, @PathVariable("connectionId") String connectionId,
      @PathVariable("email") String email, @RequestParam(value = "sid", required = false) String sid) {
    JsonView jsonView = null;
    return jsonView;
  }


  /**
   * 所有数据源获取维度列表接口(返回结构为树形结构的维度列表，需要展示几级返回几级)
   */
  @RequestMapping(value = "dimensions/{dsId}/{connectionId}/{accountName}/{profileId}",
      method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getDimensionList(@PathVariable("dsId") long dsId,
      @PathVariable("connectionId") String connectionId,
      @PathVariable("accountName") String accountName, @PathVariable("profileId") String profileId) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    return jsonView;
  }

}
