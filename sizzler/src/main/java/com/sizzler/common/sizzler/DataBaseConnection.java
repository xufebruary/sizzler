package com.sizzler.common.sizzler;

import java.io.Serializable;

public class DataBaseConnection implements Serializable {

  private static final long serialVersionUID = -4359232452394590740L;

  private String connectionId;
  private String dataBaseType;
  private String host;
  private String port;
  private String user;
  private String password;
  private String dataBaseName;
  private String tableName;
  private String connectionName;

  private String ssh;
  private String sshHost;
  private String sshPort;
  private String sshUser;
  private String sshAuthMethod;
  private String sshPassword;
  private String sshKeyPath;
  private String sshPrivateKey;
  private String sshPassphrase;

  private String uid;

  public String getConnectionId() {
    return connectionId;
  }

  public void setConnectionId(String connectionId) {
    this.connectionId = connectionId;
  }

  public String getConnectionName() {
    return connectionName;
  }

  public void setConnectionName(String connectionName) {
    this.connectionName = connectionName;
  }

  public String getDataBaseType() {
    return dataBaseType;
  }

  public void setDataBaseType(String dataBaseType) {
    this.dataBaseType = dataBaseType;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDataBaseName() {
    return dataBaseName;
  }

  public void setDataBaseName(String dataBaseName) {
    this.dataBaseName = dataBaseName;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getSsh() {
    return ssh;
  }

  public void setSsh(String ssh) {
    this.ssh = ssh;
  }

  public String getSshHost() {
    return sshHost;
  }

  public void setSshHost(String sshHost) {
    this.sshHost = sshHost;
  }

  public String getSshPort() {
    return sshPort;
  }

  public void setSshPort(String sshPort) {
    this.sshPort = sshPort;
  }

  public String getSshUser() {
    return sshUser;
  }

  public void setSshUser(String sshUser) {
    this.sshUser = sshUser;
  }

  public String getSshAuthMethod() {
    return sshAuthMethod;
  }

  public void setSshAuthMethod(String sshAuthMethod) {
    this.sshAuthMethod = sshAuthMethod;
  }

  public String getSshPassword() {
    return sshPassword;
  }

  public void setSshPassword(String sshPassword) {
    this.sshPassword = sshPassword;
  }

  public String getSshKeyPath() {
    return sshKeyPath;
  }

  public void setSshKeyPath(String sshKeyPath) {
    this.sshKeyPath = sshKeyPath;
  }

  public String getSshPassphrase() {
    return sshPassphrase;
  }

  public void setSshPassphrase(String sshPassphrase) {
    this.sshPassphrase = sshPassphrase;
  }

  public String getSshPrivateKey() {
    return sshPrivateKey;
  }

  public void setSshPrivateKey(String sshPrivateKey) {
    this.sshPrivateKey = sshPrivateKey;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

}
