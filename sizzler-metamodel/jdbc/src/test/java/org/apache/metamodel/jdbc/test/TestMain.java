package org.apache.metamodel.jdbc.test;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.jdbc.JdbcDataContext;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by ptmind on 2015/11/27.
 */
public class TestMain {
  /*
   * private static String MYSQL_DRIVER="com.mysql.jdbc.Driver"; private static String
   * URL_PREFIX="jdbc:mysql://"; private static String HOST="106.3.32.237"; private static String
   * PORT="3310"; private static String USER="TeST"; private static String PASSWORD="SmArtOne0110";
   * private static String DATA_BASE="";
   */

  private static String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
  private static String URL_PREFIX = "jdbc:mysql://";
  private static String HOST = "192.168.1.2";
  private static String PORT = "3306";
  private static String USER = "ptone";
  private static String PASSWORD = "ptone";
  private static String DATA_BASE = "diancan";

  /*
   * private static String MYSQL_DRIVER="org.h2.Driver"; private static String
   * URL_PREFIX="jdbc:h2:mem:"; private static String HOST="192.168.1.2"; private static String
   * PORT="3306"; private static String USER="ptone"; private static String PASSWORD="ptone";
   * private static String DATA_BASE="diancan";
   */

  public static void main(String[] args) {
    testQuery();
    // testMetaData();

  }

  public static void testMetaData() {
    Connection connection = createConnection();
    DataContext dc = new JdbcDataContext(connection);
    Schema[] schemas = dc.getSchemas();
    System.out.println("======schema-list======");
    for (Schema schema : schemas) {
      System.out.println(schema.getName());
      System.out.println("------" + schema.getName() + " table-list------");
      Table[] tables = schema.getTables();
      for (Table table : tables) {
        System.out.println(table.getName());
        Column[] columns = table.getColumns();
        if (table.getName().equals("user")) {
          System.out.println("*****user column-list*****");
          for (Column column : columns) {
            System.out.println("name=" + column.getName() + ",type="
                + column.getType().getSuperType());
          }
        }
      }

    }
  }

  public static void testQuery() {
    String query =
        "select u.uid,p.package_name from userstatus u left join package_base_conf p on u.package_id=p.package_id";
    query = "select username,createdate from user_info where createdate >='2015-11-01'";
    Connection connection = createConnection();
    DataContext dc = new JdbcDataContext(connection);
    DataSet dataSet = dc.executeQuery(query);
    printDataSet(dataSet);

  }

  private static Connection createConnection() {
    Connection connection = null;
    try {
      Class.forName(MYSQL_DRIVER);
      String url = URL_PREFIX + HOST + ":" + PORT + "/" + DATA_BASE;
      connection = DriverManager.getConnection(url, USER, PASSWORD);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return connection;
  }

  public static void printDataSet(DataSet dataSet) {
    while (dataSet.next()) {
      Row row = dataSet.getRow();
      for (Object o : row.getValues()) {
        System.out.print(o + ",");
      }
      System.out.println();
    }

  }
}
