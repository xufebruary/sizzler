package com.sizzler.provider.common.impl;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.provider.common.MetaRequest;

/**
 * Created by ptmind on 2015/12/8.
 */
public class DefaultMetaRequest implements MetaRequest {

  private UserConnection userConnection;

  public DefaultMetaRequest(UserConnection userConnection) {
    this.userConnection = userConnection;
  }

  @Override
  public UserConnection getUserConnection() {
    return this.userConnection;
  }
}
