package com.sizzler.system.listener;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sizzler.common.utils.StringUtil;
import com.sizzler.domain.session.dto.PtoneSession;
import com.sizzler.domain.user.PtoneUser;
import com.sizzler.system.ServiceFactory;

@Component
public class SessionContext {

  private Logger logger = LoggerFactory.getLogger(SessionContext.class);

  @Autowired
  private ServiceFactory serviceFactory;

  private static HashMap<String, PtoneSession> sessionMap = new HashMap<String, PtoneSession>();
  private long defaultSessionValidateTime = 24 * 60 * 60 * 1000; // 毫秒(24小时)
  private long rememberMeSessionValidateTime = 7 * 24 * 60 * 60 * 1000; // 毫秒(7天)

  public String addSession(PtoneUser user) {
    PtoneSession session = buildSession(user.getUserEmail(), false);
    session.setAttribute(com.sizzler.system.Constants.Current_Ptone_User, user);
    return session.getSessionId();
  }

  public String addSession(PtoneUser user, boolean rememberMe) {
    PtoneSession session = buildSession(user.getUserEmail(), rememberMe);
    session.setAttribute(com.sizzler.system.Constants.Current_Ptone_User, user);
    return session.getSessionId();
  }

  public void saveSession(PtoneSession session) {
    sessionMap.put(session.getSessionId(), session);
  }

  // 创建新session
  public PtoneSession buildSession(String email, boolean rememberMe) {
    String sessionId = email + ":" + UUID.randomUUID().toString();
    PtoneSession session = new PtoneSession(sessionId);
    if (rememberMe) {
      session.setSessionValidateTime(rememberMeSessionValidateTime + System.currentTimeMillis());
    } else {
      session.setSessionValidateTime(defaultSessionValidateTime + System.currentTimeMillis());
    }
    sessionMap.put(session.getSessionId(), session);
    return session;
  }

  public PtoneSession getSession(String sessionId) {
    if (!StringUtil.hasText(sessionId) || sessionId.equals("undefined") || !sessionId.contains("-"))
      return null;
    PtoneSession session = sessionMap.get(sessionId);
    if (session != null && session.getSessionValidateTime() < System.currentTimeMillis()) {
      sessionMap.remove(sessionId);
      return null;
    }
    return session;
  }

  public void delSession(String sessionId) {
    sessionMap.remove(sessionId);
  }

  /**
   * 清空当前email登录的所有session.
   * 
   * @param email
   * @author: zhangli
   * @date: 2017-02-14
   * @return
   */
  public void clearSession(String email) {
    sessionMap = new HashMap<String, PtoneSession>();
  }

  public PtoneUser getLoginUser(String sessionId) throws ClassNotFoundException {
    if (!StringUtil.hasText(sessionId) || sessionId.equals("undefined"))
      return null;
    PtoneSession session = getSession(sessionId);
    if (session != null) {
      return session.getAttribute(com.sizzler.system.Constants.Current_Ptone_User);
    } else {
      return null;
    }
  }

  public PtoneUser getSessionUser(String sessionId) {
    PtoneUser user = null;
    try {
      user = getLoginUser(sessionId);
    } catch (ClassNotFoundException e) {
      logger.error("not find user by session id.", e);
    }
    return user;
  }

  public synchronized void updateSessionTime(String sessionId) {
    if (StringUtil.hasText(sessionId)) {
      PtoneSession session = getSession(sessionId);
      if (session != null) {
        session.setSessionValidateTime(session.getSessionValidateTime()
            + defaultSessionValidateTime);
        logger.info(sessionId + ": update session time.");
      }
    }
  }

  public <T> T getAttribute(String key, String fieldName) throws IntrospectionException,
      InvocationTargetException, IllegalAccessException {
    PtoneSession session = getSession(key);
    PropertyDescriptor pd = new PropertyDescriptor(fieldName, PtoneSession.class);
    Method getMethod = pd.getReadMethod();
    return (T) getMethod.invoke(session);
  }

}
