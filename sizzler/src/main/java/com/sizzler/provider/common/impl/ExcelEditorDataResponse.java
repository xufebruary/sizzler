package com.sizzler.provider.common.impl;

import org.apache.metamodel.schema.MutableSchema;

/**
 * 用于统一Upload、S3、GD等文件数据源的返回对象
 * @author you.zou
 * @date 2016年11月4日 下午12:10:42
 */
public class ExcelEditorDataResponse extends DefaultEditorDataResponse {

  private static final long serialVersionUID = 7879401497769602002L;
  // 该schema是最新的，比如每个table的行数已经更新了，新增加了某些schema，删除了某些schema
  private MutableSchema schema;

  public MutableSchema getSchema() {
    return schema;
  }

  public void setSchema(MutableSchema schema) {
    this.schema = schema;
  }
}
