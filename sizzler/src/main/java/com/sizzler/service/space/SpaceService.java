package com.sizzler.service.space;

import java.util.List;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.space.PtoneSpaceInfo;
import com.sizzler.domain.space.PtoneSpaceUser;
import com.sizzler.domain.space.dto.SpaceInfoDto;
import com.sizzler.domain.user.PtoneUser;

public interface SpaceService extends ServiceBaseInterface<PtoneSpaceInfo, String> {

  /**
   * 创建空间
   * 
   * @param space
   * @param creator
   */
  public SpaceInfoDto addSpace(SpaceInfoDto space, PtoneUser creator);

  /**
   * 修改空间
   * 
   * @param space
   */
  public void updateSpace(SpaceInfoDto space);

  /**
   * 获取摸个空间信息
   * 
   * @param spaceId
   * @param uid 用户id，可以为空， 如果不为空会查询用户与空间的关系
   * @return
   */
  public SpaceInfoDto getSpaceInfo(String spaceId, String uid);

  /**
   * 删除空间
   * 
   * @param spaceId
   * @param uid 用户id， 可以为空， 如果不为空则回校验是否为空间所有者
   * 新增参数 isDelete，用以标识删除或者恢复
   */
  public void deleteById(String spaceId, String uid,boolean isDelete);

  /**
   * 获取用户的所有空间列表
   * 
   * @param uid
   * @return
   */
  public List<SpaceInfoDto> getUserSpaceList(String uid);

  /**
   * 获取某空间下所有的follower用户列表
   * 
   * @param spaceId
   * @return
   */
  public List<PtoneSpaceUser> getSpaceUserList(String spaceId);

  /**
   * 邀请用户，创建空间follower用户列表
   * @param spaceId
   * @param emails
   */
  public void inviteUsers(String spaceId, List<String> emails, PtoneUser user);

  /**
   * 检查域名是否存在
   * @param domain
   * @return
   */
  public boolean checkDomainExists(String domain, String currentSpaceId);

  /**
   * 检查邀请url
   * @param spaceInfo
   * @param userEmail
   * @return
   */
  public String checkInviteUrl(PtoneSpaceInfo spaceInfo, String userEmail);

  /**
   * 接受邀请
   * @param spaceId
   * @param userEmail
   * @return
   */
  public boolean acceptInvite(String spaceId, String userEmail);

  /**
   * 转让空间
   * @param spaceId
   * @param uid 操作人
   * @param newOwnerId 新的所有者
   */
  public void changeSpaceOwner(String spaceId, String uid, String newOwnerId);

  /**
   * 删除空间用户
   * @param spaceId
   * @param userEmail
   */
  public void deleteSpaceUser(String spaceId, String userEmail);

  /**
   * 统计某个空间下有多少个panel
   * @param spaceId
   * @author li.zhang
   */
  public int countSpacePanel(String spaceId);

  public PtoneSpaceUser getSpaceUserByUid(String spaceId, String uid);
  
  /***
   * 级联删除该空间所有相关信息（不包括数据源页面相关）
   * @author shaoqiang.guo
   * @date 2016年11月23日 下午6:46:51
   * @param spaceId
   * @param isDelete
   */
  public void cascadeDelectBySpaceId(String spaceId,boolean isDelete);

  /**
   * 校验空间及空间下的对应panel是否存在、是否有权限访问
   * @param space
   * @param uid
   * @return
   * @date: 2016年12月12日
   * @author peng.xu
   */
  public String validateSpacePanel(PtoneSpaceInfo space, String panelId, String uid);


}
