package com.sizzler.datasource.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.metamodel.schema.Column;
import org.apache.metamodel.schema.MutableSchema;
import org.apache.metamodel.schema.MutableTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ptmind.common.utils.CollectionUtil;
import com.ptmind.common.utils.StringUtil;
import com.sizzler.common.sizzler.DataBaseConfig;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.JodaDateUtil;
import com.sizzler.common.utils.SpringContextUtil;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.UserConnectionSourceDto;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableColumnDto;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableDto;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;
import com.sizzler.domain.user.PtoneUserBasicSetting;
import com.sizzler.provider.common.file.PtoneFile;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

/**
 * @ClassName: SourceConfigProcess
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2016/2/25
 * @author: zhangli
 */
public class SourceConfigProcess {

  private static Logger logger = LoggerFactory.getLogger(SourceConfigProcess.class);

  // public static List<UserConnectionSource> sourceList;
  // public static Map<String, Object> sourceSchemaMappingMap;
  // private ServiceFactory serviceFactory;

  /**
   * constructor
   *
   * @author: zhangli
   * @param sourceList
   */
  // public SourceConfigProcess(List<UserConnectionSource> sourceList,Map<String, Object>
  // sourceSchemaMappingMap) {
  // this.sourceList = sourceList;
  // this.sourceSchemaMappingMap = sourceSchemaMappingMap;
  // this.serviceFactory = SpringContextUtil.getBean("serviceFactory");
  // }

  /**
   * 初始化
   *
   * @author: zhangli
   * @param sourceList
   * @return
   */
  /*
   * public static SourceConfigProcess create(List<UserConnectionSource> sourceList,Map<String,
   * Object> sourceSchemaMappingMap) { logger.info("init sourceList"); return new
   * SourceConfigProcess(sourceList,sourceSchemaMappingMap); }
   */

  // public static List<UserConnectionSource> getSourceList() {
  // return sourceList;
  // }

  // public static Map<String, Object> getSourceSchemaMappingMap() {
  // return sourceSchemaMappingMap;
  // }

  /**
   * 更新config中的colSum,rowSum
   * 
   * 功能移到appendSheet中使用
   *
   * @deprecatedBy you.zou
   * @author: zhangli
   * @param remoteMutableTables
   * @return
   */
  @Deprecated
  public List<UserConnectionSource> updateSourceConfigSum(MutableTable[] remoteMutableTables,
      List<UserConnectionSource> sourceList) {
    logger.info("update colSum,rowSum");
    for (UserConnectionSource source : sourceList) {
      List<Map> configList = JSON.parseArray(source.getConfig(), Map.class);

      for (int i = 0; i < remoteMutableTables.length; i++) {
        for (int j = 0; j < configList.size(); j++) {
          if (remoteMutableTables[i].getName().equals(configList.get(j).get("name").toString())) {
            configList.get(j).put("rowSum", remoteMutableTables[i].getRowCount());
            configList.get(j).put("colSum", remoteMutableTables[i].getColumnCount());
            source.setConfig(JSON.toJSONString(configList));
            break;
          }
        }
      }
    }
    return sourceList;
  }
  
  /**
   * 更新内存中的表行数、列数，并同步到数据库中
   * @author you.zou
   * @date 2016年11月25日 上午11:53:37
   * @param remoteMutableTable
   * @param sourceDtos
   */
  public void updateAndSaveTableDto(MutableTable remoteMutableTable, List<UserConnectionSourceDto> sourceDtos){
    if(remoteMutableTable == null || CollectionUtil.isEmpty(sourceDtos)){ return ;}
    ServiceFactory serviceFactory = SpringContextUtil.getBean("serviceFactory");
     for(UserConnectionSourceDto sourceDto : sourceDtos){
       UserConnectionSourceTableDto tableDto = sourceDto.getTables().get(0);
       tableDto.setColSum(String.valueOf(remoteMutableTable.getColumnCount()));
       tableDto.setRowSum(String.valueOf(remoteMutableTable.getRowCount()));
       //更新到数据库
       serviceFactory.getUserConnectionSourceTableService().update(tableDto.parseToTable());
     }
  }

