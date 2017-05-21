package com.sizzler.provider.common;

import java.io.Serializable;

import com.sizzler.common.sizzler.UserConnection;

/**
 * Created by ptmind on 2015/12/26.
 */
public interface UpdateDataRequest extends Serializable {
  public UserConnection getUserConnection();
  
  /**
   * 请求来源，目前用于GD的自动更新，标识是来源GD的自动更新
   * @author you.zou
   * @date 2016年10月28日 下午2:38:08
   * @return
   */
  public String getSourceType(); 

}
