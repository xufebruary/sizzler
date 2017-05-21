package com.sizzler.provider.domain.request;

import org.apache.metamodel.util.CommonQueryRequest;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.impl.DefaultDataRequest;

public class DataBaseDataRequest extends DefaultDataRequest {

  private String databaseName;

  public DataBaseDataRequest(UserConnection userConnection, CommonQueryRequest queryRequest) {
    super(userConnection, queryRequest);
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

}
