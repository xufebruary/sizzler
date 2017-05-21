package com.sizzler.provider.common;

import java.io.Serializable;

/**
 * Created by ptmind on 2015/12/26.
 */
public interface UpdateDataResponse extends Serializable {
  public boolean getUpdateStatus();

  public boolean hasChanged();

  public boolean hasDeleted();

  public long getLastModifiedDate();
  
  /**
   * 是否单方面断开连接，目前只有GD的自动更新在用
   * @author you.zou
   * @date 2016年10月28日 下午2:44:59
   * @return
   */
  public boolean hasDisconnected();
}
