package com.sizzler.provider.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.MutableSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.provider.common.file.PtoneFile;

public class PtoneFileUtil {

  private static final Logger log = LoggerFactory.getLogger(PtoneFileUtil.class);

  public static String CurrencyReplaceRegex = "[\\$€¥￥円元(USD)(EUR)(JPY)(JP)(RMB)]";
  // 千位分隔符
  public static String NumberReplaceRegex = ",";
  public static String PercentReplaceRegex = "%";
  public static String DurationReplaceRegex = "(s|S|h|H|m|M)";

  public static boolean determineIfFileHasChanged(Long remoteLastModifiedDate,
      Long localLastModifiedDate) {
    boolean fileChanged = false;
    if (remoteLastModifiedDate == null && localLastModifiedDate == null) {
      fileChanged = false;
    } else if ((localLastModifiedDate == null && remoteLastModifiedDate != null)
        || (localLastModifiedDate != null && remoteLastModifiedDate == null)
        || (!localLastModifiedDate.equals(remoteLastModifiedDate))) {
      fileChanged = true;
    }
    return fileChanged;
  }

  public static PtoneFile generateDataFile(boolean isGoogleDrive, PtoneFile ptoneFile,
      MutableSchema schema, Map<String, Integer> ignoreRowStartMap,
      Map<String, Integer> ignoreRowEndMap) {
    PtoneFile dataPtoneFile =
        skipRow(isGoogleDrive, ptoneFile, schema, ignoreRowStartMap, ignoreRowEndMap);
    String fileId = dataPtoneFile.getId();
    String dataFileId = fileId + "_" + schema.getId() + "_data";
    dataPtoneFile.setId(dataFileId);
    dataPtoneFile.setSchema(ptoneFile.getSchema());
    return dataPtoneFile;
  }

  public static PtoneFile generateDataFile(PtoneFile ptoneFile, MutableSchema schema,
      Map<String, Integer> ignoreRowStartMap, Map<String, Integer> ignoreRowEndMap) {
    return generateDataFile(false, ptoneFile, schema, ignoreRowStartMap, ignoreRowEndMap);
  }

  public static PtoneFile generateDataFile(PtoneFile ptoneFile, MutableSchema schema,
      Map<String, Integer[]> skipRowArrayMap, Map<String, Integer[]> skipColArrayMap,
      Map<String, Integer> titleRowMap) {
    PtoneFile dataPtoneFile =
        skipRowColTitle(ptoneFile, skipRowArrayMap, skipColArrayMap, titleRowMap);
    String fileId = dataPtoneFile.getId();
    String dataFileId = fileId + "_" + schema.getId() + "_data";
    dataPtoneFile.setId(dataFileId);
    dataPtoneFile.setSchema(ptoneFile.getSchema());
    return dataPtoneFile;
  }

  public static PtoneFile generateEditFile(PtoneFile ptoneFile) throws Exception {
    PtoneFile limitPtoneFile = limitPtoneFile(ptoneFile);
    String editDataFileId = limitPtoneFile.getId() + "_edit";
    limitPtoneFile.setId(editDataFileId);
    return limitPtoneFile;
  }

