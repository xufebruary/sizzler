package com.sizzler.dao.ds;

import java.util.List;

import com.sizzler.common.base.dao.DaoBaseInterface;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.ds.dto.DsContentView;

public interface PtoneUserConnectionDao extends DaoBaseInterface<UserConnection, Long> {

  public abstract void updateConfig(String name, String dsCode, String config, String noConnectionId);

  public abstract void insertConnections(UserConnection userConnection);

  public abstract List<DsContentView> getUserDsContentView(String uid);

  public abstract UserConnection get(String connectionId);

  public abstract UserConnection get(String name, String uid, long dsId, String spaceId);

  public abstract List<DsContentView> getSpaceDsContentView(String spaceId,String userSource);

}
