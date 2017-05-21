package com.sizzler.provider.service.upload;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.util.CommonQueryRequest;
import org.apache.metamodel.util.FileHelper;
import org.apache.metamodel.util.HdfsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ptmind.common.utils.StringUtil;
import com.ptmind.cpdetector.CpdetectorUtil;
import com.sizzler.common.exception.ServiceException;
import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.FileType;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.sizzler.UserConnectionConfig;
import com.sizzler.provider.common.MetaRequest;
import com.sizzler.provider.common.exception.DataSourceLogMessageUtil;
import com.sizzler.provider.common.file.PtoneFile;
import com.sizzler.provider.common.impl.DefaultDataResponse;
import com.sizzler.provider.common.impl.ExcelEditorDataRequest;
import com.sizzler.provider.common.impl.ExcelEditorDataResponse;
import com.sizzler.provider.common.impl.ExcelUpdateDataRequest;
import com.sizzler.provider.common.query.QueryDataBaseUtil;
import com.sizzler.provider.common.util.BuildStringUtil;
import com.sizzler.provider.common.util.PtoneFileUtil;
import com.sizzler.provider.common.util.PtoneHdfsUtil;
import com.sizzler.provider.common.util.SpliterFileUtil;
import com.sizzler.provider.common.util.excel.ExcelUtil;
import com.sizzler.provider.domain.request.ExcelDataRequest;
import com.sizzler.provider.domain.response.UploadFileMetaResponse;
import com.sizzler.provider.domain.response.UploadUpdateDataResponse;

