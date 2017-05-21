package com.sizzler.dao.ds.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.dao.ds.UserConnectionSourceTableColumnDao;
import com.sizzler.domain.ds.UserConnectionSourceTableColumn;
import com.sizzler.system.Constants;

@Repository
public class UserConnectionSourceTableColumnDaoImpl extends
    DaoBaseInterfaceImpl<UserConnectionSourceTableColumn, String> implements
    UserConnectionSourceTableColumnDao {

  @Override
  public UserConnectionSourceTableColumn get(String colId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("colId", new Object[] {colId});
    paramMap.put("status", new Object[] {Constants.validate});
    return this.getByWhere(paramMap);
  }
}
