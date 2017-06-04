package com.sizzler.service.collect;

import java.util.Map;

public interface CollectService {

  /**
   * 保存app采集信息
   */
  public void saveAppCollectInfo(Map<String, String> infoMap);

}
