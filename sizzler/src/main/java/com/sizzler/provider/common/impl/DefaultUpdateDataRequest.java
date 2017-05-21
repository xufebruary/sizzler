package com.sizzler.provider.common.impl;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.UpdateDataRequest;

/**
 * Created by ptmind on 2015/12/26.
 */
public class DefaultUpdateDataRequest implements UpdateDataRequest {
  private UserConnection userConnection;
  private String sourceType;

  public DefaultUpdateDataRequest(UserConnection userConnection) {
    this.userConnection = userConnection;
  }
  
  public DefaultUpdateDataRequest(UserConnection userConnection, String sourceType) {
    super();
    this.userConnection = userConnection;
    this.sourceType = sourceType;
  }

  @Override
  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  @Override
  public UserConnection getUserConnection() {
    return userConnection;
  }

  public void setUserConnection(UserConnection userConnection) {
    this.userConnection = userConnection;
  }
}
