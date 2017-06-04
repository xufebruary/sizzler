package com.sizzler.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sizzler.common.MediaType;
import com.sizzler.common.log.CollectLogUtil;
import com.sizzler.service.collect.CollectService;
import com.sizzler.system.Constants;
import com.sizzler.system.api.annotation.ApiVersion;

@RestController("collectController")
@RequestMapping("{version}/collect")
@Scope("prototype")
@ApiVersion(Constants.API_VERSION_1)
public class CollectController extends BaseController {

  private static final Logger log = Logger.getLogger(CollectController.class);

  @Autowired
  private CollectService collectService;

  @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
  public Map<String, Object> collectLog(@RequestBody Object object,
      @RequestParam(value = "type", required = false) String type) {

    boolean result = this.parseCollectInfo(object);

    Map<String, Object> response = new HashMap<String, Object>();
    response.put("_RejCode", result ? "000000" : "999999");
    response.put("ReturnMsg", result ? "success" : "error");

    return response;
  }

  /**
   * 解析采集数据
   */
  private boolean parseCollectInfo(Object object) {
    boolean result = true;
    String jsonStr = JSON.toJSONString(object);

    try {
      JSONObject jsonObj = JSON.parseObject(jsonStr);
      if (jsonObj != null) {
        Map<String, String> baseInfoMap = new HashMap<String, String>();
        if (jsonObj.containsKey("basicData")) {
          JSONObject basicData = jsonObj.getJSONObject("basicData");
          for (Map.Entry<String, Object> entry : basicData.entrySet()) {
            baseInfoMap.put(entry.getKey(), String.valueOf(entry.getValue()));
          }
        }

        if (jsonObj.containsKey("recordData")) {
          JSONArray recordData = jsonObj.getJSONArray("recordData");
          for (int i = 0; i < recordData.size(); i++) {
            JSONObject rData = recordData.getJSONObject(i);
            Map<String, String> infoMap = new HashMap<String, String>();
            infoMap.putAll(baseInfoMap);
            for (Map.Entry<String, Object> entry : rData.entrySet()) {
              infoMap.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
            collectService.saveAppCollectInfo(infoMap);
          }
        } else {
          collectService.saveAppCollectInfo(baseInfoMap);
        }

        // 保存采集信息
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      result = false;
    }

    // 打印采集到的日志到日志文件
    CollectLogUtil.info(jsonStr + "  ===>  " + result);
    return result;
  }

}
