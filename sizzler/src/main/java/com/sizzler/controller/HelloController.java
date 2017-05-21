package com.sizzler.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sizzler.common.MediaType;
import com.sizzler.system.api.annotation.ApiVersion;
import com.sizzler.system.api.common.ResponseResult;
import com.sizzler.system.api.common.RestResultGenerator;

@RestController
@RequestMapping("{version}/h")
@ApiVersion(1)
public class HelloController extends BaseController {

  @RequestMapping(value = "hello", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public ResponseResult<String> hello(HttpServletRequest request) {
    System.out.println("haha1..........");
    return RestResultGenerator.genResult("hello1.........", "hello1.........");
  }

  @RequestMapping(value = "hello", headers = "date-version=20161229", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
  @ResponseBody
  public ResponseResult<String> hello20161229(HttpServletRequest request) {
    System.out.println("haha20161229.........");
    return RestResultGenerator.genResult("hello20161229.........", "hello20161229.........");
  }

}
