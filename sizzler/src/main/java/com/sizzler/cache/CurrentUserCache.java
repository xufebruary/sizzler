package com.sizzler.cache;

import java.io.Serializable;

import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.user.PtoneUserBasicSetting;

/**
 * 用于存储当前用户的信息，封装user的整体信息
 * 
 * @author peng.xu
 * 
 */
public class CurrentUserCache implements Serializable {

  private static final long serialVersionUID = -177163469627826664L;

  private PtoneUser currentUser;

  private PtoneUserBasicSetting currentUserSetting;

  private PtoneSpaceInfo currentSpaceInfo;

  public CurrentUserCache() {}

  public CurrentUserCache(PtoneUser currentUser, PtoneUserBasicSetting currentUserSetting) {
    this.currentUser = currentUser;
    this.currentUserSetting = currentUserSetting;
  }

  public PtoneUser getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(PtoneUser currentUser) {
    this.currentUser = currentUser;
  }

  public PtoneUserBasicSetting getCurrentUserSetting() {
    return currentUserSetting;
  }

  public void setCurrentUserSetting(PtoneUserBasicSetting currentUserSetting) {
    this.currentUserSetting = currentUserSetting;
  }

  public PtoneSpaceInfo getCurrentSpaceInfo() {
    return currentSpaceInfo;
  }

  public void setCurrentSpaceInfo(PtoneSpaceInfo currentSpaceInfo) {
    this.currentSpaceInfo = currentSpaceInfo;
  }

  /**
   * 获取用户设置的周起始日: 0:Monday , 1:Sunday (周起始设置改为空间上设置)
   * 
   * @return
   */
  public int getCurrentUserWeekStartSetting() {
    return PtoneSpaceInfo.getSpaceWeekStartSetting(this.getCurrentSpaceInfo());
  }

}
