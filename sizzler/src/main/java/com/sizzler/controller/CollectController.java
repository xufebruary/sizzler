package com.sizzler.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sizzler.common.MediaType;
import com.sizzler.common.log.CollectLogUtil;
import com.sizzler.system.Constants;
import com.sizzler.system.api.annotation.ApiVersion;

@RestController("collectController")
@RequestMapping("{version}/collect")
@Scope("prototype")
@ApiVersion(Constants.API_VERSION_1)
public class CollectController extends BaseController {

  @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void collectLog(@RequestBody JSONObject jsonObj,
      @RequestParam(value = "type", required = false) String type) {

    // 打印采集到的日志到日志文件
    CollectLogUtil.info(JSON.toJSONString(jsonObj));
  }

}
