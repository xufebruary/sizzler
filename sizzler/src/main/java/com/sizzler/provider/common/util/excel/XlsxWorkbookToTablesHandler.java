package com.sizzler.provider.common.util.excel;

import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.schema.TableType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;

/**
 * Created by xin.zhang on 2016/6/24.
 */
public class XlsxWorkbookToTablesHandler extends DefaultHandler {
  private MutableSchema schema;
  private Map<String, String> sheetNameToInternalId;

  public XlsxWorkbookToTablesHandler(MutableSchema schema, Map<String, String> sheetNameToInternalId) {
    this.schema = schema;
    this.sheetNameToInternalId = sheetNameToInternalId;

  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    if ("sheet".equals(qName)) {
      String state = attributes.getValue("state");
      if (state != null && state.equalsIgnoreCase("hidden")) {
        return;
      }
      String sheetName = attributes.getValue("name");
      String relationId = attributes.getValue("r:id");
      sheetNameToInternalId.put(sheetName, relationId);
      if (schema != null) {
        MutableTable mutableTable = new MutableTable(sheetName, TableType.TABLE, schema);
        schema.addTable(mutableTable);
      }
    }

  }
}
