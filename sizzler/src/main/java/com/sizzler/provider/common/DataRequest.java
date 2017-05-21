package com.sizzler.provider.common;

import org.apache.metamodel.util.CommonQueryRequest;

import com.sizzler.common.sizzler.UserConnection;

/**
 * Created by ptmind on 2015/10/22. 数据查询请求 的接口，widget数据获取时，需要指定该接口
 *
 * 查询请求主要包括两部分： （1）UserConnection：连接数据源所需要的信息 （2）请求的内容：指标、维度、过滤等
 */
public interface DataRequest {
  // 需要加上 UserConnection对象
  // public UserConnection getUserConnection();
  public UserConnection getUserConnection();

  public CommonQueryRequest getQueryRequest();
}
