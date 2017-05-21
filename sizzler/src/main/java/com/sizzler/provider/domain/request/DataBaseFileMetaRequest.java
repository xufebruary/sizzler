package com.sizzler.provider.domain.request;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.impl.DefaultMetaRequest;

public class DataBaseFileMetaRequest extends DefaultMetaRequest {

  private static final long serialVersionUID = -7395882761783935579L;

  private String tableName;
  private String databaseName;

  public DataBaseFileMetaRequest(UserConnection userConnection) {
    super(userConnection);
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

}
