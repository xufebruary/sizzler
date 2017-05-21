package org.apache.metamodel.test;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;

/**
 * Created by ptmind on 2015/11/28.
 */
public class TestUtil {

  public static void printDataSet(DataSet dataSet) {
    dataSet.getSelectItems();
    while (dataSet.next()) {
      Row row = dataSet.getRow();
      for (Object o : row.getValues()) {
        System.out.print(o + ",");
      }
      System.out.println();
    }

  }

  public static void printSchema(DataContext dataContext) {
    Schema schema = dataContext.getSchemas()[1];
    System.out.println("schema=" + schema.getName());
    for (Table table : schema.getTables()) {
      System.out.println("table=" + table.getName());
      for (Column column : table.getColumns()) {
        System.out.println("column=" + column.getName() + ",type="
            + column.getType().getSuperType());
      }
    }
  }
}
