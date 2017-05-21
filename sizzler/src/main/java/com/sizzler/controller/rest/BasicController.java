package com.sizzler.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sizzler.common.MediaType;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.domain.basic.dto.PtoneBasicChartInfoDto;
import com.sizzler.system.ServiceFactory;

@Controller
@Scope("prototype")
@RequestMapping("/basic")
public class BasicController {

  @Autowired
  private ServiceFactory serviceFactory;

  @RequestMapping(value = "charts/owns/{type}", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getGraphs(@PathVariable("type") String type) {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      List<PtoneBasicChartInfoDto> graphList =
          serviceFactory.getPtoneBasicService().getPtoneBasicChartInfoListByType(type);
      jsonView.successPack(graphList);
    } catch (Exception e) {
      jsonView.errorPack(" query charts list error : type --> " + type, e);
    }
    return jsonView;
  }

  @RequestMapping(value = "charts/map", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public List<PtoneBasicChartInfoDto> getGhartsMap() {
    JsonView jsonView = JsonViewFactory.createJsonView();
    List<PtoneBasicChartInfoDto> list = null;
    try {
      list = serviceFactory.getPtoneBasicService().getPtoneBasicChartInfoList();
      jsonView.successPack(list);
    } catch (Exception e) {
      jsonView.errorPack(" query charts map error.", e);
    }
    return list;
  }
}