  private static PtoneFile skipRow(boolean isGoogleDrive, PtoneFile ptoneFile,
      MutableSchema schema, Map<String, Integer> ignoreRowStartMap,
      Map<String, Integer> ignoreRowEndMap) {
    PtoneFile dataPtoneFile = new PtoneFile();

    dataPtoneFile.setId(ptoneFile.getId());
    dataPtoneFile.setName(ptoneFile.getName());
    LinkedHashMap<String, List<List>> fileListDataMap = ptoneFile.getFileListDataMap();
    LinkedHashMap<String, List<List>> newFileListDataMap = new LinkedHashMap<>();

    for (Map.Entry<String, List<List>> tmpEntry : fileListDataMap.entrySet()) {
      newFileListDataMap.put(tmpEntry.getKey(), tmpEntry.getValue());
    }

    // Map<String,Integer[]> skipRowArrayMap=new HashMap<>();

    Map<String, List<Integer>> skipRowListMap = new HashMap<>();

    if (ignoreRowStartMap != null) {
      for (Map.Entry<String, Integer> entry : ignoreRowStartMap.entrySet()) {
        String sheetName = entry.getKey();

        // 现在csv上传的直接存储原始csv文件，不存储为excel文件，解析时直接解析csv文件，使用文件名作为key不存在excel的sheet的名字的最长长度为31问题 add by
        // xupeng 20160818
        // //对于GoogleDrive不进行文件名的截断处理
        // if(!isGoogleDrive)
        // {
        // if (sheetName.length() > 31) //
        // excel的sheet的名字的最长长度为31，而当以csv的文件名为sheet名时，文件名很容易超过31哥，所以需要进行此处理
        // {
        // sheetName = sheetName.substring(0, 31);
        // }
        // }


        Integer ignoreRowStart = entry.getValue();
        log.info("sheetName:"+sheetName+",ignoreRowStart="+ignoreRowStart);
        List<Integer> ignoreRowStartList = new ArrayList<>();

        for (int s = 0; s < ignoreRowStart; s++) {
          ignoreRowStartList.add(s);
        }
        skipRowListMap.put(sheetName, ignoreRowStartList);
      }
    }

    if (ignoreRowEndMap != null) {
      for (Map.Entry<String, Integer> entry : ignoreRowEndMap.entrySet()) {
        String sheetName = entry.getKey();

        /*
         * if (sheetName.length() > 31) //
         * excel的sheet的名字的最长长度为31，而当以csv的文件名为sheet名时，文件名很容易超过31哥，所以需要进行此处理 { sheetName =
         * sheetName.substring(0, 31); }
         */

        List<Integer> skipRowList = new ArrayList<>();

        if (skipRowListMap.containsKey(sheetName)) {
          skipRowList = skipRowListMap.get(sheetName);
        }
        Integer ignoreRowEnd = entry.getValue();

        Integer totalRow = newFileListDataMap.get(sheetName).size();
        log.info("sheetName:"+sheetName+",ignoreRowEnd="+ignoreRowEnd+",totalRow:"+totalRow);
        for (int e = totalRow - ignoreRowEnd; e < totalRow; e++) {
          skipRowList.add(e);
        }

        skipRowListMap.put(sheetName, skipRowList);
      }
    }

    if (skipRowListMap.entrySet().size() > 0) {
      for (Map.Entry<String, List<Integer>> entry : skipRowListMap.entrySet()) {
        String sheetName = entry.getKey();

        List<Integer> skipRowList = entry.getValue();

        // 有可能该sheetName 在最新的文件中已经删除了
        /*
         * if (sheetName.length() > 31) //
         * excel的sheet的名字的最长长度为31，而当以csv的文件名为sheet名时，文件名很容易超过31哥，所以需要进行此处理 { sheetName =
         * sheetName.substring(0, 31); }
         */
        List<List> rowList = newFileListDataMap.get(sheetName);
        if (rowList == null)// 如果已经删除了，则不进行任何的处理
        {
          continue;
        }

        List<List> skipRowResultRowList = new ArrayList<>();

        if (skipRowList != null && skipRowList.size() > 0) {
          if (rowList != null) {
            for (int i = 0; i < rowList.size(); i++) {
              boolean skip = false;

              for (Integer skipRow : skipRowList) {
                if (skipRow == i) {
                  skip = true;
                  break;
                }
              }

              if (!skip) {
                skipRowResultRowList.add(rowList.get(i));
              }
            }
          }

        } else {
          skipRowResultRowList = rowList;
        }
        newFileListDataMap.put(sheetName, skipRowResultRowList);

      }
    }

    dataPtoneFile.setFileListDataMap(newFileListDataMap);
    return dataPtoneFile;

  }