  /**
   * 将新的sheet放到source中 更新source下每个table的rowSum、colSum等配置，并同步到数据库中
   * @author you.zou
   * @date 2016年11月22日 下午4:51:32
   * @param remoteMutableTables
   * @param userConnection
   * @param sourceDtoList
   * @param fileId
   */
  public void appendSheet(MutableTable[] remoteMutableTables, UserConnection userConnection,
      List<UserConnectionSourceDto> sourceDtoList, String fileId) {
    ServiceFactory serviceFactory = SpringContextUtil.getBean("serviceFactory");

    if (CollectionUtil.isEmpty(sourceDtoList)) {
      return;
    }

    for (UserConnectionSourceDto sourceDto : sourceDtoList) {
      if (sourceDto == null) {
        continue;
      }

      List<UserConnectionSourceTableDto> tableDtos = sourceDto.getTables();
      // 声明新增的tableDto列表
      MutableSchema newAddMutableSchema = new MutableSchema();
      List<MutableTable> newAddTables = new ArrayList<>();
      PtoneFile ptoneFile = new PtoneFile();
      for (MutableTable mutableTable : remoteMutableTables) {
        if (mutableTable == null) {
          continue;
        }
        boolean isNewTable = true;
        for (UserConnectionSourceTableDto tableDto : tableDtos) {
          if (tableDto == null) {
            continue;
          }
          if (mutableTable.getName().equals(tableDto.getName())) {
            // 如果当前远程表名称在TableDtos中存在，则设置该远程表不是新的表
            isNewTable = false;
            // 另外可以顺带更新当前表的rowSum、colSum
            tableDto.setRowSum(String.valueOf(mutableTable.getRowCount()));
            tableDto.setColSum(String.valueOf(mutableTable.getColumnCount()));
            serviceFactory.getUserConnectionSourceTableService().update(tableDto.parseToTable());
            break;
          }
        }
        if (isNewTable) {
          // 如果是新增的表，则将mutableTable放到newAddTables列表中
          newAddTables.add(mutableTable);
        }
      }
      if (CollectionUtil.isEmpty(newAddTables)) {
        continue;
      }
      newAddMutableSchema.setTables(newAddTables);
      ptoneFile.setSchema(newAddMutableSchema);
      // 将ptoneFile对象构建成SourceVo对象
      UserConnectionSourceVo sourceVo =
          serviceFactory.getDataSourceBuild().buildSourceVoByPtoneFile(ptoneFile, userConnection);
      // 补充细节
      sourceVo.setFileId(fileId);
      sourceVo.setUid(sourceDto.getUid());
      sourceVo.setConnectionId(sourceDto.getConnectionId());
      sourceVo.setSourceId(sourceDto.getSourceId());
      sourceVo.setDsId(sourceDto.getDsId());
      UserConnectionSourceDto newSourceDto = new UserConnectionSourceDto(sourceDto.parseToSource());
      newSourceDto.setTables(null);
      serviceFactory.getDataSourceBuild().buildSourceDtoBySourceVo(sourceVo, newSourceDto);
      serviceFactory.getDataSourceBuild().saveTablesAndColumns(newSourceDto, false, true);
      sourceDto.getTables().addAll(newSourceDto.getTables());
    }
  }


  /**
   * 删除远端没有的sheet
   *
   * @author: zhangli
   * @param remoteMutableTables
   * @return
   */
  public void delSheet(MutableTable[] remoteMutableTables,
      List<UserConnectionSourceDto> sourceDtoList) {
    ServiceFactory serviceFactory = SpringContextUtil.getBean("serviceFactory");
    logger.info("del Sheet");
    for (UserConnectionSourceDto sourceDto : sourceDtoList) {

      List<UserConnectionSourceTableDto> tableDtos = sourceDto.getTables();
      if (CollectionUtil.isEmpty(tableDtos)) {
        continue;
      }

      List<UserConnectionSourceTableDto> delTableDtos =
          new ArrayList<UserConnectionSourceTableDto>();

      for (UserConnectionSourceTableDto tableDto : tableDtos) {
        if (tableDto == null) {
          continue;
        }
        boolean delSheetFlag = true;
        for (MutableTable remoteMutableTable : remoteMutableTables) {
          if (remoteMutableTable.getName().equals(tableDto.getName())) {
            delSheetFlag = false;
            break;
          }
        }

        if (delSheetFlag) {
          delTableDtos.add(tableDto);
          Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
          paramMap.put("tableId", new Object[] {tableDto.getTableId()});
          serviceFactory.getUserConnectionSourceTableService().delete(paramMap);
          serviceFactory.getUserConnectionSourceTableColumnService().delete(paramMap);
        }
      }

      if (CollectionUtil.isNotEmpty(delTableDtos)) {
        // 从内存中清理掉被删除的table
        for (UserConnectionSourceTableDto delTableDto : delTableDtos) {
          sourceDto.getTables().remove(delTableDto);
        }
      }

    }
  }

