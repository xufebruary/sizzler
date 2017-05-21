package com.sizzler.service.impl;

import java.beans.IntrospectionException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ptmind.common.utils.StringUtil;
import com.ptmind.common.utils.UuidUtil;
import com.sizzler.common.SourceType;
import com.sizzler.common.base.service.ServiceBaseInterfaceImpl;
import com.sizzler.common.exception.BusinessErrorCode;
import com.sizzler.common.exception.BusinessException;
import com.sizzler.common.sizzler.DsConstants;
import com.sizzler.common.sizzler.UserConnection;
import com.sizzler.dao.UserDao;
import com.sizzler.dao.UserSettingDao;
import com.sizzler.domain.session.PTSession;
import com.sizzler.domain.space.dto.SpaceInfoDto;
import com.sizzler.domain.sys.SysMetaLog;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.domain.user.PtoneUserBasicSetting;
import com.sizzler.service.UserService;
import com.sizzler.system.Constants;
import com.sizzler.system.ServiceFactory;

@Service("userService")
public class UserServiceImpl extends ServiceBaseInterfaceImpl<PtoneUser, String> implements
    UserService {

  private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired
  private UserDao userDao;
  @Autowired
  private UserSettingDao userSettingDao;
  @Autowired
  private ServiceFactory serviceFactory;

  @Override
  public void save(PtoneUser ptoneUser) {
    userDao.save(ptoneUser);
  }

  /**
   * 根据Key获取Session
   * 
   * @param key SessionKey
   */
  @Override
  public PTSession getSessionForKey(String key) {
    // return redisService.getObjectByKey(key, PTSession.class);
    return null;
  }

  /**
   * 根据Key判断用户是否登录
   * 
   * @param key SessionKey
   */
  @Override
  public boolean isLogin(String key) {
    /*
     * PTSession session = redisService.getObjectByKey(key, PTSession.class); if (session == null) {
     * return false; } else if (session.getUser() == null) { return false; }
     */
    return true;
  }

  @Override
  public void createNewSession(String keyID, Integer time, PTSession session) {
    logger.info("createNewSession : " + keyID + ", " + session.getUser().getUserEmail());
//    redisService.setKey(keyID, time, JSON.toJSONString(session));
  }

  @Override
  public void setSession(String key, PTSession session) {
    /*
     * String userIdBase64Key = getValueByCookieName(request, Constants.REDIS_SESSION_KEY); if
     * (StringUtil.hasText(userIdBase64Key)) { String userIdKey =
     * CodecUtil.base64decode(userIdBase64Key); Long time = redisService.getKeyTTLTime(userIdKey);
     * createNewSession(userIdKey, Integer.parseInt(String.valueOf(time)), session); }
     */
  }

  @Override
  public void removeSession(String keyID) {
    if (StringUtil.hasText(keyID)) {
//      redisService.remove(keyID);
    }
  }

  @Override
  public Object getObjectFromSession(String key, String fieldName) throws IntrospectionException,
      InvocationTargetException, IllegalAccessException {
    // 从redis里取user
    /*
     * PTSession session = getSessionForKey(key); PropertyDescriptor pd = new
     * PropertyDescriptor(fieldName, PTSession.class); Method getMethod = pd.getReadMethod(); Object
     * value = getMethod.invoke(session);
     */

    // 从mysql里取user
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("userEmail", new Object[] {key});
    paramMap.put("status", new Object[] {Constants.validate});
    PtoneUser loginUser = userDao.getByWhere(paramMap);
    return loginUser;
  }

  @Override
  public boolean login(String userEmail, String password) throws Exception {
    try {
      Map<String, Object[]> paramMap = new HashMap<>();
      paramMap.put("userEmail", new Object[] {userEmail});
      paramMap.put("status", new Object[] {Constants.validate});
      PtoneUser loginUser = userDao.getByWhere(paramMap);
      /*
       * if (loginUser == null) { return false; }
       */
      String md5Password = password;
      if (loginUser.getUserPassword().equalsIgnoreCase(md5Password)) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      logger.error(userEmail + " sign in error.", e);
      return false;
    }
  }

  @Override
  public void updateUserSetting(PtoneUserBasicSetting setting) {
    userSettingDao.update(setting);
  }

  @Override
  public PtoneUserBasicSetting getUserSetting(String ptId, String spaceId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("ptId", new Object[] {ptId});

    PtoneUserBasicSetting setting = userSettingDao.getByWhere(paramMap);

    // 根据spaceId，调整profileSelected信息，只返回对应空间的信息
    if (StringUtil.isNotBlank(spaceId) && setting != null
        && StringUtil.isNotBlank(setting.getProfileSelected())) {
      String profileSelected = setting.getProfileSelected();
      Map<String, Map<String, String>> profileSelectedMap =
          JSON.parseObject(profileSelected, HashMap.class);
      Map<String, String> spaceProfileSelectedMap =
          (profileSelectedMap != null ? profileSelectedMap.get(spaceId) : null);
      if (spaceProfileSelectedMap != null) {
        setting.setProfileSelected(JSON.toJSONString(spaceProfileSelectedMap));
      } else {
        setting.setProfileSelected(null);
      }
    }

    return setting;
  }

  @Override
  public void saveUserSetting(PtoneUserBasicSetting setting) {
    userSettingDao.save(setting);
  }

  @Override
  @Transactional
  public PtoneUser saveUserInfoAndSettings(PtoneUser user, PtoneUserBasicSetting setting) {
    userDao.save(user);
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("status", new Object[] {Constants.validate});
    paramMap.put("userEmail", new Object[] {user.getUserEmail()});
    PtoneUser dbUser = userDao.getByWhere(paramMap);
    setting.setPtId(dbUser.getPtId());
    userSettingDao.save(setting);
    // saveDefaultTemplet(dbUser,setting.getLocale());
    serviceFactory.getPtonePermissionManagerService().saveDefaultUserRole(dbUser);
    return dbUser;
  }

  @Transactional
  public List<PtoneUser> updatePreUserStatus(String userIds) {
    if (StringUtil.isBlank(userIds)) {
      return null;
    }
    String[] userIdArray = userIds.split(",");
    List<PtoneUser> preUsers = findByWhere("ptId", "in", userIdArray);
    if (null != preUsers && !preUsers.isEmpty()) {
      for (PtoneUser preUser : preUsers) {
        if (preUser == null) {
          continue;
        }
        updatePreUser(preUser);
      }
    }
    return preUsers;
  }

  private void updatePreUser(PtoneUser preUser) {
    preUser.setIsPreRegistration(Constants.validate);
    // li.zhang 20160809 用户自己重置密码激活时，设置激活时间
    update(preUser);
  }

  /**
   * 更新用户最近一次选择profile信息
   */
  @Override
  public void updateProfileSelectedSetting(String userId, String dsId, String spaceId,
      Map<String, String> setting) {
    if (StringUtil.isBlank(spaceId)) {
      return;
    }
    PtoneUserBasicSetting userSetting = getUserSetting(userId, null);
    Map<String, Map<String, String>> profileSelectedMap =
        new HashMap<String, Map<String, String>>();
    Map<String, String> spaceSetting = new HashMap<String, String>();
    if (userSetting == null) {
      userSetting = new PtoneUserBasicSetting();
      userSetting.setPtId(userId);
      profileSelectedMap.put(spaceId, setting);
      userSetting.setProfileSelected(JSON.toJSONString(profileSelectedMap));
      userSettingDao.save(userSetting);
    } else {
      String profileSelected = userSetting.getProfileSelected();
      if (StringUtil.isNotBlank(profileSelected)) {
        profileSelectedMap = JSON.parseObject(profileSelected, HashMap.class);
      }
      if (profileSelectedMap != null && profileSelectedMap.get(spaceId) != null) {
        spaceSetting = profileSelectedMap.get(spaceId);
      }
      spaceSetting.putAll(setting);
      profileSelectedMap.put(spaceId, spaceSetting);
      userSetting.setId(null);
      userSetting.setProfileSelected(JSON.toJSONString(profileSelectedMap));
      userSettingDao.update(userSetting);
    }
  }

  @Override
  public void updateUserSelectedSetting(String userId, Map<String, String> setting) {
    PtoneUserBasicSetting userSetting = getUserSetting(userId, null);

    Map<String, String> userSelectedSetting = new HashMap<String, String>();
    if (userSetting == null) {
      userSetting = new PtoneUserBasicSetting();
      userSetting.setPtId(userId);
      userSetting.setUserSelected(JSON.toJSONString(setting));
      userSettingDao.save(userSetting);
    } else {
      String userSelected = userSetting.getUserSelected();
      if (userSelected != null && !"".equals(userSelected)) {
        userSelectedSetting = JSON.parseObject(userSelected, HashMap.class);
      }
      userSelectedSetting.putAll(setting);
      userSetting.setId(null);
      userSetting.setUserSelected(JSON.toJSONString(userSelectedSetting));
      userSettingDao.update(userSetting);
    }

  }

  @Override
  public PtoneUser getPtoneUser(String userId) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("ptId", new Object[] {userId});
    paramMap.put("status", new Object[] {Constants.validate});
    return userDao.getByWhere(paramMap);
  }

  @Override
  public PtoneUser getPtoneUserByEmail(String email) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("userEmail", new Object[] {email});
    paramMap.put("status", new Object[] {Constants.validate});
    return userDao.getByWhere(paramMap);
  }

  public void sendSysMetaLog(SysMetaLog sysMetaLog) {
    // update by li.zhang 2016/08/09
//    serviceFactory.getLogCollectUtil().sendData(sysMetaLog);
  }

  @Override
  public void sendPreRegisterSysMetaLog(PtoneUser user, PtoneUserBasicSetting setting) {
    SysMetaLog sysMetaLog = new SysMetaLog();
    sysMetaLog.setUid(user.getPtId());
    sysMetaLog.setTime(System.currentTimeMillis());
    sysMetaLog.setPosition("pre-register");
    sysMetaLog.setOperate("pre-register-btn");
    sysMetaLog.setOperateId(UUID.randomUUID().toString());
    Map<String, String> contentMap = new HashMap<>();
    contentMap.put("email", user.getUserEmail());
    contentMap.put("uid", user.getPtId());
    contentMap.put("locale", setting.getLocale());
    contentMap.put("week-start", setting.getWeekStart());
    sysMetaLog.setContent(JSON.toJSONString(contentMap));
    sendSysMetaLog(sysMetaLog);
  }

  @Override
  @Transactional
  public void createSpaceAndResetPwd(PtoneUser user, SpaceInfoDto space) {
    serviceFactory.getUserService().update(user);
    serviceFactory.getSpaceService().addSpace(space, user);
    saveTempletByUserSource(user,space.getSpaceId());
  }

  @Override
  @Transactional
  public SpaceInfoDto createDefaultSpaceAndResetPwd(PtoneUser user,PtoneUserBasicSetting setting) {
    SpaceInfoDto spaceInfoDto = createDefaultSpace(user,setting);
    serviceFactory.getUserService().update(user);
    return spaceInfoDto;
  }

  @Override
  public SpaceInfoDto createDefaultSpace(PtoneUser user,PtoneUserBasicSetting setting) {
    boolean isExists = true;
    int i = 0;
    SpaceInfoDto space = new SpaceInfoDto();
    String spaceDomain = "";
    String spaceId = "";
    String spaceName = user.getUserEmail().length() > 30 ? user.getUserEmail().substring(0,30):user.getUserEmail();
    do{
      spaceDomain = UuidUtil.generateShortUuid().toLowerCase();
      spaceId = UuidUtil.generateUuid();
      isExists = serviceFactory.getSpaceService().checkDomainExists(spaceDomain, spaceId);
      if(i++ > 5){
        throw new BusinessException(BusinessErrorCode.Space.BUILD_SPACE_DOMAIN_ERROR,"space domain build method happen error.");
      }
    }while (isExists);
    space.setSpaceId(spaceId);
    space.setDomain(spaceDomain);
    space.setName(spaceName);
    space.setWeekStart(setting.getWeekStart());
    serviceFactory.getSpaceService().addSpace(space, user);
    saveTempletByUserSource(user,spaceId);
    return space;
  }

  /**
   * 单独启线程预制面板.
   */
  public void saveTempletByUserSource(final PtoneUser user, final String spaceId) {
//    try {
//      Map<String, Object[]> paramMap = new HashMap<>();
//      paramMap.put("ptId", new Object[]{user.getPtId()});
//      PtoneUserBasicSetting setting = serviceFactory.getUserSettingService().getByWhere(paramMap);
//      serviceFactory.getUserService().saveTempletByUserSource(user, setting.getLocale(), spaceId);
//    } catch (Exception e) {
//      logger.error("save Default Templet error from reset pwd.", e);
//      serviceFactory.getMailFactory().sendEmail("save Default Templet error from reset pwd.",
//              user.getUserEmail(), "ptone@ptthink.com");
//      e.printStackTrace();
//    }
  }

  @Override
  public PtoneUser getUser(String email) {
    Map<String, Object[]> paramMap = new HashMap<>();
    paramMap.put("userEmail", new Object[]{email});
    paramMap.put("status", new Object[]{Constants.validate});
    PtoneUser dbUser = serviceFactory.getUserService().getByWhere(paramMap);
    return dbUser;
  }

  @Override
  public void saveDefaultTemplet(PtoneUser user, String localLang, String space) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void saveTempletByUserSource(PtoneUser user, String localLang, String space)
      throws Exception {
    // TODO Auto-generated method stub
    
  }
}
