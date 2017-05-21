package com.sizzler.service.space;

import java.util.List;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.space.PtoneSpaceUser;

public interface SpaceUserService extends ServiceBaseInterface<PtoneSpaceUser, Long> {

  /**
   * 获取某个空间下的某用户信息
   * @param spaceId
   * @param uid
   * @return
   */
  public PtoneSpaceUser getSpaceUserByUid(String spaceId, String uid);

  /**
   * 获取某个空间下的某用户信息
   * @param spaceId
   * @param email
   * @return
   */
  public PtoneSpaceUser getSpaceUserByEmail(String spaceId, String email);

  /**
   * 获取用户自己follow的所有空间的用户列表
   * 
   * @param uid
   * @return
   */
  public List<PtoneSpaceUser> getUserSpaceUserList(String uid);

  /**
   * 获取空间下所有的follower用户列表
   * 
   * @param spaceId
   * @return
   */
  public List<PtoneSpaceUser> getSpaceFollowerSpaceUserList(String spaceId);

}