  /**
   * 原方法名：checkSheetColumn<br>
   * 远程与本地的表结构做对比，更新、删除、增加column
   * @author you.zou
   * @date 2016年11月21日 下午5:05:53
   * @param remoteMutableTables
   * @param sourceDtoList
   */
  public void checkAndUpdateColumn(MutableTable[] remoteMutableTables,
      List<UserConnectionSourceDto> sourceDtoList) {
    ServiceFactory serviceFactory = SpringContextUtil.getBean("serviceFactory");
    logger.info("check Sheet Column");

    // 由于前面的逻辑限制了SourceList只可能属于一个用户，那么只需要获取一次这个用户的配置就可以了 modify by you.zou
    PtoneUserBasicSetting userSetting = null;

    for (UserConnectionSourceDto sourceDto : sourceDtoList) {
      if (userSetting == null) {
        userSetting = serviceFactory.getUserSettingService().get(sourceDto.getUid().toString());
      }

      List<UserConnectionSourceTableDto> tableDtos = sourceDto.getTables();

      for (int i = 0; i < remoteMutableTables.length; i++) {
        MutableTable remoteMutableTable = remoteMutableTables[i];

        for (int j = 0; j < tableDtos.size(); j++) {
          UserConnectionSourceTableDto dbTableDto = tableDtos.get(j);
          if (remoteMutableTable.getName().equals(dbTableDto.getName())) {
            Column[] remoteColumns = remoteMutableTable.getColumns();
            serviceFactory.getDataSourceBuild().processColumnName(remoteColumns);
            List<UserConnectionSourceTableColumnDto> dbColumnDtos = dbTableDto.getColumns();
            int dbColumnDtosSize = dbColumnDtos.size();
            // 列的总数相等时，只更新每一列,更新内容为
            if (remoteColumns.length == dbColumnDtosSize) {
              for (int k = 0; k < dbColumnDtosSize; k++) {
                UserConnectionSourceTableColumnDto dbColumnDto = dbColumnDtos.get(k);
                // 如果用户更新过，则不再更新
                if (isUpdateColumn(dbColumnDto, k)) {
                  updateColumn(remoteColumns[k], dbColumnDto, userSetting);
                }
              }
            }
            // 远程列的总数大于本地列时，更新本地每一列,插入远程多的列
            if (remoteColumns.length > dbColumnDtosSize) {
              for (int k = 0; k < remoteColumns.length; k++) {
                if (k <= dbColumnDtosSize - 1) {
                  UserConnectionSourceTableColumnDto dbColumnDto = dbColumnDtos.get(k);
                  updateColumn(remoteColumns[k], dbColumnDto, userSetting);
                } else {
                  addColumn(sourceDto, dbTableDto, remoteColumns[k], userSetting, null);
                }
              }
            }
            // 远程列的总数小于本地列时，更新本地每一列,删除本地多的列
            if (remoteColumns.length < dbColumnDtosSize) {
              // 记录被删除的列对象
              List<UserConnectionSourceTableColumnDto> delColumnDtos =
                  new ArrayList<UserConnectionSourceTableColumnDto>();
              for (int k = 0; k < dbColumnDtosSize; k++) {
                UserConnectionSourceTableColumnDto dbColumnDto = dbColumnDtos.get(k);
                if (k <= remoteColumns.length - 1) {
                  updateColumn(remoteColumns[k], dbColumnDto, userSetting);
                } else {
                  delColumn(dbColumnDto.getColId());
                  delColumnDtos.add(dbColumnDto);
                }
              }
              // 从内存对象中将被删除的列清理出去
              for (UserConnectionSourceTableColumnDto delColumnDto : delColumnDtos) {
                dbTableDto.getColumns().remove(delColumnDto);
              }
            }
            break;
          }
        }
      }
    }
  }

