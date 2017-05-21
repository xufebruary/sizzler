package com.sizzler.domain.ds.dto;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import com.sizzler.common.sizzler.UserConnection;

/**
 * 授权账号类型UserConnection信息前端对应实体类
 */
public class UiAccountConnection extends UserConnection implements Serializable {

  private static final long serialVersionUID = 38288261330219002L;

  private JSONObject configObject;

  public JSONObject getConfigObject() {
    return configObject;
  }

  public void setConfigObject(JSONObject configObject) {
    this.configObject = configObject;
  }

}
