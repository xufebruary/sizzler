package com.sizzler.common.extension.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextSetter implements ApplicationContextAware {

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    SpringExtensionFactory.addApplicationContext(applicationContext);
  }

}
