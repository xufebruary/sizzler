package org.apache.metamodel.test.excel;


import org.apache.metamodel.DataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.data.Row;
import org.apache.metamodel.excel.ExcelConfiguration;
import org.apache.metamodel.excel.ExcelDataContext;
import org.apache.metamodel.util.HdfsResource;

/**
 * Created by ptmind on 2015/11/27.
 */
public class TestMain {
  private static String hdfsPath = "hdfs://192.168.18.73:8020";

  public static void main(String[] args) throws Exception {

    // ExcelConfiguration configuration=new ExcelConfiguration();
    // DataContext dataContext=new ExcelDataContext(urlResource,configuration);
    // System.out.println(dataContext.getSchemas());

    String destinationPath = "/ptone/1002/upload/zx_2015_10_26_2.xlsx";

    HdfsResource hdfsResource = new HdfsResource(hdfsPath + destinationPath);
    // ExcelTool.convertExcelToPtoneFile(hdfsResource.read());

    ExcelConfiguration configuration = new ExcelConfiguration();
    DataContext dataContext = new ExcelDataContext(hdfsResource, configuration);
    // 0个是系统自带的infomation_schema
    /*
     * Schema schema=dataContext.getSchemas()[1]; System.out.println("schema="+schema.getName());
     * for(Table table:schema.getTables()) { System.out.println("table="+table.getName());
     * for(Column column:table.getColumns()) {
     * System.out.println("column="+column.getName()+",type="+column.getType().getSuperType()); } }
     */

    String query = "select max(name) as sum_id from sheet1";
    DataSet dataSet = dataContext.executeQuery(query);
    printDataSet(dataSet);

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