@Service("uploadGatherService")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class UploadGatherServiceImpl implements UploadGatherService {

  private static final Logger log = LoggerFactory.getLogger(UploadGatherServiceImpl.class);

  private static final String UPDATE_DATA = "updateData";
  private static final String GET_EDITOR_DATA = "getEditorData";
  private static final String FILE_ID = "fileId";

  @Override
  public UploadFileMetaResponse getMeta(MetaRequest metaRequest) throws ServiceException {

    UploadFileMetaResponse response = new UploadFileMetaResponse();
    LogMessage logMessage = DataSourceLogMessageUtil.buildLogMessage(metaRequest);
    UserConnection userConnection = metaRequest.getUserConnection();
    try {
      PtoneFile ptoneFile = null;
      ptoneFile = getFile(userConnection, null);
      response.setFile(limitPtoneFile(ptoneFile));

    } catch (Exception e) {
      // 将异常信息输出到日志文件
      log.error("upload get meta error: ", e);
      throw ServiceException.buildServiceException("upload get meta error: ", e);
    } finally {
      // 将logMessage打印到日志中
      log.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return response;
  }

  @Override
  public DefaultDataResponse getData(ExcelDataRequest dataRequest) throws ServiceException {
    LogMessage logMessage = DataSourceLogMessageUtil.buildLogMessage(dataRequest);
    DefaultDataResponse response = new DefaultDataResponse();
    String uid = dataRequest.getUserConnection().getUid();
    String dataBaseName = "ptone_" + uid;
    String tableName = dataRequest.getTableName();
    CommonQueryRequest commonQueryRequest = dataRequest.getQueryRequest();
    String query = BuildStringUtil.buildQuerySql(commonQueryRequest.getQuery(), tableName,
        dataBaseName);
    logMessage.addOperateInfo("querySql", query);
    String dsCode = dataRequest.getUserConnection().getDsCode();
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    Statement totalStatement = null;
    ResultSet totalResultSet = null;
    try {
      connection = QueryDataBaseUtil.createQueryDataBaseConnection();
      statement = connection.createStatement();

      resultSet = statement.executeQuery(query);
      response.setObjetRowList(QueryDataBaseUtil.buildResultRowList(resultSet));
      response.setObjectRowColumnList(getColumnNameList(resultSet));
      List<Object> totalRowList = new ArrayList<Object>();
      if (commonQueryRequest.getMetrics() != null && !commonQueryRequest.getMetrics().equals("")) {
        String totalQuery = BuildStringUtil.buildQuerySql(commonQueryRequest.getTotalQuery(),
            tableName, dataBaseName);
        logMessage.addOperateInfo("totalQuery", query);
        if (StringUtil.isBlank(totalQuery)) {
          totalQuery = QueryDataBaseUtil.createTotalSql(commonQueryRequest, tableName, dsCode,
              dataBaseName);
        }
        // log.info(uid + ":total-query->" + totalQuery);
        totalStatement = connection.createStatement();
        totalResultSet = totalStatement.executeQuery(totalQuery);
        List<List> totalResultRowList = QueryDataBaseUtil.buildResultRowList(totalResultSet);
        if (totalResultRowList != null && totalResultRowList.size() > 0
            && totalResultRowList.get(0) != null) {
          totalRowList = totalResultRowList.get(0);
        }
      }
      response.setTotalRowList(totalRowList);

    } catch (Exception e) {
      log.error("upload get data errro: ", e);
      // 邮件
      throw ServiceException.buildServiceException("upload get data errro: ", e);
    } finally {
      FileHelper.safeClose(statement, resultSet, totalStatement, totalResultSet);
      QueryDataBaseUtil.releaseConnection(connection);
      // 将logMessage打印到日志中
      log.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return response;
  }

  // 目前只限制了行数，列数没有进行限制
  private PtoneFile limitPtoneFile(PtoneFile ptoneFile) {
    PtoneFile limitResultPtoneFile = ptoneFile;
    try {
      limitResultPtoneFile = (PtoneFile) ptoneFile.clone();
    } catch (Exception e) {
      e.printStackTrace();
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
     * LinkedHashMap<String, List<Row>>
     * fileDataMap=limitResultPtoneFile.getFileDataMap();
     * if(fileDataMap!=null&&fileDataMap.entrySet().size()>0) {
     * for(Map.Entry<String, List<Row>> entry:fileDataMap.entrySet()) {
     * List<Row> tmpList=new ArrayList<>(); String sheetName=entry.getKey();
     * List<Row> sheetRowList=entry.getValue(); log.info(ptoneFile.getName() +
     * "->" + sheetName + "->" + sheetRowList.size()); if(sheetRowList.size()>=
     * Constants.MAX_ROWS) {
     * tmpList=sheetRowList.subList(0,Constants.MAX_ROWS.intValue()); }else {
     * tmpList=sheetRowList; } limitFileDataMap.put(sheetName,tmpList); } }
     * limitResultPtoneFile.setFileDataMap(limitFileDataMap);
     */

    return limitResultPtoneFile;
  }

  @Override
  public UploadUpdateDataResponse updateData(ExcelUpdateDataRequest request)
      throws ServiceException {

    UploadUpdateDataResponse response = new UploadUpdateDataResponse();

    boolean fileChanged = false;
    UserConnection userConnection = request.getUserConnection();
    LogMessage logMessage = buildLogMessage(userConnection, UPDATE_DATA);
    logMessage.addOperateInfo(FILE_ID, request.getFileId());
    try {
      String operateType = request.getOperateType();

      PtoneFile ptoneFile = getFile(request.getUserConnection(), false); // 从hdfs上获取文件
      logMessage.addOperateInfo(operateType, "operateType is " + operateType);
      if (operateType.equalsIgnoreCase("update") || operateType.equalsIgnoreCase("edit_save")) {
        response.setLastModifiedDate(System.currentTimeMillis());
      }
      executeUpdate(ptoneFile, request, true);
      response.setChanged(fileChanged);
      response.setUpdateStatus(true);
    } catch (Exception e) {
      // 将异常信息输出到日志文件
      log.error("upload update Data error:" + e.getMessage(), e);
      throw ServiceException.buildServiceException("upload update Data error:", e);
    } finally {
      // 将logMessage打印到日志中
      log.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return response;
  }

  @Override
  public ExcelEditorDataResponse getEditorData(ExcelEditorDataRequest request)
      throws ServiceException {
    ExcelEditorDataResponse response = new ExcelEditorDataResponse();
    UserConnection userConnection = request.getUserConnection();
    LogMessage logMessage = buildLogMessage(userConnection, GET_EDITOR_DATA);
    logMessage.addOperateInfo(FILE_ID, request.getFileId());
    try {
      PtoneFile ptoneFile = null;

      ptoneFile = getFile(userConnection, null);
      PtoneFile editPtoneFile = PtoneFileUtil.generateEditFile(ptoneFile);

      response.setEditorData(editPtoneFile.getFileListDataMap());
      response.setSchema(ptoneFile.getSchema());

    } catch (Exception e) {
      log.error("upload getEditorData error:", e);
      throw ServiceException.buildServiceException("upload getEditorData error:", e);
    } finally {
      // 将logMessage打印到日志中
      log.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return response;
  }

  private PtoneFile getFile(UserConnection userConnection, Boolean maxRowLimit) throws Exception {

    PtoneFile ptoneFile = null;
    InputStream inputStream = null;
    ByteArrayOutputStream swapStream = null;
    ByteArrayInputStream byteArrayInputStream = null;
    try {

      maxRowLimit = maxRowLimit == null ? true : maxRowLimit;
      Map<String, Object> config = (Map<String, Object>) JSON.parse(userConnection.getConfig());

      String path = (String) config.get(UserConnectionConfig.UploadConfig.PATH);
      String type = (String) config.get(UserConnectionConfig.UploadConfig.TYPE);
      String spliter = (String) config.get(UserConnectionConfig.UploadConfig.SPLITER);
      String fileId = (String) config.get(UserConnectionConfig.UploadConfig.FILE_ID);
      String fileName = (String) config.get(UserConnectionConfig.UploadConfig.FILE_NAME);

      // HdfsResource hdfsResource = new HdfsResource(path);
      HdfsResource hdfsResource = PtoneHdfsUtil.createHdfsResource(path);
      inputStream = hdfsResource.read();

      String hdfsFileName = hdfsResource.getName();
      FileType fileType = FileType.getFileTypeByName(type);
      if (StringUtil.isBlank(fileName)) {
        fileName = hdfsFileName;
      }

      if (fileType == FileType.EXCEL) {
        ptoneFile = ExcelUtil.convertExcelToPtoneFile(fileName, inputStream, maxRowLimit);
      } else if (fileType == FileType.CSV || fileType == FileType.TSV || fileType == FileType.TXT) {
        // if(fileName.length() > 31){
        // fileName = fileName.substring(0, 31);
        // }

        swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024 * 10]; // buff用于存放循环读取的临时数据
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, 100)) > 0) {
          swapStream.write(buff, 0, rc);
        }
        byteArrayInputStream = new ByteArrayInputStream(swapStream.toByteArray());

        // 先监测编码
        Charset charset = CpdetectorUtil.determineChartSet(byteArrayInputStream);

        ptoneFile = SpliterFileUtil.convertSpliterFileToPtoneFile(fileName, byteArrayInputStream,
            spliter, maxRowLimit, charset);

      }
      if (ptoneFile != null && StringUtil.isBlank(ptoneFile.getId())) {
        ptoneFile.setId(fileId);
      }
    } finally {
      FileHelper.safeClose(byteArrayInputStream, swapStream, inputStream);
    }
    return ptoneFile;
  }

  public void executeUpdate(PtoneFile ptoneFile, ExcelUpdateDataRequest request, boolean fileChanged)
      throws Exception {

    // 针对每个schema都需要生成单独的data文件
    List<MutableSchema> schemaList = request.getSchemaList();
    for (MutableSchema schema : schemaList) {
      String schemaId = schema.getId();

      Map<String, Integer> ignoreRowStartMap = request.getSchemaIgnoreRowStartMap().get(schemaId);
      Map<String, Integer> ignoreRowEndMap = request.getSchemaIgnoreRowEndMap().get(schemaId);

      // 生成Data文件
      // PtoneFile
      PtoneFile dataPtoneFile = PtoneFileUtil.generateDataFile(ptoneFile, schema,
          ignoreRowStartMap, ignoreRowEndMap);

      String dataBaseName = "ptone_" + request.getUserConnection().getUid();

      Connection connection = null;
      try {
        connection = QueryDataBaseUtil.createQueryDataBaseConnection();

        // 判断数据库是否存在，如果不存在，则需要进行创建 ，数据库的命名规范为 ptone_uid
        QueryDataBaseUtil.createDataBase(dataBaseName, connection);
        QueryDataBaseUtil.dropTable(request.getFileId(), schema, connection, dataBaseName);
        // 创建表，并将数据插入进去 表的命名规范为：tableId
        QueryDataBaseUtil.createTable(request.getFileId(), schema, connection, dataBaseName);

        QueryDataBaseUtil.insertPtoneFileToTable(request.getFileId(), dataPtoneFile, schema,
            connection, dataBaseName);
      } finally {
        QueryDataBaseUtil.releaseConnection(connection);
      }
    }

  }

  private List<String> getColumnNameList(ResultSet resultSet) {
    List<String> columnNameList = new ArrayList<String>();
    try {
      ResultSetMetaData rsmd = resultSet.getMetaData();
      for (int c = 1; c <= rsmd.getColumnCount(); c++) {
        columnNameList.add(rsmd.getColumnName(c));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return columnNameList;
  }

  /**
   * 创建LogMessage对象，同时将userConnection，fileId中的信息设置到LogMessage中 \
   * 
   * @author shaoqiang.guo
   * @date 2016年9月14日16:30:18
   * @param userConnection
   * @param operate
   * @return
   */
  private static LogMessage buildLogMessage(UserConnection userConnection, String operate) {
    LogMessage logMessage = new LogMessage();
    logMessage.setOperate(operate);
    if (userConnection == null) {
      return logMessage;
    }
    DataSourceLogMessageUtil.addUserConnectionToLogMessage(userConnection, logMessage);
    return logMessage;
  }

}
