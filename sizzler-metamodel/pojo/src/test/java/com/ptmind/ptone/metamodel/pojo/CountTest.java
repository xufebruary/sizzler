package com.ptmind.ptone.metamodel.pojo;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.util.SimpleTableDef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ptmind on 2015/11/26.
 */
public class CountTest {

  public static void main(String[] args) {
    testArrayTableDataProvider();
  }

  public static void testArrayTableDataProvider() {
    String tableName = "student";
    String[] columns = new String[] {"id", "name", "birthday", "ege"};
    ColumnType[] columnTypes =
        new ColumnType[] {ColumnType.STRING, ColumnType.STRING, ColumnType.DATE, ColumnType.INTEGER};
    SimpleTableDef tableDef = new SimpleTableDef(tableName, columns);

    List<Object[]> rowList = new ArrayList<>();

    List row1 = new ArrayList();
    row1.add("10");
    row1.add("wangwu");
    row1.add("2015-01-01");
    row1.add(null);
    rowList.add(row1.toArray());

    List row2 = new ArrayList();
    row2.add("50");
    row2.add("lisi");
    row2.add("2015-01-02");
    row2.add(30);
    rowList.add(row2.toArray());

    List row3 = new ArrayList();
    row3.add("102");
    row3.add("wangwu");
    row3.add("2015-01-03");
    row3.add("null");
    rowList.add(row3.toArray());

    List row4 = new ArrayList();
    row4.add("105");
    row4.add("sssssss");
    row4.add("2015-01-04");
    row4.add("");
    rowList.add(row4.toArray());

    ArrayTableDataProvider arrayTableDataProvider = new ArrayTableDataProvider(tableDef, rowList);

    DataContext studentContext = new PojoDataContext("ptone", arrayTableDataProvider);
    // String query="select id from student order by id desc limit 2";
    // query="select count(*) from  student";
    // query="select action:id,name,birthday  from student where birthday in ('2015-01-01','2015-01-02') ";
    // String queryAll = "select id, name from student";
    String query = "select  count(ege),countunique(ege) from student";
    // DataSet dataSetAll=studentContext.executeQuery(queryAll);
    // printDataSet(dataSetAll, "all");
    // System.out.println("===================================================");
    DataSet dataSet = studentContext.executeQuery(query);
    printDataSet(dataSet, "countunique");
    System.out.println("===================================================");
  }

  public static void printDataSet(DataSet dataSet, String name) {
    while (dataSet.next()) {
      Row row = dataSet.getRow();
      for (Object o : row.getValues()) {
        System.out.print(name + ":::" + o + ",");
      }
      System.out.println();
    }

  }
}