  private static PtoneFile skipRowColTitle(PtoneFile ptoneFile,
      Map<String, Integer[]> skipRowArrayMap, Map<String, Integer[]> skipColArrayMap,
      Map<String, Integer> titleRowMap) {
    PtoneFile dataPtoneFile = new PtoneFile();

    dataPtoneFile.setId(ptoneFile.getId());
    dataPtoneFile.setName(ptoneFile.getName());

    LinkedHashMap<String, List<List>> fileListDataMap = ptoneFile.getFileListDataMap();
    LinkedHashMap<String, List<List>> newFileListDataMap = new LinkedHashMap<>();

    for (Map.Entry<String, List<List>> tmpEntry : fileListDataMap.entrySet()) {
      newFileListDataMap.put(tmpEntry.getKey(), tmpEntry.getValue());
    }

    // 处理跳过行 (把title row也放入到skip row中)
    if (skipRowArrayMap.entrySet().size() > 0) {
      for (Map.Entry<String, Integer[]> entry : skipRowArrayMap.entrySet()) {
        String sheetName = entry.getKey();
        Integer[] skipRowArray = entry.getValue();

        // 有可能该sheetName 在最新的文件中已经删除了
        /*
         * if (sheetName.length() > 31) //
         * excel的sheet的名字的最长长度为31，而当以csv的文件名为sheet名时，文件名很容易超过31哥，所以需要进行此处理 { sheetName =
         * sheetName.substring(0, 31); }
         */
        List<List> rowList = newFileListDataMap.get(sheetName);
        if (rowList == null)// 如果已经删除了，则不进行任何的处理
        {
          continue;
        }

        List<List> skipRowResultRowList = new ArrayList<>();

        if (skipRowArray != null && skipRowArray.length > 0) {
          if (rowList != null) {
            for (int i = 0; i < rowList.size(); i++) {
              boolean skip = false;

              for (Integer skipRow : skipRowArray) {
                if (skipRow == i) {
                  // skipRowResultRowList.add(rowList.get(i));
                  skip = true;
                  break;
                }
              }

              if (!skip) {
                skipRowResultRowList.add(rowList.get(i));
              }
            }
          }

        } else {
          skipRowResultRowList = rowList;
        }

        newFileListDataMap.put(sheetName, skipRowResultRowList);
        // 跳过title
        // rowList.remove()
      }
    }


    /*
     * LinkedHashMap<String,List<Row>> fileDataMap=ptoneFile.getFileDataMap();
     * 
     * LinkedHashMap<String,List<Row>> newFileDataMap=new LinkedHashMap<>();
     * 
     * 
     * for(Map.Entry<String,List<Row>> tmpEntry:fileDataMap.entrySet()) {
     * newFileDataMap.put(tmpEntry.getKey(),tmpEntry.getValue()); }
     * 
     * //处理跳过行 (把title row也放入到skip row中) if(skipRowArrayMap.entrySet().size()>0) {
     * for(Map.Entry<String,Integer[]> entry:skipRowArrayMap.entrySet()) { String
     * sheetName=entry.getKey(); Integer[] skipRowArray=entry.getValue();
     * 
     * List<Row> rowList=newFileDataMap.get(sheetName);
     * 
     * List<Row> skipRowResultRowList=new ArrayList<>();
     * 
     * for(int i=0;i<rowList.size();i++) { for(Integer skipRow:skipRowArray) { if(skipRow!=i) {
     * skipRowResultRowList.add(rowList.get(i)); } } }
     * newFileDataMap.put(sheetName,skipRowResultRowList); //跳过title //rowList.remove() } }
     */

    // 处理跳过列 (列暂时不需要进行处理，因为查询的时候 根本就不会查询被跳过的列)
    /*
     * if(skipColArrayMap.entrySet().size()>0) { for(Map.Entry<String,Integer[]>
     * entry:skipColArrayMap.entrySet()) { String sheetName=entry.getKey(); Integer[]
     * skipColArray=entry.getValue(); List<Row> rowList=newFileDataMap.get(sheetName); List<Row>
     * skipColResultRowList=new ArrayList<>();
     * 
     * for(Row row:rowList) { List<SelectItem> selectItemList=Arrays.asList(row.getSelectItems());
     * List<SelectItem> newSelectItemList=new ArrayList<>();
     * 
     * List<Object> valueList=Arrays.asList(row.getValues()); List<Object> newValueList=new
     * ArrayList<>();
     * 
     * for(int j=0;j<selectItemList.size();j++) { for(Integer skipCol:skipColArray) { if(j!=skipCol)
     * { newSelectItemList.add(selectItemList.get(j)); newValueList.add(valueList.get(j)); } } }
     * 
     * DefaultRow defaultRow=new DefaultRow(new
     * SimpleDataSetHeader(newSelectItemList),newValueList.toArray());
     * skipColResultRowList.add(defaultRow); }
     * 
     * newFileDataMap.put(sheetName,skipColResultRowList); } }
     */

    // dataPtoneFile.setFileDataMap(newFileDataMap);
    dataPtoneFile.setFileListDataMap(newFileListDataMap);
    return dataPtoneFile;
  }

