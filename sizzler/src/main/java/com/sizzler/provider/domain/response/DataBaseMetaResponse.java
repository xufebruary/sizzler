package com.sizzler.provider.domain.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.sizzler.MetaContentNode;
import com.sizzler.provider.common.MetaResponse;

public class DataBaseMetaResponse implements MetaResponse {

  private static final long serialVersionUID = 950457130152986338L;

  private List<MutableSchema> schemaList;

  public List<MutableSchema> getSchemaList() {
    return schemaList;
  }

  public void setSchemaList(List<MutableSchema> schemaList) {
    this.schemaList = schemaList;
  }

  @Override
  public String getContent() {
    String contentJson = "";
    List<MetaContentNode> metaContentNodeList = new ArrayList<>();
    if (schemaList != null && schemaList.size() > 0) {
      for (MutableSchema schema : schemaList) {
        MetaContentNode dataBaseNode = new MetaContentNode();
        dataBaseNode.setName(schema.getName());
        if (schema.getTables() != null && schema.getTables().length > 0) {
          dataBaseNode.setLeaf(false);
          List<MetaContentNode> tableChild = new ArrayList<>();
          dataBaseNode.setChild(tableChild);

          for (MutableTable table : schema.getTables()) {
            MetaContentNode tableNode = new MetaContentNode();
            tableNode.setName(table.getName());
            if (table.getColumnCount() > 0) {
              tableNode.setLeaf(false);
              List<MetaContentNode> columnChild = new ArrayList<>();
              tableNode.setChild(columnChild);
              for (Column column : table.getColumns()) {
                MetaContentNode columnNode = new MetaContentNode();
                columnNode.setName(column.getName());
                columnNode.setLeaf(true);
                columnChild.add(columnNode);
              }

            } else {
              tableNode.setLeaf(true);
            }
            tableChild.add(tableNode);
          }
        } else {
          dataBaseNode.setLeaf(true);
        }
        metaContentNodeList.add(dataBaseNode);
      }
    }
    contentJson = JSON.toJSONString(metaContentNodeList);
    return contentJson;
  }
}
