package com.sizzler.common.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceBeanContext {

  private static ServiceBeanContext context = null;

  private static ApplicationContext ctx = null;

  private ServiceBeanContext() {

  }

  public static ServiceBeanContext getInstance() throws Exception {
    if (context == null) {
      context = new ServiceBeanContext();
    }
    return context;
  }

  public void loadContext(String path) {
    ctx = new ClassPathXmlApplicationContext(path);
  }

  public Object getBean(String bean) throws Exception {
    return ctx.getBean(bean);
  }
}
