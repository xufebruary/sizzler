package com.sizzler.provider.domain.request;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.impl.DefaultEditorDataRequest;

public class DataBaseEditorDataRequest extends DefaultEditorDataRequest {

  private static final long serialVersionUID = 2682508175545795355L;

  private String tableName;
  private String databaseName;

  public DataBaseEditorDataRequest(UserConnection userConnection) {
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