  // 目前只限制了行数，列数没有进行限制
  public static PtoneFile limitPtoneFile(PtoneFile ptoneFile) throws Exception {
    PtoneFile limitResultPtoneFile = ptoneFile;
    try {
      limitResultPtoneFile = (PtoneFile) ptoneFile.clone();
    } catch (CloneNotSupportedException e) {
      log.error("clone PtoneFile Error", e);
      throw e;
    }

    LinkedHashMap<String, List<List>> limitFileListDataMap = new LinkedHashMap<>();
    LinkedHashMap<String, List<List>> fileListDataMap = limitResultPtoneFile.getFileListDataMap();

    LinkedHashMap<String, List<Row>> limitFileDataMap = new LinkedHashMap<>();
    LinkedHashMap<String, List<Row>> fileDataMap = limitResultPtoneFile.getFileDataMap();

    if (fileListDataMap != null && fileListDataMap.entrySet().size() > 0) {
      for (Map.Entry<String, List<List>> entry : fileListDataMap.entrySet()) {
        List<List> tmpList = new ArrayList<>();
        String sheetName = entry.getKey();
        List<List> sheetRowList = entry.getValue();

        // log.info(ptoneFile.getId() + "->" + sheetName + "->" + sheetRowList.size());
        if (sheetRowList.size() >= DsConstants.EXCEL_EDITOR_ROW_LIMIT) {
          if (fileDataMap.containsKey(sheetName)) {
            limitFileDataMap.put(sheetName,
                fileDataMap.get(sheetName)
                    .subList(0, DsConstants.EXCEL_EDITOR_ROW_LIMIT.intValue()));
          }
          tmpList = sheetRowList.subList(0, DsConstants.EXCEL_EDITOR_ROW_LIMIT.intValue());
        } else {
          if (fileDataMap.containsKey(sheetName)) {
            limitFileDataMap.put(sheetName, fileDataMap.get(sheetName));
          }
          tmpList = sheetRowList;
        }
        // log.info(ptoneFile.getName()+"->"+sheetName+"->maxSize:"+tmpList.size());
        limitFileListDataMap.put(sheetName, tmpList);
      }
    }
    limitResultPtoneFile.setFileListDataMap(limitFileListDataMap);
    if (limitFileDataMap.entrySet().size() > 0) {
      limitResultPtoneFile.setFileDataMap(limitFileDataMap);
    }

    /*
     * LinkedHashMap<String, List<Row>> limitFileDataMap=new LinkedHashMap<>();
     * LinkedHashMap<String, List<Row>> fileDataMap=limitResultPtoneFile.getFileDataMap();
     * if(fileDataMap!=null&&fileDataMap.entrySet().size()>0) { for(Map.Entry<String, List<Row>>
     * entry:fileDataMap.entrySet()) { List<Row> tmpList=new ArrayList<>(); String
     * sheetName=entry.getKey(); List<Row> sheetRowList=entry.getValue();
     * log.info(ptoneFile.getName() + "->" + sheetName + "->" + sheetRowList.size());
     * if(sheetRowList.size()>= Constants.MAX_ROWS) {
     * tmpList=sheetRowList.subList(0,Constants.MAX_ROWS.intValue()); }else { tmpList=sheetRowList;
     * } limitFileDataMap.put(sheetName,tmpList); } }
     * limitResultPtoneFile.setFileDataMap(limitFileDataMap);
     */

    return limitResultPtoneFile;
  }

  // public static void dropTable(String fileId, MutableSchema schema, String dataBaseName)
  // throws Exception {
  // // 首先生成删除表的语句
  // List<String> dropTableSqlList = generateDropTableSqlList(fileId, schema);
  // Connection connection = QueryDataBaseUtil.createQueryDataBaseConnection(dataBaseName);
  // Statement dropTableStatement = connection.createStatement();
  // for (String dropTableSql : dropTableSqlList) {
  // dropTableStatement.addBatch(dropTableSql);
  // }
  // dropTableStatement.executeBatch();
  // }

