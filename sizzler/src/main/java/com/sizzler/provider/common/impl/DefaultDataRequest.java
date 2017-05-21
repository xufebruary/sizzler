package com.sizzler.provider.common.impl;

import org.apache.metamodel.util.CommonQueryRequest;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.DataRequest;

/**
 * Created by ptmind on 2015/12/12.
 */
public class DefaultDataRequest implements DataRequest {

  private UserConnection userConnection;
  private CommonQueryRequest queryRequest;
  private String tableName;

  public DefaultDataRequest(UserConnection userConnection, CommonQueryRequest queryRequest) {
    this.userConnection = userConnection;
    this.queryRequest = queryRequest;
  }

  @Override
  public UserConnection getUserConnection() {
    return this.userConnection;
  }

  @Override
  public CommonQueryRequest getQueryRequest() {
    return queryRequest;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

}
