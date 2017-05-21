package com.sizzler.service.ds.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.dao.ds.UserConnectionSourceTableDao;
import com.sizzler.domain.ds.UserConnectionSourceTable;
import com.sizzler.domain.ds.dto.UserConnectionSourceTableDto;
import com.sizzler.service.ds.UserConnectionSourceTableService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

@Service
public class UserConnectionSourceTableServiceImpl extends
    ServiceBaseInterfaceImpl<UserConnectionSourceTable, String> implements
    UserConnectionSourceTableService {

  @Autowired
  private UserConnectionSourceTableDao userConnectionSourceTableDao;
  @Autowired
  private ServiceFactory serviceFactory;

  @Override
  public List<UserConnectionSourceTableDto> findBySourceIdOfDto(String sourceId) {
    Map<String, String> orderMap = new HashMap<String, String>();
    orderMap.put("id", "asc");
    List<UserConnectionSourceTable> tables = findBySourceId(sourceId, orderMap);
    List<UserConnectionSourceTableDto> tableDtos = new ArrayList<UserConnectionSourceTableDto>();
    for (UserConnectionSourceTable table : tables) {
      tableDtos.add(new UserConnectionSourceTableDto(table));
    }
    return tableDtos;
  }

  @Override
  public List<UserConnectionSourceTable> findBySourceId(String sourceId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("sourceId", new Object[] {sourceId});
    paramMap.put("status", new Object[] {Constants.validate});
    return findByWhere(paramMap);
  }
  
  @Override
  public List<UserConnectionSourceTable> findBySourceId(String sourceId, Map<String, String> orderMap){
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("sourceId", new Object[] {sourceId});
    paramMap.put("status", new Object[] {Constants.validate});
    return findByWhere(paramMap, orderMap);
  }

  @Override
  public List<UserConnectionSourceTableDto> findBySourceIdOfDtoIncludeColumns(String sourceId) {
    List<UserConnectionSourceTableDto> tableDtos = findBySourceIdOfDto(sourceId);
    for (UserConnectionSourceTableDto tableDto : tableDtos) {
      // 遍历table获取
      tableDto.setColumns(serviceFactory.getUserConnectionSourceTableColumnService()
          .findByTableIdOfDto(tableDto.getTableId()));
    }
    return tableDtos;
  }

  @Override
  public UserConnectionSourceTableDto getByTableIdOfDtoIncludeClumns(String tableId) {
    UserConnectionSourceTableDto tableDto = getByTableIdOfDto(tableId);
    tableDto.setColumns(serviceFactory.getUserConnectionSourceTableColumnService()
        .findByTableIdOfDto(tableId));
    return tableDto;
  }
  
  @Override
  public UserConnectionSourceTableDto getByTableIdOfDto(String tableId){
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("tableId", new Object[] {tableId});
    paramMap.put("status", new Object[] {Constants.validate});
    UserConnectionSourceTableDto tableDto = new UserConnectionSourceTableDto(getByWhere(paramMap));
    return tableDto;
  }
  
  @Override
  public void updateStatusBySourceId(String status, String sourceId){
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("sourceId", new Object[] {sourceId});
    Map<String, String> updateMap = new HashMap<>();
    updateMap.put("status", status);
    userConnectionSourceTableDao.update(paramMap, updateMap);
  }

  @Override
  public void updateStatusByConnectionId(String status, String connectionId){
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("connectionId", new Object[] {connectionId});
    Map<String, String> updateMap = new HashMap<>();
    updateMap.put("status", status);
    userConnectionSourceTableDao.update(paramMap, updateMap);
  }

}
