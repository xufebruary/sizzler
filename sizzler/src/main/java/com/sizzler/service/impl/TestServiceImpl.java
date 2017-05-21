package com.sizzler.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.restful.JsonView;
import com.sizzler.common.restful.JsonViewFactory;
import com.sizzler.service.TestService;
import com.sizzler.system.ServiceFactory;

@Service("testService")
public class TestServiceImpl implements TestService {

  private Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);

  @Autowired
  private ServiceFactory serviceFactory;

  @Override
  public JsonView testService() {

    JsonView jsonView = JsonViewFactory.createJsonView();
    
    logger.info(" >>>>>>>>>>>> TestService <<<<<<<<<<<<<<<<<<<");

    return jsonView;
  }


}