  // public static void createTable(String fileId, MutableSchema schema, String dataBaseName)
  // throws Exception {
  // // 首先生成创建表的语句
  // List<String> createTableSqlList = generateCreateTableSqlList(fileId, schema);
  // Connection connection = QueryDataBaseUtil.createQueryDataBaseConnection(dataBaseName);
  // Statement createTableStatement = connection.createStatement();
  // for (String createTableSql : createTableSqlList) {
  // createTableStatement.addBatch(createTableSql);
  // }
  // createTableStatement.executeBatch();
  // }

  // public static void clearTable(String fileId, MutableSchema schema, String dataBaseName)
  // throws Exception {
  // // 首先生成清空表的语句
  // List<String> createTableSqlList = generateClearTableSqlList(fileId, schema);
  // Connection connection = QueryDataBaseUtil.createQueryDataBaseConnection(dataBaseName);
  // Statement clearTableStatement = connection.createStatement();
  // for (String clearTableSql : createTableSqlList) {
  // clearTableStatement.addBatch(clearTableSql);
  // }
  // clearTableStatement.executeBatch();
  // }

  // public static void insertPtoneFileToTable(String fileId, PtoneFile ptoneFile,
  // String dataBaseName, MutableSchema schema) throws Exception {
  // //首先生成创建表的语句
  // Connection connection= QueryDataBaseUtil.createQueryDataBaseConnection(dataBaseName);
  // /*List<String> createTableSqlList=generateCreateTableSqlList(ptoneFile.getSchema());
  // Connection connection= QueryDataBaseUtil.createQueryDataBaseConnection(dataBaseName);
  // Statement createTableStatement = connection.createStatement();
  // for(String createTableSql:createTableSqlList)
  // {
  // createTableStatement.addBatch(createTableSql);
  // }
  // createTableStatement.executeBatch();*/
  //
  // //当远程文件的结构更新时，原来的schema和最新的schema是不一样的，需要使用原理的schema
  // //MutableSchema schema=ptoneFile.getSchema();
  //
  // //生成插入的语句
  // LinkedHashMap<String, List<List>> fileListDataMap = ptoneFile.getFileListDataMap();
  //
  // for (Table table : schema.getTables()) {
  // String tableName = table.getName();
  // if (tableName.length() > 31)// 处理CSV对应的sheet文件名超过31的限制
  // {
  // tableName = tableName.substring(0, 31);
  // }
  //
  // List<List> tableData = fileListDataMap.get(tableName);
  //
  // List<String> insertTableSqlList = generateInsertSql(fileId, tableData, table);
  // Statement insertTableStatement = connection.createStatement();
  // for (String insertTableSql : insertTableSqlList) {
  // insertTableStatement.addBatch(insertTableSql);
  // }
  // insertTableStatement.executeBatch();
  //
  // }
  //
  // /*for(Map.Entry<String,List<List>> entry:fileListDataMap.entrySet())
  // {
  // String tableName=entry.getKey();
  //
  // Table table=schema.getTableByName(tableName);
  // if(table==null)
  // {
  // System.out.println(fileId+"新增加了->sheet:"+tableName);
  // }else {
  // List<List> tableData=entry.getValue();
  //
  // List<String> insertTableSqlList=generateInsertSql(fileId,tableData,table);
  // Statement insertTableStatement = connection.createStatement();
  // for(String insertTableSql:insertTableSqlList)
  // {
  // insertTableStatement.addBatch(insertTableSql);
  // }
  // insertTableStatement.executeBatch();
  // }
  //
  // }*/
  //
  // }

  // public static List<String> generateClearTableSqlList(String fileId, MutableSchema schema) {
  // List<String> clearTableSqlList = new ArrayList<>();
  // for (MutableTable table : schema.getTables()) {
  // String tableName = table.getId();
  // String clearTableSql = "TRUNCATE `" + tableName + "`;";
  // clearTableSqlList.add(clearTableSql);
  // }
  //
  // return clearTableSqlList;
  // }

