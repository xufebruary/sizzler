package com.sizzler.metamodel.pojo;

import java.util.ArrayList;
import java.util.List;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.util.SimpleTableDef;

public class TestMain {

  public static void main(String[] args) {
    testArrayTableDataProvider();
  }

  public static void testArrayTableDataProvider() {
    String tableName = "student";
    String[] columns = new String[] {"id", "name", "birthday"};
    ColumnType[] columnTypes =
        new ColumnType[] {ColumnType.STRING, ColumnType.STRING, ColumnType.DATE};
    SimpleTableDef tableDef = new SimpleTableDef(tableName, columns);

    List<Object[]> rowList = new ArrayList<>();

    List row1 = new ArrayList();
    row1.add("10");
    row1.add("zhangsan");
    row1.add("2015-01-01");
    rowList.add(row1.toArray());

    List row2 = new ArrayList();
    row2.add("50");
    row2.add("lisi");
    row2.add("2015-01-02");
    rowList.add(row2.toArray());

    List row3 = new ArrayList();
    row3.add("102");
    row3.add("wangwu");
    row3.add("2015-01-03");
    rowList.add(row3.toArray());

    ArrayTableDataProvider arrayTableDataProvider = new ArrayTableDataProvider(tableDef, rowList);

    DataContext studentContext = new PojoDataContext("ptone", arrayTableDataProvider);
    // String query="select id from student order by id desc limit 2";
    // query="select count(*) from  student";
    // query="select action:id,name,birthday  from student where birthday in ('2015-01-01','2015-01-02') ";
    String queryAll = "select id, name from student";
    String query =
        "select name,birthday from student where id  not_like '%10%' and id  not_like '%li%'";
    String query1 =
        "select name,birthday from student where name start_like '%w%' or name start_like '%l%'";
    String query2 = "select name,birthday from student where name end_like '%si%'";
    String query3 = "select name,birthday from student where name not_start_like '%w%'";
    String query4 = "select name,birthday from student where name not_end_like '%si%'";
    DataSet dataSetAll = studentContext.executeQuery(queryAll);
    printDataSet(dataSetAll, "all");
    System.out.println("===================================================");
    DataSet dataSet = studentContext.executeQuery(query);
    printDataSet(dataSet, "not_like ");
    System.out.println("===================================================");
    DataSet dataSet1 = studentContext.executeQuery(query1);
    printDataSet(dataSet1, "start_like ");
    System.out.println("===================================================");
    DataSet dataSet2 = studentContext.executeQuery(query2);
    printDataSet(dataSet2, "end_like ");
    System.out.println("===================================================");
    DataSet dataSet3 = studentContext.executeQuery(query3);
    printDataSet(dataSet3, "not_start_like ");
    System.out.println("===================================================");
    DataSet dataSet4 = studentContext.executeQuery(query4);
    printDataSet(dataSet4, "not_end_like ");
    System.out.println("===================================================");

    String q = "select name,birthday from student where birthday >= '2015-01-02'";
    DataSet dataSet5 = studentContext.executeQuery(q);
    printDataSet(dataSet5, ">= ");
    System.out.println("===================================================");
    String q1 = "select name,birthday from student where birthday <= '2015-01-02'";
    DataSet dataSet6 = studentContext.executeQuery(q1);
    printDataSet(dataSet6, "<= ");
    System.out.println("===================================================");
    String q2 = "select name,birthday from student where birthday = '2015-01-02'";
    DataSet dataSet7 = studentContext.executeQuery(q2);
    printDataSet(dataSet7, ">= ");
    System.out.println("===================================================");
    String q3 = "select name,birthday from student where birthday <> '2015-01-02'";
    DataSet dataSet8 = studentContext.executeQuery(q3);
    printDataSet(dataSet8, "<> ");
    System.out.println("===================================================");
    String q4 = "select name,birthday from student where birthday > '2015-01-02'";
    DataSet dataSet9 = studentContext.executeQuery(q4);
    printDataSet(dataSet9, "> ");
    System.out.println("===================================================");
    String q5 = "select name,birthday from student where birthday < '2015-01-02'";
    DataSet dataSet10 = studentContext.executeQuery(q5);
    printDataSet(dataSet10, "< ");
    System.out.println("===================================================");
    String q6 = "select name,birthday from student where birthday in ('2015-01-01','2015-01-02')";
    DataSet dataSet11 = studentContext.executeQuery(q6);
    printDataSet(dataSet11, "<> ");
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
