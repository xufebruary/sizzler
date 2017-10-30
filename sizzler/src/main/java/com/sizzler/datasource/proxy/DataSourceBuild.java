package com.sizzler.datasource.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.metamodel.data.Row;
import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.MutableColumn;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sizzler.common.log.ElkLogUtil;
import com.sizzler.common.log.LogMessage;
import com.sizzler.common.log.LogMessageUtil;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.CollectionUtil;
import com.sizzler.common.utils.JodaDateUtil;
import com.sizzler.common.utils.StringUtil;
import com.sizzler.datasource.reflex.ClassReflex;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.UserConnectionSourceTable;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableColumnDto;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableDto;
import com.sizzler.domain.ds.vo.UserConnectionSourceTableColumnVo;
import com.sizzler.domain.ds.vo.UserConnectionSourceTableVo;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.domain.dto.UIEditorData;
import com.sizzler.domain.user.PtoneUserBasicSetting;
import com.sizzler.provider.common.file.PtoneFile;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

@Component
public class DataSourceBuild {

  private Logger logger = LoggerFactory.getLogger(DataSourceBuild.class);

  @Autowired
  private ServiceFactory serviceFactory;

  /**
   * 构建UserConnectionSourceVo对象
   * 
   * @Description: buildSourceVoByPtoneFile.
   * @date: 2016/01/27
   * @author: zhangli
   */
  public UserConnectionSourceVo buildSourceVoByPtoneFile(PtoneFile ptoneFile, UserConnection savedUserConnection) {
    LogMessage logMessage = new LogMessage();
    UserConnectionSourceVo sourceVo = new UserConnectionSourceVo();
    try {
      String uid = savedUserConnection.getUid();
      String connectionid = savedUserConnection.getConnectionId();
      String fileId = ptoneFile.getId();
      LogMessageUtil.addBasicInfo(logMessage, uid, "buildSourceVoByPtoneFile");
      LogMessageUtil.addOperateInfoOfFile(logMessage, fileId);
      LogMessageUtil.addOperateInfoOfExcel(logMessage, savedUserConnection.getConnectionId(), null,
          savedUserConnection.getDsCode());
      PtoneUserBasicSetting userSetting = serviceFactory.getUserSettingService().get(uid);

      MutableSchema mutableSchema = ptoneFile.getSchema();

      LinkedHashMap<String, List<Row>> dataMap = ptoneFile.getFileDataMap();

      String queryType =
          serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById(savedUserConnection.getDsId())
              .getQueryType();
      // String category =
      // serviceFactory.getPtoneDsInfoCache().getPtoneDsInfoById(savedUserConnection.getDsId()).getCategory();

      sourceVo.setName(ptoneFile.getName());
      sourceVo.setConnectionId(connectionid);
      sourceVo.setUid(Long.parseLong(uid));
      sourceVo.setFileId(ptoneFile.getId());
      sourceVo.setFolderId(ptoneFile.getFolderId());
      sourceVo.setLastModifiedDate(ptoneFile.getLastModifiedDate());

      // List<UITable> uiTableList = buildUITables(mutableSchema, dataMap, queryType);
      int sheetCount = mutableSchema.getTables().length;
      List<UserConnectionSourceTableVo> tableVos = new ArrayList<>();
      for (int i = 0; i < sheetCount; i++) {
        UserConnectionSourceTableVo tableVo = new UserConnectionSourceTableVo();
        Column column[] = mutableSchema.getTables()[i].getColumns();

        String sheetName = mutableSchema.getTables()[i].getName();

        List<UserConnectionSourceTableColumnVo> columnVos = new ArrayList<>();

        // 后台指定
        tableVo.setIgnoreRowStart(1);
        tableVo.setIgnoreRowEnd(0);

        // 处理重名
        boolean headModeFlag = processColumnName(column);
        if (headModeFlag) {
          tableVo.setHeadMode("custom");
        } else {
          tableVo.setHeadMode("assign");
        }

        tableVo.setIgnoreRow(new ArrayList<Integer>());
        tableVo.setIgnoreCol(new ArrayList<Integer>());

        // 后台指定
        tableVo.setId(UUID.randomUUID().toString());

        if (queryType.equalsIgnoreCase("local")) {
          // 后台指定
          tableVo.setCode(tableVo.getId());
        } else if (queryType.equalsIgnoreCase("remote")) {
          tableVo.setCode(sheetName);
        }

        tableVo.setColSum(String.valueOf(mutableSchema.getTables()[i].getColumnCount()));
        tableVo.setRowSum(String.valueOf(mutableSchema.getTables()[i].getRowCount()));
        tableVo.setName(sheetName);
        tableVo.setHeadIndex(0l);
        tableVo.setHeadType("row");
        for (int j = 0; j < column.length; j++) {
          UserConnectionSourceTableColumnVo columnVo = new UserConnectionSourceTableColumnVo();
          String columnName = buildColumnName(column[j].getName(), j, userSetting);
          columnVo.setName(columnName);
          columnVo.setColumnType(column[j].getNativeType());
          if (StringUtil.hasText(column[j].getType().getName())) {
            columnVo.setDataType(column[j].getType().getName());
          } else {
            columnVo.setDataType("STRING");
          }
          columnVo.setType(serviceFactory.getPtoneBasicDictCache()
              .getDictItemByCode("data_type_" + column[j].getType().getName()).getName());
          // andy 2016-01-07 增加dataFormat的设置
          if (column[j].getFormat() != null) {
            columnVo.setDataFormat(column[j].getFormat());
          }
          columnVo.setIndex(new Long(j));
          // 后台指定
          columnVo.setId(UUID.randomUUID().toString());
          if (queryType.equalsIgnoreCase("local")) {
            columnVo.setCode(columnVo.getId());
          } else if (queryType.equalsIgnoreCase("remote")) {
            columnVo.setCode(column[j].getName());
          }
          columnVos.add(columnVo);
        }
        List<Row> rowList = dataMap.get(sheetName);
        if (null != rowList && !rowList.isEmpty()) {
          List<List> data = new ArrayList<>();
          for (Row row : rowList) {
            data.add(Arrays.asList(row.getValues()));
          }
          tableVo.setData(data);
        }
        tableVo.setSchema(columnVos);
        tableVos.add(tableVo);
      }

      sourceVo.setTable(tableVos);
      sourceVo.setDsId(savedUserConnection.getDsId());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw e;
    } finally {
      logger.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
    return sourceVo;
  }

  /**
   * UserConnectionSourceDto对象转换为UserConnectionSourceVo对象
   * @author you.zou
   * @date 2016年11月23日 上午10:43:47
   * @param sourceDto
   * @param sourceVo
   * @param rowSumMap
   * @param editorData
   */
  public void buildSourceVoBySourceDto(UserConnectionSourceDto sourceDto,
      UserConnectionSourceVo sourceVo, Map<String, Object> rowSumMap,
      LinkedHashMap<String, List<List>> editorData) {
    if (sourceDto == null) {
      return;
    }
    List<UserConnectionSourceTableDto> tableDtos = sourceDto.getTables();
    if (CollectionUtil.isEmpty(tableDtos)) {
      // 目前关系型数据库不返回UserConnectionSourceTableDto列表
      // 所以需要从数据库中提取，该判断为备用
      tableDtos =
          serviceFactory.getUserConnectionSourceTableService().findBySourceIdOfDtoIncludeColumns(
              sourceDto.getSourceId());
      return;
    }
    List<UserConnectionSourceTableVo> tableVos = new ArrayList<UserConnectionSourceTableVo>();
    for (UserConnectionSourceTableDto tableDto : tableDtos) {
      if (tableDto == null) {
        continue;
      }
      UserConnectionSourceTableVo tableVo = buildTableVoByTableDto(tableDto, rowSumMap);
      // 设置uiTable的数据
      tableVo.setData(editorData.get(tableVo.getName()));

      List<UserConnectionSourceTableColumnDto> columnDtos = tableDto.getColumns();
      if (CollectionUtil.isEmpty(columnDtos)) {
        // 目前关系型数据库不返回UserConnectionSourceTableColumnDto列表
        // 所以需要从数据库中提取
        columnDtos =
            serviceFactory.getUserConnectionSourceTableColumnService().findByTableIdOfDto(
                tableDto.getTableId());
      }
      List<UserConnectionSourceTableColumnVo> columnVos = new ArrayList<>();
      for (UserConnectionSourceTableColumnDto columnDto : columnDtos) {
        if (columnDto == null) {
          continue;
        }
        UserConnectionSourceTableColumnVo columnVo = buildColumnVoByColumnDto(columnDto);
        columnVos.add(columnVo);
      }
      tableVo.setSchema(columnVos);
      tableVos.add(tableVo);
    }
    sourceVo.setTable(tableVos);
  }

  /**
   * UserConnectionSourceTableColumnDto转换为UITableSchema
   * @author you.zou
   * @date 2016年11月23日 上午10:41:00
   * @param columnDto
   * @return
   */
  public UserConnectionSourceTableColumnVo buildColumnVoByColumnDto(UserConnectionSourceTableColumnDto columnDto) {
    if(columnDto == null){ return null;}
    UserConnectionSourceTableColumnVo columnVo = new UserConnectionSourceTableColumnVo();
    columnVo.setName(columnDto.getName());
    columnVo.setId(columnDto.getColId());
    columnVo.setType(columnDto.getType());
    columnVo.setDataType(columnDto.getDataType());
    columnVo.setDataFormat(columnDto.getDataFormat());
    columnVo.setIndex(columnDto.getColIndex());
    columnVo.setIsIgnore(columnDto.getIsIgnore());
    return columnVo;
  }

  /**
   * tableDto转换为UITable对象
   * @author you.zou
   * @date 2016年11月23日 上午10:28:16
   * @param tableDto
   * @param rowSumMap 用于关系型数据库设置表数据行数
   * @return
   */
  public UserConnectionSourceTableVo buildTableVoByTableDto(UserConnectionSourceTableDto tableDto,
      Map<String, Object> rowSumMap) {
    if(tableDto == null){ return null;}
    UserConnectionSourceTableVo tableVo = new UserConnectionSourceTableVo();
    String tableName = tableDto.getName();
    tableVo.setId(tableDto.getTableId());
    tableVo.setCode(tableDto.getCode());
    tableVo.setName(tableName);
    tableVo.setColSum(tableDto.getColSum());
    if (CollectionUtil.isEmpty(rowSumMap) || !rowSumMap.containsKey(tableName)) {
      tableVo.setRowSum(tableDto.getRowSum());
    } else {
      tableVo.setRowSum(String.valueOf(rowSumMap.get(tableName)));
    }
    tableVo.setIgnoreRow(tableDto.getIgnoreRowList());
    tableVo.setIgnoreCol(tableDto.getIgnoreColList());
    tableVo.setIgnoreRowStart(Integer.valueOf(tableDto.getIgnoreRowStart()));
    tableVo.setIgnoreRowEnd(Integer.valueOf(tableDto.getIgnoreRowEnd()));
    tableVo.setHeadMode(tableDto.getHeadMode());
    tableVo.setHeadType(tableDto.getType());
    String headIndex = tableDto.getHeadIndex();
    if (StringUtil.isNotBlank(headIndex)) {
      tableVo.setHeadIndex(Long.parseLong(tableDto.getHeadIndex()));
    }
    return tableVo;
  }

  /**
   * 通过UserConnectionSourceVo对象构建UserConnectionSourceDto对象<br>
   * 包含Table列表、Column列表<br>
   * 原方法名：createTableSchema
   * @author you.zou
   * @date 2016年11月18日 下午7:09:15
   * @param sourceVo
   * @param sourceDto
   */
  public void buildSourceDtoBySourceVo(UserConnectionSourceVo sourceVo,
      UserConnectionSourceDto sourceDto) {
    if (sourceVo == null) {
      return;
    }
    String sourceId = sourceDto.getSourceId();
    String connectionId = sourceDto.getConnectionId();
    String spaceId = sourceDto.getSpaceId();
    Long uid = sourceDto.getUid();
    String dsCode = sourceDto.getDsCode();
    Long dsId = sourceDto.getDsId();
    String currentDateTime = JodaDateUtil.getCurrentDateTime();
    List<UserConnectionSourceTableVo> tableVos = sourceVo.getTable();
    if (CollectionUtil.isEmpty(tableVos)) {
      return;
    }

    List<UserConnectionSourceTableDto> tableDtos = new ArrayList<UserConnectionSourceTableDto>();
    for (UserConnectionSourceTableVo tableVo : tableVos) {
      if (tableVo == null) {
        continue;
      }

      UserConnectionSourceTableDto tableDto = buildTableDtoByTableVo(tableVo);
      tableDto.setStatus(Constants.validate);
      tableDto.setSourceId(sourceId);
      tableDto.setUid(uid);
      tableDto.setDsCode(dsCode);
      tableDto.setDsId(dsId);
      tableDto.setSpaceId(spaceId);
      tableDto.setCreateTime(currentDateTime);
      tableDto.setConnectionId(connectionId);

      List<UserConnectionSourceTableColumnVo> columnVos = tableVo.getSchema();
      if (CollectionUtil.isNotEmpty(columnVos)) {
        List<UserConnectionSourceTableColumnDto> columnDtos =
            new ArrayList<UserConnectionSourceTableColumnDto>();
        for (UserConnectionSourceTableColumnVo  columnVo : columnVos) {
          if (columnVo == null) {
            continue;
          }
          UserConnectionSourceTableColumnDto columnDto =
              buildColumnDtoByColumnVo(columnVo);
          columnDto.setConnectionId(connectionId);
          columnDto.setSourceId(sourceId);
          columnDto.setUid(uid);
          columnDto.setDsCode(dsCode);
          columnDto.setDsId(dsId);
          columnDto.setSpaceId(spaceId);
          columnDto.setCreateTime(currentDateTime);
          columnDto.setStatus(Constants.validate);
          columnDto.setTableId(tableVo.getId());
          columnDtos.add(columnDto);
        }
        tableDto.setColumns(columnDtos);
      }
      tableDtos.add(tableDto);
    }
    sourceDto.setTables(tableDtos);
  }

  /**
   * 通过UITableSchema对象构建UserConnectionSourceTableColumnDto对象
   * @author you.zou
   * @date 2016年11月18日 下午7:08:07
   * @param uiTableSchema
   * @return
   */
  public UserConnectionSourceTableColumnDto buildColumnDtoByColumnVo(
      UserConnectionSourceTableColumnVo columnVo) {
    UserConnectionSourceTableColumnDto columnDto = new UserConnectionSourceTableColumnDto();
    if (columnVo == null) {
      return null;
    }

    columnDto.setColId(columnVo.getId());
    columnDto.setColIndex(columnVo.getIndex());
    columnDto.setDataFormat(columnVo.getDataFormat());
    columnDto.setIsIgnore(columnVo.getIsIgnore());
    columnDto.setIsCustom(columnVo.getIsCustom());
    columnDto.setName(columnVo.getName());
    columnDto.setCode(columnVo.getCode());
    columnDto.setType(columnVo.getType());
    columnDto.setColumnType(columnVo.getColumnType());
    columnDto.setDataType(columnVo.getDataType());

    return columnDto;
  }

  /**
   * 通过UITable对象构建UserConnectionSourceTableDto对象
   * @author you.zou
   * @date 2016年11月18日 下午6:46:52
   * @param uiTable
   * @return
   */
  public static UserConnectionSourceTableDto buildTableDtoByTableVo(UserConnectionSourceTableVo tableVo) {
    UserConnectionSourceTableDto tableDto = new UserConnectionSourceTableDto();
    if (tableVo == null) {
      return null;
    }
    tableDto.setTableId(tableVo.getId());
    tableDto.setName(tableVo.getName());
    tableDto.setCode(tableVo.getCode());
    tableDto.setType(tableVo.getHeadType());
    tableDto.setIgnoreRowList(tableVo.getIgnoreRow());
    Integer ignoreRowStart = tableVo.getIgnoreRowStart();
    Integer ignoreRowEnd = tableVo.getIgnoreRowEnd();
    if(ignoreRowStart == null){
      tableDto.setIgnoreRowStart("1");
    }else{
      tableDto.setIgnoreRowStart(String.valueOf(ignoreRowStart));
    }
    if(ignoreRowEnd == null){
      tableDto.setIgnoreRowEnd("0");
    }else{
      tableDto.setIgnoreRowEnd(String.valueOf(ignoreRowEnd));
    }
    tableDto.setIgnoreColList(tableVo.getIgnoreCol());
    // 批量忽略列还没有实现，暂时作为注释备份在这
    // mapConfig.put("ignoreColStart", tableVo.getIgnoreColStart());
    // mapConfig.put("ignoreColEnd", tableVo.getIgnoreColEnd());
    tableDto.setColSum(tableVo.getColSum());
    tableDto.setRowSum(tableVo.getRowSum());
    tableDto.setHeadMode(tableVo.getHeadMode());
    Long headIndex = tableVo.getHeadIndex();
    if(headIndex != null){
      tableDto.setHeadIndex(String.valueOf(tableVo.getHeadIndex()));
    }else{
      tableDto.setHeadIndex("0");
    }
    return tableDto;
  }

  /**
   * 保存Table列表和Column列表<br>
   * 原方法名：saveTableSchema
   * @author you.zou
   * @date 2016年11月18日 下午7:20:01
   * @param sourceDto
   * @param editSaveFlag
   * @param saveFlag
   */
  public void saveTablesAndColumns(UserConnectionSourceDto sourceDto, boolean editSaveFlag,
      boolean saveFlag) {
    LogMessage logMessage = new LogMessage();
    try {
      LogMessageUtil.addBasicInfo(logMessage, null, "saveTableAndColumn");
      if (sourceDto == null) {
        throw new NullPointerException(
            "UserConnectionSourceDto Object can't be empty in saveTablesAndColumns method!");
      }
      List<UserConnectionSourceTableDto> tableDtos = sourceDto.getTables();

      if (CollectionUtil.isEmpty(tableDtos)) {
        return;
      }

      for (UserConnectionSourceTableDto tableDto : tableDtos) {

        if (tableDto == null) {
          continue;
        }

        UserConnectionSourceTable table = tableDto.parseToTable();
        if (editSaveFlag) {
          serviceFactory.getUserConnectionSourceTableService().update(table);
        } else if (saveFlag) {
          serviceFactory.getUserConnectionSourceTableService().save(table);
        }

        List<UserConnectionSourceTableColumnDto> columnDtos = tableDto.getColumns();
        if (CollectionUtil.isEmpty(columnDtos)) {
          continue;
        }

        for (UserConnectionSourceTableColumnDto columnDto : columnDtos) {
          if (columnDto == null) {
            continue;
          }
          UserConnectionSourceTableColumn column = columnDto.parseToColumn();

          if (editSaveFlag) {
            serviceFactory.getUserConnectionSourceTableColumnService().update(column);
          } else if (saveFlag) {
            serviceFactory.getUserConnectionSourceTableColumnService().save(column);
          }
        }
      }

      LogMessageUtil.addOperateInfoOfExcel(logMessage, sourceDto.getConnectionId(),
          sourceDto.getSourceId(), sourceDto.getDsCode());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw e;
    } finally {
      logger.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
  }


  public void saveFileDataToDB(UserConnection userConnection, UserConnectionSourceVo sourceVo,
      UserConnectionSourceDto sourceDto, Boolean isAutoUpdate) throws Exception {
    ClassReflex.invokeValue(userConnection.getDsCode(), "saveFileDataToDB", new Class[] {
        UserConnection.class, UserConnectionSourceVo.class, UserConnectionSourceDto.class, Boolean.class},
        userConnection, sourceVo, sourceDto, isAutoUpdate);
  }

  public UIEditorData getEditData(UserConnection userConnection,
      UserConnectionSourceDto sourceDto, Boolean isAutoUpdate, Boolean isUpdate) throws Exception {
    UIEditorData uiEditorData =
        ClassReflex.invokeValue(userConnection.getDsCode(), "getEditData", new Class[] {
            UserConnection.class, UserConnectionSourceDto.class, Boolean.class, Boolean.class},
            userConnection, sourceDto, isAutoUpdate, isUpdate);
    return uiEditorData;
  }

  /**
   * 构建查询Source的参数
   * @author you.zou
   * @date 2016年11月21日 上午11:40:18
   * @param uid
   * @param fileId
   * @param sourceId
   * @param dsCode
   * @param operateType
   * @return
   */
  private Map<String, Object[]> buildFindSourceParam(Long uid, String fileId, String sourceId,
      String dsCode, String operateType) {
    Map<String, Object[]> paramMap = new HashMap<>();
    if (operateType.equalsIgnoreCase("update")
        && !dsCode.equalsIgnoreCase(DsConstants.DS_CODE_UPLOAD)) {
      // 如果是update操作，并且不是Upload数据源，则需要根据用户ID、文件ID查询到相关的其他Source列表
      paramMap.put("uid", new Object[] {uid});
      paramMap.put("fileId", new Object[] {fileId});
    } else {
      // 其他操作只需要获取到SourceID即可
      paramMap.put("sourceId", new Object[] {sourceId});
    }
    // 默认状态都为1
    paramMap.put("status", new Object[] {Constants.validate});
    return paramMap;
  }

  /**
   * 通过Table列表构建忽略行、忽略列的信息
   * @author you.zou
   * @date 2016年11月21日 下午12:18:37
   * @param skipColMap
   * @param skipRowMap
   * @param skipRowStartMap
   * @param skipRowEndMap
   * @param tableDtoList
   */
  private void buildSkipInfoByTables(Map<String, Integer[]> skipColMap,
      Map<String, Integer[]> skipRowMap, Map<String, Integer> skipRowStartMap,
      Map<String, Integer> skipRowEndMap, List<UserConnectionSourceTableDto> tableDtoList) {
    if (CollectionUtil.isEmpty(tableDtoList)) {
      return;
    }
    for (UserConnectionSourceTableDto tableDto : tableDtoList) {
      if (tableDto == null) {
        continue;
      }
      String tableName = tableDto.getName();
      Integer ignoreRowStart = Integer.valueOf(tableDto.getIgnoreRowStart());
      Integer ignoreRowEnd = Integer.valueOf(tableDto.getIgnoreRowEnd());
      // 忽略行、忽略列暂不添加，目前没有被使用
      skipRowStartMap.put(tableName, ignoreRowStart);
      skipRowEndMap.put(tableName, ignoreRowEnd);
    }
  }

  /**
   * 查询fileId下，所有文件的最新结构发送到后台，生成表和列<br>
   * 补充说明：<br>
   * 1、根据UserConnectionSourceDto对象构建Schema列表<br>
   * 2、根据UserConnectionSourceTableDto对象构建忽略行忽略列的设置
   *
   * @param sourceVo
   * @param sourceDto
   * @param schemaSkipRowArrayMap
   * @param schemaSkipColArrayMap
   * @param schemaIgnoreRowStartMap
   * @param schemaIgnoreRowEndMap
   * @param schemaList
   * @return
   * @author: zhangli
   */
  public void getSchemaListAndIgnoreList(UserConnectionSourceVo sourceVo,
      UserConnectionSourceDto sourceDto, Map<String, Map<String, Integer[]>> schemaSkipRowArrayMap,
      Map<String, Map<String, Integer[]>> schemaSkipColArrayMap,
      Map<String, Map<String, Integer>> schemaIgnoreRowStartMap,
      Map<String, Map<String, Integer>> schemaIgnoreRowEndMap, List<MutableSchema> schemaList) {
    LogMessage logMessage = new LogMessage();
    try {
      // 提取相关参数
      Long uid = sourceVo.getUid();
      String fileId = sourceVo.getFileId();
      String sourceId = sourceVo.getSourceId();

      // 插入日志
      LogMessageUtil.addOperateInfoOfFile(logMessage, fileId);
      LogMessageUtil.addOperateInfoOfExcel(logMessage, sourceVo.getConnectionId(), sourceId,
          sourceDto.getDsCode());
      LogMessageUtil.addBasicInfo(logMessage, String.valueOf(uid), "getSchemaListAndIgnoreList");

      // 验证操作类型，准备不同的查询对象
      Map<String, Object[]> paramMap =
          buildFindSourceParam(uid, fileId, sourceId, sourceDto.getDsCode(),
              sourceVo.getOperateType());

      // 修正编辑时忽略列设置在保存后不生效，更新后才生效问题： by peng.xu 20160803
      // 因为事务原因导致从数据库中获取的source列表不是最新数据，此处先将最新的source放入列表，再从数据库中获取列表加入到新列表，确保在sourceSet中最新的source能够生效
      Set<UserConnectionSourceDto> sourceDtoSet = new HashSet<>();
      // 将当前的Source对象放到List中
      sourceDtoSet.add(sourceDto);
      List<UserConnectionSourceDto> dbSourceDtoList =
          serviceFactory.getUserConnectionSourceService().findSourceDtoByWhereIncludeTables(
              paramMap);
      if (CollectionUtil.isNotEmpty(dbSourceDtoList)) {
        sourceDtoSet.addAll(dbSourceDtoList);
      }

      for (UserConnectionSourceDto sourceDtoEntity : sourceDtoSet) {
        MutableSchema mutableSchema =
            serviceFactory.getDataSourceManagerService().sourceDtoToSchema(sourceDtoEntity);
        if(mutableSchema == null){ continue;}
        schemaList.add(mutableSchema);

        Map<String, Integer[]> skipColMap = new TreeMap<>();
        Map<String, Integer[]> skipRowMap = new TreeMap<>();
        Map<String, Integer> skipRowStartMap = new TreeMap<>();
        Map<String, Integer> skipRowEndMap = new TreeMap<>();

        List<UserConnectionSourceTableDto> tableDtoList = sourceDtoEntity.getTables();
        if (CollectionUtil.isEmpty(tableDtoList)) {
          continue;
        }

        buildSkipInfoByTables(skipColMap, skipRowMap, skipRowStartMap, skipRowEndMap, tableDtoList);

        String sourceDtoId = sourceDtoEntity.getSourceId();
        schemaSkipRowArrayMap.put(sourceDtoId, skipRowMap);
        schemaSkipColArrayMap.put(sourceDtoId, skipColMap);
        schemaIgnoreRowStartMap.put(sourceDtoId, skipRowStartMap);
        schemaIgnoreRowEndMap.put(sourceDtoId, skipRowEndMap);

      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw e;
    } finally {
      logger.info(logMessage.toString());
      ElkLogUtil.info(logMessage.generateJsonString());
    }
  }

  /**
   * @PageCtrl 控制Excel编辑器中展示的列名，规则如下：<br>
   *           <ul>
   *           <li>1、单一sheet中，不出现列名重复的情况</li>
   *           <li>2、单一sheet中，不出现重命名后的列名与其他列名相同的情况</li>
   *           <li>3、多sheet时，在切换sheet时看到的列名同规则1、2</li>
   *           <li>4、当sheet中有列名被系统重命名后，Set column header 显示为 Customize header name</li>
   *           <li>5、Sheet中不能存在超过30字符的列名，系统自动重命名后的列名也不能超过30字符</li>
   *           <li>6、重命名的结构：原始列名+(序号)，例如：A(1)</li>
   *           <li>7、忽略列不影响重命名规则</li>
   *           </ul>
   * @Description: 相同列重新命名列名
   * @param column
   * @Date:2016/07/22
   * @author:zhangli
   * @modifyBy you.zou
   * @modifyDate 2016-11-08 15:00
   * @modifyDesc 优化重名列重命名的代码，有问题已还原
   */
  public boolean processColumnName(Column column[]) {
    boolean result = false;
    if (column.length > 1) {

      /*
       * //用于存放每个列名的index，如果有重名时，index+1，并设置Name为Name(index) Map<String, Integer> nameWithIndexMap =
       * new LinkedHashMap<String, Integer>();
       * 
       * for(int i=0; i<column.length; i++){ MutableColumn mutableColumn = (MutableColumn)
       * column[i]; //列名超了30个字符后，需要截取前30个字符 if (mutableColumn.getName().length() > 30) {
       * mutableColumn.setName(mutableColumn.getName().substring(0, 30)); } String columnName =
       * mutableColumn.getName(); String columnKey = columnName.toLowerCase(); if
       * (StringUtil.isBlank(columnName)) {continue;} if(nameWithIndexMap.containsKey(columnKey)){
       * Integer index = nameWithIndexMap.get(columnKey); index ++;
       * mutableColumn.setName(replaceColumnName(columnName, index));
       * nameWithIndexMap.put(columnKey, index); }else{ nameWithIndexMap.put(columnKey, 0); } }
       */

      // 列名超了30个字符后，需要截取前30个字符
      for (int i = 0; i < column.length; i++) {
        MutableColumn mutableColumn = (MutableColumn) column[i];
        /*
        if (mutableColumn.getName().length() > 30) {
          mutableColumn.setName(mutableColumn.getName().substring(0, 30));
        }
        */
      }
      Set<String> columnSet = new HashSet<>();// 所有不重名列集合
      int countIndex = 0;// 列名计数器
      String columnSchemaName = "";// 原始列名
      for (int i = 0; i < column.length; i++) {
        String name = column[i].getName();
        if (StringUtil.isNotBlank(name)) {
          if (columnSet.contains(name.toLowerCase())) {
            columnSchemaName = name;
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("resetName", name);
            paramMap.put("countIndex", countIndex);
            paramMap = getColumnName(column, paramMap, i, columnSchemaName);
            MutableColumn mutableColumn = (MutableColumn) column[i];
            mutableColumn.setName(paramMap.get("resetName").toString());
            countIndex = 0;
            paramMap.put("countIndex", countIndex);
            columnSchemaName = "";
            result = true;
          } else {
            columnSet.add(column[i].getName().toLowerCase());
          }
        }
      }
    }
    return result;
  }

  public Map<String, Object> getColumnName(Column column[], Map<String, Object> paramMap,
      int index, String columnSchemaName) {

    for (int i = 0; i < column.length; i++) {
      String columnName = column[i].getName();
      if (columnName.equalsIgnoreCase(paramMap.get("resetName").toString()) && i != index) {
        int countIndex = Integer.valueOf(paramMap.get("countIndex").toString()) + 1;
        String resetName = replaceColumnName(columnSchemaName, countIndex);
        paramMap.put("resetName", resetName);
        paramMap.put("countIndex", countIndex);
        paramMap = getColumnName(column, paramMap, i, columnSchemaName);
        break;
      }
    }
    return paramMap;
  }

  /**
   * 列名根据countIndex生成新的列名<br>
   * 例如：columnSchemaName=列名, countIndex=1，生成：列名(1)<br>
   * 当生成出来的列名长度超出30时，需要截取
   * @author li.zhang
   * @date 2016年11月8日 下午3:11:36
   * @param columnSchemaName
   * @param countIndex
   * @return
   */
  public String replaceColumnName(String columnSchemaName, int countIndex) {
    String suffix = "(" + countIndex + ")";
    String resetName = columnSchemaName + suffix;
    if (resetName.length() > 30) {
      resetName = columnSchemaName.substring(0, 30 - suffix.length()) + suffix;
    }
    return resetName;
  }

  /**
   * 根据列名、列序列、用户设置的语言生成新的列名<br>
   * 例如：<br>
   * 列名=NULL、列序列=0、语言=zh_CN<br>
   * 生成：列0<br>
   * 如果列名不为空，则不需要重新生成
   * @author li.zhang
   * @date 2016年11月7日 下午5:48:01
   * @param name
   * @param index
   * @param userSetting
   * @return
   */
  public String buildColumnName(String name, int index, PtoneUserBasicSetting userSetting) {
    String columnName = "";
    if (StringUtil.hasText(name)) {
      columnName = name;
    } else {
      String schemaPreName = "Column";
      if (StringUtil.hasText(userSetting.getLocale())
          && (userSetting.getLocale().equalsIgnoreCase("ja_JP") || userSetting.getLocale()
              .equalsIgnoreCase("zh_CN"))) {
        schemaPreName = "列";
      }
      columnName = schemaPreName + StringUtil.getExcelColumnLabel(index);
    }
    return columnName;
  }

  /**
   * 通过SourceList获取TableSchema信息
   * @author li.zhang
   * @deprecatedBy you.zou
   * @date 2016年11月22日 下午6:46:00
   * @param sourceList
   * @param sourceSchemaMappingMap
   */
  @Deprecated
  public void buildSourceMap(List<UserConnectionSource> sourceList,
      Map<String, Object> sourceSchemaMappingMap) {
    Map<String, Object[]> paramMap = null;

    Map<String, String> orderMap = new HashMap<>();
    orderMap.put("colIndex", "asc");

    for (UserConnectionSource source : sourceList) {
      paramMap = new HashMap<>();
      paramMap.put("sourceId", new Object[] {source.getSourceId()});
      /*
       * List<UserConnectionTableSchema> tableSchemaList =
       * serviceFactory.getUserConnectionTableSchemaService().findByWhere(paramMap, orderMap);
       * sourceSchemaMappingMap.put(source.getSourceId(), tableSchemaList);
       */
    }
  }

  /**
   * @Description: source config的各种处理
   * @param remoteMutableTables
   *        ,sourceList,sourceSchemaMappingMap,userConnection,userConnectionSource
   * @Date:2016/08/04
   * @author:zhangli
   */
  public void processSheet(MutableTable[] remoteMutableTables,
      List<UserConnectionSourceDto> sourceDtoList, UserConnection userConnection,
      UserConnectionSourceDto sourceDto) {
    SourceConfigProcess sourceConfigProcess = new SourceConfigProcess();
    sourceConfigProcess.checkAndUpdateColumn(remoteMutableTables, sourceDtoList);
    sourceConfigProcess.delSheet(remoteMutableTables, sourceDtoList);
    sourceConfigProcess.appendSheet(remoteMutableTables, userConnection, sourceDtoList,
        sourceDto.getFileId());
    // sourceConfigProcess.updateSourceConfigSum(remoteMutableTables, sourceList);
    // sourceConfigProcess.saveSourceList(sourceList);

    /*
     * List<UserConnectionTableSchema> tableSchemaList = (List<UserConnectionTableSchema>)
     * sourceSchemaMappingMap.get(sourceDto .getSourceId()); resultMap.put("sourceList",
     * sourceList);
     */
    // resultMap.put("tableSchemaList", tableSchemaList);
  }

}
