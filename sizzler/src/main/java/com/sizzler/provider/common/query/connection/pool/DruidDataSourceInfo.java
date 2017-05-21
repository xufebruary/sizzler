package com.sizzler.provider.common.query.connection.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.jcraft.jsch.Session;

public class DruidDataSourceInfo extends DruidDataSource {

  private static final long serialVersionUID = 1L;

  /** 连接池的最后一次活跃时间 */
  protected volatile long lastActiveTime;

  protected Session session;

  public long getLastActiveTime() {
    return lastActiveTime;
  }

  public void setLastActiveTime(long lastActiveTime) {
    this.lastActiveTime = lastActiveTime;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

}
