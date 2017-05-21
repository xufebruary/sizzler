package com.sizzler.provider.common;

import java.io.Serializable;

import com.sizzler.common.sizzler.UserConnection;

/**
 * Created by ptmind on 2015/12/7. 用于查询某个数据源的配置所需要的请求信息
 */
public interface MetaRequest extends Serializable {
  public UserConnection getUserConnection();
}
