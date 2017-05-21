package com.sizzler.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sizzler.common.MediaType;
import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.service.TestService;
import com.sizzler.system.ServiceFactory;

/**
 * Created by ptmind on 2015/12/9.
 */

@Controller
@Scope("prototype")
@RequestMapping("/test")
public class TestController {

  private Logger logger = LoggerFactory.getLogger(TestController.class);

//  private MetaProvider metaProvider;

  @Autowired
  private ServiceFactory serviceFactory;

  @Autowired
  private TestService testService;


  @RequestMapping(value = "testService", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public JsonView testService() {

    JsonView jsonView = JsonViewFactory.createJsonView();

    try {

      jsonView = testService.testService();

    } catch (Exception e) {
      jsonView.errorPack("error", e);
    }

    return jsonView;
  }

 
}
