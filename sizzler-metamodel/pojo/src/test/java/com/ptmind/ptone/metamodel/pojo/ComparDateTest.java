package com.ptmind.ptone.metamodel.pojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.query.FilterItem;
import org.apache.metamodel.query.FromItem;
import org.apache.metamodel.query.FunctionType;
import org.apache.metamodel.query.Query;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.ColumnType;
import org.apache.metamodel.schema.ImmutableColumn;
import org.apache.metamodel.schema.MutableColumn;
import org.apache.metamodel.schema.MutableTable;
import org.apache.metamodel.schema.Table;
import org.apache.metamodel.util.SimpleTableDef;

public class ComparDateTest {

  public static void main(String[] args) throws ParseException {

    String tableName = "student";
    String[] columns = new String[] {"id", "name", "birthday", "ege"};
    ColumnType[] columnTypes =
        new ColumnType[] {ColumnType.STRING, ColumnType.STRING, ColumnType.DATE, ColumnType.INTEGER};
    SimpleTableDef tableDef = new SimpleTableDef(tableName, columns, columnTypes);

    List<Object[]> rowList = new ArrayList<>();
    // 指定一个日期
    // 对 calendar 设置为 date 所定的日期
    List<Object> row1 = new ArrayList<Object>();
    row1.add("10");
    row1.add("wangwu");
    row1.add("2015-01-01 15:10:16");
    row1.add(87);
    rowList.add(row1.toArray());

    List<Object> row2 = new ArrayList<Object>();
    row2.add("50");
    row2.add("lisi");
    row2.add("2015-01-02 15:10:16");
    row2.add(0);
    rowList.add(row2.toArray());

    List<Object> row3 = new ArrayList<Object>();
    row3.add("102");
    row3.add("wangwu");
    row3.add("2015-01-03");
    row3.add(385);
    rowList.add(row3.toArray());

    List<Object> row4 = new ArrayList<Object>();
    row4.add("10");
    row4.add("wangwu");
    row4.add("2015-01-01");
    row4.add(87);
    rowList.add(row4.toArray());

    List<Object> row5 = new ArrayList<Object>();
    row5.add("10");
    row5.add("wangwu");
    row5.add("2015 01 02");
    row5.add(87);
    rowList.add(row5.toArray());

    List<Object> row6 = new ArrayList<Object>();
    row6.add("10");
    row6.add("wangwu");
    row6.add("2015.01");
    row6.add(87);
    rowList.add(row6.toArray());

    ArrayTableDataProvider arrayTableDataProvider = new ArrayTableDataProvider(tableDef, rowList);
    DataContext studentContext = new PojoDataContext("ptone", arrayTableDataProvider);
    String query = "select birthday from student where birthday = '2015/01/01'";
    DataSet dataSet = studentContext.executeQuery(query);
    printDataSet(dataSet, "2015/01/01");
    System.out.println("===================================================");
    String query2 = "select birthday from student where birthday <> '2015-01-01'";
    DataSet dataSet2 = studentContext.executeQuery(query2);
    printDataSet(dataSet2, "2015-01-01");
    System.out.println("===================================================");
    String query3 = "select birthday from student where birthday <> '2015.01.01'";
    DataSet dataSet3 = studentContext.executeQuery(query3);
    printDataSet(dataSet3, "2015.01.01");
    System.out.println("===================================================");
    String query4 = "select birthday from student where birthday <> '20150101'";
    DataSet dataSet4 = studentContext.executeQuery(query4);
    printDataSet(dataSet4, "20150101");
    System.out.println("===================================================");
    String query5 = "select birthday from student where birthday = '2015 01 02'";
    DataSet dataSet5 = studentContext.executeQuery(query5);
    printDataSet(dataSet5, "2015 01 02");
    System.out.println("===================================================");
    String query6 = "select birthday from student where birthday <> '2015/01/01 15:10:16'";
    DataSet dataSet6 = studentContext.executeQuery(query6);
    printDataSet(dataSet6, "2015/01/01 15:10:16");
    System.out.println("===================================================");
    String query7 = "select birthday from student where birthday <> '2015-01-01 15:10:16'";
    DataSet dataSet7 = studentContext.executeQuery(query7);
    printDataSet(dataSet7, "2015-01-01 15:10:16");
    System.out.println("===================================================");
    String query8 = "select birthday from student where birthday = '2015.01'";
    DataSet dataSet8 = studentContext.executeQuery(query8);
    printDataSet(dataSet8, "2015/01/01");
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
