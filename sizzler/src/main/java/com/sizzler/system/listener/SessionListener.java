package com.sizzler.system.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
  
  @Override
  public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    // HttpSession session = httpSessionEvent.getSession();
    // SessionContext.DelSession(session);
    // logger.info(session.getId()
    // +" -------------------------------------------------------------destroyed.");
  }

  @Override
  public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    // SessionContext.AddSession(httpSessionEvent.getSession());
    // logger.info(httpSessionEvent.getSession().getId() +" created.");
  }
}
