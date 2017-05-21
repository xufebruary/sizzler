package com.sizzler.service;

import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.domain.ds.vo.UserConnectionSourceVo;

public interface UploadService {
  
  public UserConnectionSourceVo getSourceVo(UserConnection connection);

}
