package com.sizzler.dao.ds.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.dao.ds.UserConnectionSourceDao;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.dto.UserAccountSource;

@Repository
public class UserConnectionSourceDaoImpl extends DaoBaseInterfaceImpl<UserConnectionSource, String>
    implements UserConnectionSourceDao {
  @Override
  public void updateLastModifiedDate(String sourceId, long lastModifiedDate) {
    String sql = "UPDATE user_connection_source SET last_modified_date= ? WHERE  source_id = ?";
    Object param[] = {lastModifiedDate, sourceId};
    DataOperationUtils.insert(sql, param);
  }

  @Override
  public void updateUpdateTime(String sourceId, long updateTime) {
    String sql = "UPDATE user_connection_source SET update_time= ? WHERE  source_id = ?";
    Object param[] = {updateTime, sourceId};
    DataOperationUtils.insert(sql, param);
  }

  @Override
  public List<UserAccountSource> getUserAccountSource(String uid, String dsId) {
    String sql =
        "SELECT u.name accountName,s.* FROM user_connection u,user_connection_source s WHERE "
            + "u.connection_id = s.connection_id "
            + "AND u.uid = :uid AND u.status = '1' and s.status = '1' AND u.ds_id= :dsId ORDER BY s.create_time desc";
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("uid", uid);
    paramMap.put("dsId", dsId);
    return (List<UserAccountSource>) DataOperationUtils.queryForList(sql, paramMap,
        UserAccountSource.class);
  }

  @Override
  public void updateConnectionSourceRemoteStatus(String status, String fileId) {
    String sql = "UPDATE user_connection_source SET remote_status = ? WHERE file_id = ?";
    Object param[] = {status, fileId};
    DataOperationUtils.insert(sql, param);
  }

  @Override
  public void updateConnectionSourceConfig(String config, String sourceId) {
    String sql = "UPDATE user_connection_source SET config = ? WHERE source_Id = ?";
    Object param[] = {config, sourceId};
    DataOperationUtils.insert(sql, param);
  }

  @Override
  public void updateConnectionSourcePath(String path, String connectionId) {
    String sql = "UPDATE user_connection_source SET remote_path = ? WHERE connection_id = ?";
    Object param[] = {path, connectionId};
    DataOperationUtils.insert(sql, param);
  }
  
  @Override
  public void updateConnectionSourceStatusByConnectionId(String status, String connectionId){
    String sql = "UPDATE user_connection_source SET status = ? WHERE connection_id = ?";
    Object param[] = {status, connectionId};
    DataOperationUtils.insert(sql, param);
  }

  public void updateConnectionSourceRemoteStatusBySourceId(String sourceId, String status) {
    String sql = "UPDATE user_connection_source SET remote_status = ? WHERE  source_id = ?";
    Object param[] = {status, sourceId};
    DataOperationUtils.insert(sql, param);
  }

  @Override
  public List<UserAccountSource> getSpaceAccountSource(String spaceId, String dsId) {
    String sql =
        "SELECT u.name accountName,s.* FROM user_connection u,user_connection_source s WHERE "
            + "u.connection_id = s.connection_id "
            + "AND u.space_id = :spaceId AND u.status = '1' and s.status = '1' AND u.ds_id= :dsId ORDER BY s.create_time desc";
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("spaceId", spaceId);
    paramMap.put("dsId", dsId);
    return (List<UserAccountSource>) DataOperationUtils.queryForList(sql, paramMap,
        UserAccountSource.class);
  }

  @Override
  public void deleteSourceByConnectionId(String connectionId) {
    String sql = "UPDATE user_connection_source SET status = '0' WHERE connection_id = ?";
    Object param[] = {connectionId};
    DataOperationUtils.insert(sql, param);
  }
}