  /**
   * 关系型数据库只有一个表、一个Source<br>
   * 同步远端的mysql的列结构(按字段名)<br>
   * 更新完成后需要根据colIndex对列进行排序
   * @author li.zhang
   * @modifyBy you.zou
   * @date 2016年11月25日 上午9:56:13
   * @param remoteMutableTable
   * @param sourceDtos
   */
  public void checkRdsColumn(MutableTable remoteMutableTable,
      List<UserConnectionSourceDto> sourceDtos) {
    ServiceFactory serviceFactory = SpringContextUtil.getBean("serviceFactory");

    // 由于前面的逻辑限制了SourceList只可能属于一个用户，那么只需要获取一次这个用户的配置就可以了 modify by you.zou
    PtoneUserBasicSetting userSetting = null;
    for (UserConnectionSourceDto sourceDto : sourceDtos) {
      if(userSetting == null){
        userSetting = serviceFactory.getUserSettingService().get(sourceDto.getUid().toString());
      }

      UserConnectionSourceTableDto dbTableDto = sourceDto.getTables().get(0);// 提取第一个TableDto对象

      // 由于在获取表信息时，就是通过表名去获取，所以remoteMutableTable与tableDto的表名必定一致，所以这里不判断表名是否相同
      Column[] remoteColumns = remoteMutableTable.getColumns();
      List<UserConnectionSourceTableColumnDto> dbColumnDtos = dbTableDto.getColumns();
      int dbColumnDtosSize = dbColumnDtos.size();

      // 更新本地每一列,插入远程多的列
      for (int n = 0; n < remoteColumns.length; n++) {
        boolean flag = true;
        for (int k = 0; k < dbColumnDtosSize; k++) {
          UserConnectionSourceTableColumnDto dbColumn = dbColumnDtos.get(k);
          if (dbColumn.getCode().equals(remoteColumns[n].getName())) {
            dbColumn.setIsIgnore(Constants.inValidate);//置为不忽略,最后统一忽略（应对插入列，删除列情况）
            if (isUpdateDBColumn(dbColumn)) {
              updateDBColumn(remoteColumns[n], dbColumn, userSetting, Long.valueOf(n));
            }else{//总是要更新col index
              dbColumn.setColId(dbColumn.getColId());
              dbColumn.setColIndex(Long.valueOf(n));
              serviceFactory.getUserConnectionSourceTableColumnService().update(dbColumn.parseToColumn());
            }
            flag = false;
            break;
          }
        }
        if (flag) {
          addColumn(sourceDto, dbTableDto, remoteColumns[n], userSetting, Long.valueOf(n));
        }
      }
      // 记录被删除的列对象
      List<UserConnectionSourceTableColumnDto> delColumnDtos =
          new ArrayList<UserConnectionSourceTableColumnDto>();
      // 删除本地多的列
      for (int k = 0; k < dbColumnDtosSize; k++) {
        boolean flag = true;
        UserConnectionSourceTableColumnDto dbColumn = dbColumnDtos.get(k);
        for (int n = 0; n < remoteColumns.length; n++) {
          if (dbColumn.getCode().equals(remoteColumns[n].getName())) {
            flag = false;
            break;
          }
        }
        if (flag) {
          delColumnDtos.add(dbColumn);
          delColumn(dbColumn.getColId());
        }
      }
      // 从内存对象中将被删除的列清理出去
      for (UserConnectionSourceTableColumnDto delColumnDto : delColumnDtos) {
        dbTableDto.getColumns().remove(delColumnDto);
      }
      //对内存中的列进行排序
      Collections.sort(dbColumnDtos, new Comparator<UserConnectionSourceTableColumnDto>() {
        @Override
        public int compare(UserConnectionSourceTableColumnDto o1, UserConnectionSourceTableColumnDto o2) {
          return (int) (o1.getColIndex() - o2.getColIndex());
        }
      });
      List<Integer> ignoreColList = dbTableDto.getIgnoreColList();//获取列忽略列表
      if(CollectionUtil.isNotEmpty(ignoreColList) && CollectionUtil.isNotEmpty(dbColumnDtos)){//根据忽略列表忽略列（暂时把列号定为标识）,须在列删除排序以后操作
        for (int i = 0; i < ignoreColList.size(); i++) {
          Integer colIndex = ignoreColList.get(i);
          if(colIndex < dbColumnDtos.size()){
            UserConnectionSourceTableColumnDto dbColumn = dbColumnDtos.get(colIndex);
            if(dbColumn.getIsIgnore().equals(Constants.inValidate)){
              dbColumn.setIsIgnore(Constants.validate);
              serviceFactory.getUserConnectionSourceTableColumnService().update(dbColumn.parseToColumn());
            }
          }
        }
      }
    }
  }


