package com.sizzler.service;

import java.util.Map;

public interface SysService {

  public Map<String, String> getAllDataVersion();

  public String refreshMemeryCache();

  public boolean validateAccessTokenKey(String key);

}
