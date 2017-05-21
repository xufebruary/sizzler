package com.sizzler.controller.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sizzler.common.MediaType;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.system.ServiceFactory;

@Controller
@Scope("prototype")
@RequestMapping("/sys")
public class SysController {

  @Autowired
  private ServiceFactory serviceFactory;

  @RequestMapping(value = "dataVersion", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView getAllDataVersion() {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      Map<String, String> dataVersion = serviceFactory.getSysService().getAllDataVersion();
      jsonView.successPack(dataVersion);
    } catch (Exception e) {
      jsonView.errorPack(" get data version error.", e);
    }
    return jsonView;
  }

  @RequestMapping(value = "refreshSysCache", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView refreshSysCache() {
    JsonView jsonView = JsonViewFactory.createJsonView();
    try {
      String refreshHost = serviceFactory.getSysService().refreshMemeryCache();
      jsonView.messagePack("refresh success : " + refreshHost);
    } catch (Exception e) {
      jsonView.errorPack(" refresh Sys Cache.", e);
    }
    return jsonView;
  }

}
