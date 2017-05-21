package com.sizzler.domain.ds.dto;

import java.io.Serializable;

import com.sizzler.common.sizzler.DataBaseConnection;

public class UIDataBaseConnection extends DataBaseConnection implements Serializable {

  private static final long serialVersionUID = 5146405797694704926L;

  private String operateType;
  private String dsCode;
  private Long dsId;
  private String ssh;// 是否开启了ssh，1为开启，0为未开启，默认为0
  private String sshHost;
  private String sshPort;
  private String sshUser;
  private String sshAuthMethod;// 两类 password和private key
  private String sshPassword;
  private String sshKeyPath;
  private String sshPrivateKey;
  private String sshPassphrase;
  private String accessKeyId;
  private String secretAccessKey;
  private String spaceId;

  public String getSshPrivateKey() {
    return sshPrivateKey;
  }

  public void setSshPrivateKey(String sshPrivateKey) {
    this.sshPrivateKey = sshPrivateKey;
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

  public String getOperateType() {
    return operateType;
  }

  public void setOperateType(String operateType) {
    this.operateType = operateType;
  }

  public String getDsCode() {
    return dsCode;
  }

  public void setDsCode(String dsCode) {
    this.dsCode = dsCode;
  }

  public Long getDsId() {
    return dsId;
  }

  public void setDsId(Long dsId) {
    this.dsId = dsId;
  }

  public String getAccessKeyId() {
    return accessKeyId;
  }

  public void setAccessKeyId(String accessKeyId) {
    this.accessKeyId = accessKeyId;
  }

  public String getSecretAccessKey() {
    return secretAccessKey;
  }

  public void setSecretAccessKey(String secretAccessKey) {
    this.secretAccessKey = secretAccessKey;
  }

  public String getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }

}
