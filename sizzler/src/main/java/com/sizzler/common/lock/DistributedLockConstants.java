package com.sizzler.common.lock;

public class DistributedLockConstants {

  /**
   * 用于操作panelLayout时增加分布式锁
   * 
   * 所有接口需要包含spaceId参数用于生成lockKey
   */
  public static final String DISTRIBUTED_LOCK_PANEL_LAYOUT = "PanelLayout";

}