  // public static List<String> generateCreateTableSqlList(String fileId, MutableSchema schema) {
  // List<String> createTableSqlList = new ArrayList<>();
  //
  // for (MutableTable table : schema.getTables()) {
  // String tableName = table.getId();
  // StringBuilder createTableSqlBuilder = new StringBuilder("create table  IF NOT EXISTS `");
  // createTableSqlBuilder.append(tableName).append("` ( \n");
  // Column[] columns = table.getColumns();
  //
  // for (int i = 0; i < columns.length; i++) {
  // Column column = columns[i];
  // String columnName=column.getId();
  // //String columnType=column.getType().getName();
  //
  // createTableSqlBuilder.append("`").append(columnName).append("` ");
  // /*if(columnType.equalsIgnoreCase("STRING")
  // ||columnType.equalsIgnoreCase("PERCENT")
  // ||columnType.equalsIgnoreCase("CURRENCY")
  // ||columnType.equalsIgnoreCase("TIME")
  // )//文本类型
  // {
  // createTableSqlBuilder.append(" VARCHAR(255) ");
  // }else {
  // createTableSqlBuilder.append(" VARCHAR(255) ");
  // }*/
  // createTableSqlBuilder.append(" LONGTEXT ");
  //
  // if (i < columns.length - 1) {
  // createTableSqlBuilder.append(", \n");
  // }
  // }
  // createTableSqlBuilder.append(" \n ) ENGINE=MyISAM DEFAULT CHARSET=utf8;");
  //
  // createTableSqlList.add(createTableSqlBuilder.toString());
  // }
  // return createTableSqlList;
  // }

  // public static List<String> generateDropTableSqlList(String fileId, MutableSchema schema) {
  // List<String> dropTableSqlList = new ArrayList<>();
  //
  // for (MutableTable table : schema.getTables()) {
  // String tableName = table.getId();
  // StringBuilder dropTableSqlBuilder = new StringBuilder("drop table  IF  EXISTS `");
  // dropTableSqlBuilder.append(tableName).append("`; ");
  //
  // dropTableSqlList.add(dropTableSqlBuilder.toString());
  // }
  // return dropTableSqlList;
  // }

  // public static List<String> generateInsertTableSqlList(String fileId, PtoneFile ptoneFile) {
  // List<String> insertTableSqlList = new ArrayList<>();
  //
  // LinkedHashMap<String, List<List>> fileListDataMap = ptoneFile.getFileListDataMap();
  //
  // for (Map.Entry<String, List<List>> entry : fileListDataMap.entrySet()) {
  // String tableName = entry.getKey();
  // List<List> tableData = entry.getValue();
  //
  // Table table = ptoneFile.getSchema().getTableByName(tableName);
  //
  // List<String> insertSqlList = generateInsertSql(fileId, tableData, table);
  // insertTableSqlList.addAll(insertSqlList);
  // }
  //
  // return insertTableSqlList;
  //
  // }

  // public static List<String> generateInsertSql(String fileId, List<List> rowList, Table table) {
  // List<String> insertSqlList = new ArrayList<>();
  // String tableName = table.getId();
  // int realColCount = table.getColumns().length;
  //
  // for (List colList : rowList) {
  // StringBuilder insertSqlBuilder = new StringBuilder();
  // insertSqlBuilder.append(" insert into  `").append(tableName).append("` values (");
  // List realColList = colList.subList(0, realColCount);
  //
  // for (int i = 0; i < realColList.size(); i++) {
  //
  // Object col = realColList.get(i);
  // String colValue = col.toString();
  // String str = "'";
  // if (col.toString().contains("'")) {
  // colValue = colValue.replaceAll("'", "\\\\'");
  // }
  //
  // Column column = table.getColumn(i);
  // if (column != null && column.getType() != null) {
  // // CURRENCY NUMBER PERCENT
  // String columnType = column.getType().getName();
  //
  // if (columnType.equalsIgnoreCase("CURRENCY")) {
  // colValue = colValue.replaceAll(CurrencyReplaceRegex, "");
  // } else if (columnType.equalsIgnoreCase("NUMBER")) {
  // colValue = colValue.replaceAll(NumberReplaceRegex, "");
  // } else if (columnType.equalsIgnoreCase("PERCENT")) {
  // colValue = colValue.replaceAll(PercentReplaceRegex, "");
  // } else if (columnType.equalsIgnoreCase("DURATION")) {
  // colValue = colValue.replaceAll(DurationReplaceRegex, "");
  // }
  // // log.info("columnType:"+columnType+",colValue:"+colValue);
  // }
  //
  // insertSqlBuilder.append(str).append(colValue).append(str);
  // if (i < realColList.size() - 1) {
  // insertSqlBuilder.append(",");
  // }
  // }
  // insertSqlBuilder.append(");");
  // insertSqlList.add(insertSqlBuilder.toString());
  // }
  // return insertSqlList;
  // }

}
