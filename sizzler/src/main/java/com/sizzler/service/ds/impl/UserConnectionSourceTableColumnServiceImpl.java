package com.sizzler.service.ds.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptmind.common.utils.CollectionUtil;
import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.ds.UserConnectionSourceTableColumnDao;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableColumnDto;
import com.sizzler.service.ds.UserConnectionSourceTableColumnService;
import com.sizzler.system.Constants;

@Service
public class UserConnectionSourceTableColumnServiceImpl extends
    ServiceBaseInterfaceImpl<UserConnectionSourceTableColumn, String> implements
    UserConnectionSourceTableColumnService {

  @Autowired
  private UserConnectionSourceTableColumnDao userConnectionSourceTableColumnDao;


  @Override
  public UserConnectionSourceTableColumn getAvailableColumn(String colId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("colId", new Object[] {colId});
    paramMap.put("status", new Object[] {Constants.validate});
    paramMap.put("isIgnore", new Object[] {Constants.inValidate});
    return userConnectionSourceTableColumnDao.getByWhere(paramMap);
  }

  @Override
  public List<UserConnectionSourceTableColumn> findBySourceId(String sourceId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("sourceId", new Object[] {sourceId});
    paramMap.put("status", new Object[] {Constants.validate});
    List<UserConnectionSourceTableColumn> tableSchemaList =
        userConnectionSourceTableColumnDao.findByWhere(paramMap);
    if (tableSchemaList != null) {
      sort(tableSchemaList);
    }
    return tableSchemaList;
  }

  @Override
  public List<UserConnectionSourceTableColumn> findByTableId(String tableId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("tableId", new Object[] {tableId});
    paramMap.put("status", new Object[] {Constants.validate});
    List<UserConnectionSourceTableColumn> schemaList =
        userConnectionSourceTableColumnDao.findByWhere(paramMap);
    // 按tableId、headerIndex排序
    if (schemaList != null) {
      sort(schemaList);
    }
    return schemaList;
  }

  /**
   * 用于column的排序，排序规则根据colIndex及tableId来排序
   * @author you.zou
   * @date 2016年11月18日 下午4:31:48
   * @param schemaList
   */
  public void sort(List<UserConnectionSourceTableColumn> schemaList) {
    Collections.sort(schemaList, new Comparator<UserConnectionSourceTableColumn>() {
      @Override
      public int compare(UserConnectionSourceTableColumn o1, UserConnectionSourceTableColumn o2) {
        if (o1.getTableId().equals(o2.getTableId())) {
          return o1.getColIndex().compareTo(o2.getColIndex());
        } else {
          return o1.getTableId().compareTo(o2.getTableId());
        }
      }
    });
  }

  /**
   * 将UserConnectionSourceTableColumn集合转换为UserConnectionSourceTableColumnDto集合
   * @author you.zou
   * @date 2016年11月18日 下午4:39:38
   * @param columns
   * @return
   */
  public List<UserConnectionSourceTableColumnDto> convert(
      List<UserConnectionSourceTableColumn> columns) {
    List<UserConnectionSourceTableColumnDto> columnDtos =
        new ArrayList<UserConnectionSourceTableColumnDto>();
    if (CollectionUtil.isEmpty(columns)) {
      return columnDtos;
    }
    for (UserConnectionSourceTableColumn column : columns) {
      columnDtos.add(new UserConnectionSourceTableColumnDto(column));
    }
    return columnDtos;
  }

  @Override
  public List<UserConnectionSourceTableColumnDto> findByTableIdOfDto(String tableId) {
    List<UserConnectionSourceTableColumn> columns = findByTableId(tableId);
    return convert(columns);
  }

  @Override
  public List<UserConnectionSourceTableColumn> findByTableId(String tableId, String[] typeArray) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("tableId", new Object[] {tableId});
    if (typeArray != null) {
      paramMap.put("type", typeArray);
    }
    paramMap.put("status", new Object[] {Constants.validate});
    paramMap.put("isIgnore", new Object[] {Constants.inValidate});

    Map<String, String> orderMap = new HashMap<>();
    orderMap.put("type", "desc");

    return userConnectionSourceTableColumnDao.findByWhere(paramMap, orderMap);
  }

  @Override
  public List<UserConnectionSourceTableColumnDto> findByTableIdOfDto(String tableId,
      String[] typeArray) {
    List<UserConnectionSourceTableColumn> columns = findByTableId(tableId, typeArray);
    return convert(columns);
  }
  
  @Override
  public void updateStatusBySourceId(String status, String sourceId){
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("sourceId", new Object[] {sourceId});
    Map<String, String> updateMap = new HashMap<>();
    updateMap.put("status", status);
    userConnectionSourceTableColumnDao.update(paramMap, updateMap);
  }
  
  @Override
  public void updateStatusByConnectionId(String status, String connectionId){
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("connectionId", new Object[] {connectionId});
    Map<String, String> updateMap = new HashMap<>();
    updateMap.put("status", status);
    userConnectionSourceTableColumnDao.update(paramMap, updateMap);
  }

}
