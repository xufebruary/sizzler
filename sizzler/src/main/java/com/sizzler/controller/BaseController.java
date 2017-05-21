package com.sizzler.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sizzler.system.ServiceFactory;

/**
 * 基础Controller
 */
@RestController
@Scope("prototype")
@RequestMapping("/api")
public class BaseController {

	protected Logger logger = LoggerFactory.getLogger(BaseController.class);

	@Autowired
	protected ServiceFactory serviceFactory;

}
