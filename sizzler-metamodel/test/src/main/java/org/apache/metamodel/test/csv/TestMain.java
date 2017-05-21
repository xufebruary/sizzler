package org.apache.metamodel.test.csv;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.csv.CsvDataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.test.TestUtil;
import org.apache.metamodel.util.HdfsResource;

/**
 * Created by ptmind on 2015/11/28.
 */
public class TestMain {
  private static String hdfsPath = "hdfs://192.168.18.73:8020";

  public static void main(String[] args) {
    testCsv();
  }

  public static void testCsv() {
    String destinationPath = "/ptone/1002/upload/test_1.csv";

    HdfsResource hdfsResource = new HdfsResource(hdfsPath + destinationPath);

    DataContext dataContext = new CsvDataContext(hdfsResource);

    TestUtil.printSchema(dataContext);
    String query = "select * from test_1";
    DataSet dataSet = dataContext.executeQuery(query);
    TestUtil.printDataSet(dataSet);
  }


}