  /**
   * 保存最新的source Config
   *
   * 功能移动到appendSheet中
   *
   * @deprecatedBy you.zou
   *
   * @author: zhangli
   * @return
   */
  @Deprecated
  public List<UserConnectionSource> saveSourceList(List<UserConnectionSource> sourceList) {
    ServiceFactory serviceFactory = SpringContextUtil.getBean("serviceFactory");
    logger.info("update source config");
    for (UserConnectionSource source : sourceList) {
      serviceFactory.getUserConnectionSourceService().updateConnectionSourceConfig(
          source.getConfig(), source.getSourceId());
    }
    return sourceList;
  }

  /**
   * 验证该列是否有被用户自己更新过，验证的规则如下：<br>
   * 2、UserConnectionSourceTableColumnDto的colIndex与参数colIndex一致<br>
   * 3、UserConnectionSourceTableColumnDto的isCustome值是1<br>
   * 该验证用于判断该列是否需要根据新文件更新<br>
   * <strong> PS：当用户对该列设置了列名称、列数据类型时，就表名该列已自定义;<br>
   * 该方法只有在更新文件的时候才会被调用 </strong>
   * 
   * @author li.zhang
   * @date 2016年11月7日 下午5:35:20
   * @param colIndex
   * @return
   */
  public boolean isUpdateColumn(UserConnectionSourceTableColumnDto dbColumnDto, int colIndex) {
    if (dbColumnDto.getColIndex() - colIndex == 0
        && StringUtil.isNotBlank(dbColumnDto.getIsCustom())
        && dbColumnDto.getIsCustom().equals(Constants.validate)) {
      return false;
    }
    return true;
  }

  public boolean isUpdateDBColumn(UserConnectionSourceTableColumnDto dbColumnDto) {
    if (StringUtil.isNotBlank(dbColumnDto.getIsCustom())
            && dbColumnDto.getIsCustom().equals(Constants.validate)) {
      return false;
    }
    return true;
  }


  /*
   * 更新内容 user_connection_table_schema :type,data_type,name,data_format
   * user_connection_metrics_dimension :type,data_type,name,data_format user_connection_table_header
   * :name
   */
  /**
   * 根据远端文件的column信息去更新数据库中的column信息<br>
   * 前提是远端column与数据库column信息不一致<br>
   * 更新的表：UserConnectionSourceTableColumnDto<br>
   * 更新的字段：type、data_type、name、data_format
   * @author li.zhang
   * @modifyBy you.zou
   * @modifyDesc 增加isEqualsOfRemoteWithDBColumn验证
   * @date 2016年11月7日 下午6:14:54
   * @param remoteColumns
   * @param userSetting
   */
  public void updateColumn(Column remoteColumns, UserConnectionSourceTableColumnDto dbColumnDto,
      PtoneUserBasicSetting userSetting) {
    ServiceFactory serviceFactory = SpringContextUtil.getBean("serviceFactory");
    String colId = dbColumnDto.getColId();
    String dataType = remoteColumns.getType().getName();
    // 生成远端的列名
    String name =
        serviceFactory.getDataSourceBuild().buildColumnName(remoteColumns.getName(),
            remoteColumns.getColumnNumber(), userSetting);
    String format = remoteColumns.getFormat();
    if (isEqualsOfRemoteWithDBColumn(dataType, name, format, dbColumnDto)) {
      return;
    }
    String type =
        serviceFactory.getPtoneBasicDictCache()
            .getDictItemByCode("data_type_" + remoteColumns.getType().getName()).getName();
    dbColumnDto.setColId(colId);
    dbColumnDto.setName(name);
    dbColumnDto.setDataType(dataType);
    dbColumnDto.setType(type);
    dbColumnDto.setDataFormat(format);
    serviceFactory.getUserConnectionSourceTableColumnService().update(dbColumnDto.parseToColumn());
  }

