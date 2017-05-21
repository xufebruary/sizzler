package com.sizzler.provider.common;

import org.apache.metamodel.util.Oauth2Token;

/**
 * 如果需要Oauth2Token，则需要实现该接口
 */
public interface Oauth2able {

  public Oauth2Token getOauth2Token();

}
