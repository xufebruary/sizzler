package com.sizzler.dao.ds;

import java.util.List;

import com.sizzler.common.base.dao.DaoBaseInterface;
import com.sizzler.domain.ds.UserConnectionSource;
import com.sizzler.domain.ds.dto.UserAccountSource;

/**
 * @ClassName: UserConnectionSourceDao
 * @Description:.
 * @Company: Copyright (c) Pt mind
 * @version: 2.1
 * @date: 2015/12/10
 * @author: zhangli
 */
public interface UserConnectionSourceDao extends DaoBaseInterface<UserConnectionSource, String> {
  public abstract void updateLastModifiedDate(String sourceId, long lastModifiedDate);

  public abstract void updateUpdateTime(String sourceId, long updateTime);

  public abstract List<UserAccountSource> getUserAccountSource(String uid, String dsId);

  public abstract void updateConnectionSourceRemoteStatus(String status, String fileId);

  public abstract void updateConnectionSourceConfig(String config, String sourceId);

  public abstract void updateConnectionSourcePath(String path, String connectionId);
  
  /**
   * 根据connectionId修改Source的状态
   * @author you.zou
   * @date 2016年11月28日 上午10:07:34
   * @param status
   * @param connectionId
   */
  public abstract void updateConnectionSourceStatusByConnectionId(String status, String connectionId);

  public abstract void updateConnectionSourceRemoteStatusBySourceId(String sourceId, String status);

  public abstract List<UserAccountSource> getSpaceAccountSource(String spaceId, String dsId);

  public abstract void deleteSourceByConnectionId(String connectionId);
}
