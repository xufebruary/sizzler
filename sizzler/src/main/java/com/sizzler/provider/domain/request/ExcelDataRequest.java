package com.sizzler.provider.domain.request;

import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.util.CommonQueryRequest;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.impl.DefaultDataRequest;

public class ExcelDataRequest extends DefaultDataRequest {

  private String hdfsPath;
  private MutableSchema schema;

  public ExcelDataRequest(UserConnection userConnection, CommonQueryRequest queryRequest) {
    super(userConnection, queryRequest);
  }

  public String getHdfsPath() {
    return hdfsPath;
  }

  public void setHdfsPath(String hdfsPath) {
    this.hdfsPath = hdfsPath;
  }

  public MutableSchema getSchema() {
    return schema;
  }

  public void setSchema(MutableSchema schema) {
    this.schema = schema;
  }
}
