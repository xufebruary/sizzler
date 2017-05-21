package com.sizzler.provider.common.query.connection.pool;

import java.sql.Connection;

import com.jcraft.jsch.Session;

/**
 * @name DataBaseConnectionInfo
 * @author shaoqiang.guo
 * @data：2017年2月7日 上午9:40:56
 */
public class DataBaseConnectionInfo {
  protected Connection connection;
  protected Session session;

  public Connection getConnection() {
    return connection;
  }

  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

}
