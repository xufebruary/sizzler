package com.sizzler.dao.ds.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sizzler.common.base.dao.DaoBaseInterfaceImpl;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.common.utils.DataOperationUtils;
import com.sizzler.dao.ds.PtoneUserConnectionDao;
import com.sizzler.domain.ds.dto.DsContentView;
import com.sizzler.system.Constants;

@Repository("ptoneUserConnectionDao")
public class PtoneUserConnectionDaoImpl extends DaoBaseInterfaceImpl<UserConnection, Long>
    implements PtoneUserConnectionDao {

  @Override
  public UserConnection get(String connectionId) {
    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("connectionId", new Object[] {connectionId});
    paramMap.put("status", new Object[] {Constants.validate});
    return getByWhere(paramMap);
  }

  @Override
  public UserConnection get(String name, String uid, long dsId, String spaceId) {
    Map<String, Object[]> paramMap = new HashMap<String, Object[]>();
    paramMap.put("name", new Object[] {name});
    paramMap.put("uid", new Object[] {uid});
    paramMap.put("dsId", new Object[] {dsId});
    paramMap.put("spaceId", new Object[] {spaceId});
    paramMap.put("status", new Object[] {Constants.validate});
    return getByWhere(paramMap);
  }

  @Override
  public void insertConnections(UserConnection userConnection) {
    String sql =
        "insert into user_connection (connection_id,name,uid,ds_id,ds_code,config,status,update_time) values (?,?,?,?,?,?,?,?)"
            + " ON DUPLICATE KEY UPDATE config = VALUES(config);";
    Object param[] =
        {userConnection.getConnectionId(), userConnection.getName(), userConnection.getUid(),
            userConnection.getDsId(), userConnection.getDsCode(), userConnection.getConfig(),
            userConnection.getStatus(), userConnection.getUpdateTime()};
    DataOperationUtils.insert(sql, param);
  }

  @Override
  public void updateConfig(String name, String dsCode, String config, String noConnectionId) {
    String sql =
        "UPDATE user_connection SET config= ? WHERE name = ? and ds_code = ? and connection_id != ?";
    Object param[] = {config, name, dsCode, noConnectionId};
    DataOperationUtils.insert(sql, param);
  }

  @Override
  public List<DsContentView> getUserDsContentView(String uid) {
    String sql =
        "SELECT ds.config dsConfig,ds.name dsName,ds.order_number dsOrderNumber,c.ds_id,c.ds_code,c.accountNum,s.nameNum FROM ("
            + "SELECT ds_id,ds_code,COUNT(NAME) accountNum,CONNECTION_ID FROM user_connection WHERE uid = :uid AND STATUS = 1 GROUP BY ds_code) c "
            + "LEFT JOIN "
            + "(SELECT ds_id,ds_code,COUNT(NAME) nameNum FROM user_connection_source WHERE uid = :uid AND STATUS = 1 GROUP BY ds_code) s "
            + "ON c.ds_id = s.ds_id "
            + "LEFT JOIN ptone_ds_info ds "
            + "ON c.ds_id = ds.id ORDER BY ds.order_number asc ";
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("uid", uid);
    return (List<DsContentView>) DataOperationUtils
        .queryForList(sql, paramMap, DsContentView.class);
  }

  @Override
  public List<DsContentView> getSpaceDsContentView(String spaceId,String userSource) {
    String sql =
        "SELECT ds.is_plus AS isPlus,ds.config dsConfig,ds.name dsName,c.ds_id,c.ds_code,c.accountNum,s.nameNum FROM ("
            + "SELECT ds_id,ds_code,COUNT(NAME) accountNum,CONNECTION_ID FROM user_connection WHERE space_id = :spaceId AND STATUS = 1 GROUP BY ds_code) c "
            + "LEFT JOIN "
            + "(SELECT ds_id,ds_code,COUNT(NAME) nameNum FROM user_connection_source WHERE space_id = :spaceId AND STATUS = 1 GROUP BY ds_code) s "
            + "ON c.ds_id = s.ds_id "
            + "LEFT JOIN ptone_ds_info ds "
            + "ON c.ds_id = ds.id ";
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("spaceId", spaceId);
    return (List<DsContentView>) DataOperationUtils
        .queryForList(sql, paramMap, DsContentView.class);
  }

}
