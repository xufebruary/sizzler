package com.sizzler.provider.common;

import java.io.Serializable;

/**
 * 用于响应数据源的配置请求
 */
public interface MetaResponse extends Serializable {

  public String getContent();
}
