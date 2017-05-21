package com.sizzler.proxy.model.model;

import java.io.Serializable;

import org.apache.metamodel.schema.MutableSchema;

import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.dto.PtoneMetricsDimension;
import com.sizzler.proxy.common.model.CommonQueryParam;

public class ModelQueryParam extends CommonQueryParam implements Serializable {

  private static final long serialVersionUID = 2574001068273408367L;
  private String fileId;
  private String tableId;
  private String tableName;
  private String tableCode;
  private PtoneMetricsDimension dateDimension;

  private UserConnectionSource source;
  private String hdfsPath;
  private MutableSchema schema;

  // /////////////////////////////////////////////////////////

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public String getTableId() {
    return tableId;
  }

  public void setTableId(String tableId) {
    this.tableId = tableId;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getTableCode() {
    return tableCode;
  }

  public void setTableCode(String tableCode) {
    this.tableCode = tableCode;
  }

  public PtoneMetricsDimension getDateDimension() {
    return dateDimension;
  }

  public void setDateDimension(PtoneMetricsDimension dateDimension) {
    this.dateDimension = dateDimension;
  }

  public UserConnectionSource getSource() {
    return source;
  }

  public void setSource(UserConnectionSource source) {
    this.source = source;
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
