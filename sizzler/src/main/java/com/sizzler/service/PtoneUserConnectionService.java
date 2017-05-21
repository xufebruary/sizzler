package com.sizzler.service;

import java.util.List;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.ds.dto.DsContentView;
import com.sizzler.domain.ds.vo.ConnectionTimezoneVo;

public interface PtoneUserConnectionService extends ServiceBaseInterface<UserConnection, Long> {

  public void updateConfig(String name, String dsCode, String config, String noConnectionId);

  public void saveAndUpdate(UserConnection userConnection);

  public void saveAndUpdateOthersConfig(UserConnection userConnection);

  /**
   * 获取当前用户下授权的数据源Connection.
   */
  public UserConnection getPtoneUserConnection(String uid, long dsId, String name);

  /**
   * 获取当前用户下授权的数据源Connection.
   */
  public UserConnection getSpaceUserConnection(String spaceId, long dsId, String connectionId,
      String name, boolean useConnectionId);

  /**
   * 获取当前用户下授权的某个数据源的ConnectionList.
   */
  public List<UserConnection> findPtoneUserConnectionList(String uid, long dsId);

  /**
   * 获取当前用户下授权的某个数据源的ConnectionList.
   */
  public List<UserConnection> findSpaceUserConnectionList(String spaceId, long dsId);

  public abstract List<DsContentView> getUserDsContentView(String uid);

  public abstract UserConnection get(String connectionId);

  public List<DsContentView> getSpaceDsContentView(String spaceId,String userSource);

  public abstract UserConnection get(String name, String uid, long dsId, String spaceId);

  /**
   * 获取连接的时区信息
   * @date: 2017/3/16
   * @author: zhangli
   * @param dsId
   * @param connectionId
   * @param sourceId
   * @return
   */
  public abstract ConnectionTimezoneVo getConnectionTimezone(long dsId,String connectionId,String sourceId);

  /**
   * 更新连接的时区信息
   * @date: 2017/3/16
   * @author: zhangli
   * @param dsId
   * @param connectionId
   * @param sourceId
   * @return
   */
  public abstract void updateConnectionTimezone(long dsId,String connectionId,String sourceId,ConnectionTimezoneVo vo);

  /**
   * 根据tableId获取sourceId
   * @author shaoqiang.guo
   * @date 2017年3月17日 上午8:46:06
   * @param tableId
   * @return
   */
  public String getSourceIdByTableId(String tableId) ;

}
