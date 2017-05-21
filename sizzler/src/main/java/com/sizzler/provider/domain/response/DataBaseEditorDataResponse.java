package com.sizzler.provider.domain.response;

import org.apache.metamodel.schema.MutableSchema;

import com.sizzler.provider.common.impl.DefaultEditorDataResponse;

public class DataBaseEditorDataResponse extends DefaultEditorDataResponse {

  private static final long serialVersionUID = 2359932431653485808L;

  // 最新的总行数
  private long rowCount;

  private MutableSchema schema;

  public MutableSchema getSchema() {
    return schema;
  }

  public void setSchema(MutableSchema schema) {
    this.schema = schema;
  }

  public long getRowCount() {
    return rowCount;
  }

  public void setRowCount(long rowCount) {
    this.rowCount = rowCount;
  }
}
