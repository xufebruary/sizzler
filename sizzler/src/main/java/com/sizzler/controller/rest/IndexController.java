package com.sizzler.controller.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by li.zhang on 2015/3/19.
 */
@Controller
public class IndexController {

  @RequestMapping("/index")
  public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
    return new ModelAndView("index");
  }

}
