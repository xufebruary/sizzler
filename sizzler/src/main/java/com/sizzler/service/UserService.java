package com.sizzler.service;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.sizzler.common.base.service.ServiceBaseInterface;
import com.sizzler.domain.session.PTSession;
import com.sizzler.domain.space.dto.SpaceInfoDto;
import com.sizzler.domain.sys.SysMetaLog;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.user.PtoneUserBasicSetting;

public interface UserService extends ServiceBaseInterface<PtoneUser, String> {

  /***
   * 根据Key获取Session
   * 
   * @param key SessionKey
   * */
  public PTSession getSessionForKey(String key);

  /***
   * 根据Key判断用户是否登录
   * 
   * @param key SessionKey
   * */
  public boolean isLogin(String key);

  public void createNewSession(String keyID, Integer time, PTSession session);

  public void setSession(String key, PTSession session);

  public void removeSession(String userIdBase64Key);

  public Object getObjectFromSession(String key, String fieldName) throws IntrospectionException,
      InvocationTargetException, IllegalAccessException;

  public boolean login(String userEmail, String password) throws Exception;

  public void updateUserSetting(PtoneUserBasicSetting setting);

  public PtoneUserBasicSetting getUserSetting(String ptId, String spaceId);

  public void saveUserSetting(PtoneUserBasicSetting setting);

  public PtoneUser saveUserInfoAndSettings(PtoneUser user, PtoneUserBasicSetting setting);

  public void saveDefaultTemplet(PtoneUser user, String localLang, String space)
      throws Exception;

  public void saveTempletByUserSource(PtoneUser user, String localLang, String space)
      throws Exception;

  public void updateProfileSelectedSetting(String userId, String dsId, String spaceId,
      Map<String, String> setting);

  public void updateUserSelectedSetting(String userId, Map<String, String> userSelectedSetting);

  public PtoneUser getPtoneUser(String userId);

  public PtoneUser getPtoneUserByEmail(String email);

  /**
   * 修改预注册用户状态
   * @param userIds
   */
  public List<PtoneUser> updatePreUserStatus(String userIds);

  public void sendSysMetaLog(SysMetaLog sysMetaLog);

  public void sendPreRegisterSysMetaLog(PtoneUser user, PtoneUserBasicSetting setting);

  /**
   * 新用户注册重置密码并创建空间（前台）
   * @param user
   * @param space
   * @return
   * @date: 2016年9月10日
   * @author li.zhang
   */
  public void createSpaceAndResetPwd(PtoneUser user,SpaceInfoDto space);

  /**
   * 新用户注册创建空间(后台)
   * @param user
   * @return
   * @date: 2016年2月8日
   * @author li.zhang
   */
  public SpaceInfoDto createDefaultSpace(PtoneUser user,PtoneUserBasicSetting setting);

  /**
   * 新用户注册重置密码并创建空间(后台)
   * @param user
   * @return
   * @date: 2016年2月8日
   * @author li.zhang
   */
  public SpaceInfoDto createDefaultSpaceAndResetPwd(PtoneUser user,PtoneUserBasicSetting setting);

  /**
   * 查询有效的email用户信息
   * @param email
   * @date 2017年2月8日
   * @author li.zhang
   * @return
   */
  public PtoneUser getUser(String email);

}