  /**
   * 针对关系型数据库的列更新
   * @date 2016/11/23
   * @author li.zhang
   * @param remoteColumns
   * @param userSetting
   * @param colIndex
   */
  public void updateDBColumn(Column remoteColumns, UserConnectionSourceTableColumnDto dbColumnDto,
      PtoneUserBasicSetting userSetting, Long colIndex) {
    ServiceFactory serviceFactory = SpringContextUtil.getBean("serviceFactory");
    String colId = dbColumnDto.getColId();
    String dataType = remoteColumns.getType().getName();
    // 生成远端的列名
    String name =
        serviceFactory.getDataSourceBuild().buildColumnName(remoteColumns.getName(),
            remoteColumns.getColumnNumber(), userSetting);
    String format = remoteColumns.getFormat();
    String type =
        serviceFactory.getPtoneBasicDictCache()
            .getDictItemByCode("data_type_" + remoteColumns.getType().getName()).getName();
    dbColumnDto.setColId(colId);
    dbColumnDto.setName(name);
    dbColumnDto.setDataType(dataType);
    dbColumnDto.setType(type);
    dbColumnDto.setDataFormat(format);
    dbColumnDto.setColIndex(colIndex);
    serviceFactory.getUserConnectionSourceTableColumnService().update(dbColumnDto.parseToColumn());
  }

  /**
   * 验证远端Column与数据库存储的Column的信息是否一致
   * @author you.zou
   * @date 2016年11月7日 下午6:13:26
   * @param dataType
   * @param name
   * @param format
   * @return
   */
  public boolean isEqualsOfRemoteWithDBColumn(String dataType, String name, String format,
      UserConnectionSourceTableColumnDto dbColumnDto) {
    if (dbColumnDto == null || dataType == null || name == null || format == null) {
      return false;
    }
    String dbFormat = dbColumnDto.getDataFormat();
    if (dataType.equals(dbColumnDto.getType()) && name.equals(dbColumnDto.getName())
        && (format.equals(dbFormat) || (dbFormat == null && format.equals("")))) {
      return true;
    }
    return false;
  }

  public void delColumn(String colId) {
    ServiceFactory serviceFactory = SpringContextUtil.getBean("serviceFactory");
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("colId", new Object[] {colId});
    serviceFactory.getUserConnectionSourceTableColumnService().delete(paramMap);
  }


  /**
   * 增加列<br>
   * 新增的列存入数据库，事务待提交状态<br>
   * 新增的列放入内存对象SourceDto中<br>
   * 增加colIndex字段，用于提供给MySQL关系型数据库使用
   * @author you.zou
   * @date 2016年11月24日 上午9:32:31
   * @param sourceDto
   * @param tableDto
   * @param rColumn
   * @param userSetting
   */
  public void addColumn(UserConnectionSourceDto sourceDto, UserConnectionSourceTableDto tableDto,
      Column rColumn, PtoneUserBasicSetting userSetting, Long colIndex) {

    ServiceFactory serviceFactory = SpringContextUtil.getBean("serviceFactory");
    String columnName =
        serviceFactory.getDataSourceBuild().buildColumnName(rColumn.getName(),
            rColumn.getColumnNumber(), userSetting);
    String colId = UUID.randomUUID().toString();
    String dsCode = sourceDto.getDsCode();

    UserConnectionSourceTableColumn column = new UserConnectionSourceTableColumn();
    column.setColId(colId);
    column.setDataFormat(rColumn.getFormat());
    column.setName(columnName);
    if (null != colIndex) {
      column.setColIndex(colIndex);
    } else {
      column.setColIndex(new Long(rColumn.getColumnNumber()));
    }
    if(DataBaseConfig.isDatabase(dsCode) || dsCode.equalsIgnoreCase(DsConstants.DS_CODE_BIGQUERY)){
      //bigquery是特殊的，所以需要单独添加判断
      column.setCode(columnName);
    }else{
      column.setCode(colId);
    }
    column.setDataType(rColumn.getType().getName());
    column.setType(serviceFactory.getPtoneBasicDictCache()
        .getDictItemByCode("data_type_" + rColumn.getType().getName()).getName());
    column.setConnectionId(sourceDto.getConnectionId());
    column.setSourceId(sourceDto.getSourceId());
    column.setUid(sourceDto.getUid());
    column.setDsCode(dsCode);
    column.setDsId(sourceDto.getDsId());
    column.setSpaceId(sourceDto.getSpaceId());
    column.setCreateTime(JodaDateUtil.getCurrentDateTime());
    column.setStatus(Constants.validate);
    column.setTableId(tableDto.getTableId());
    // 将新增的列放到内存对象中
    tableDto.getColumns().add(new UserConnectionSourceTableColumnDto(column));
    serviceFactory.getUserConnectionSourceTableColumnService().save(column);
  }

}
