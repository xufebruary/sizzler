package com.sizzler.common.extension.spring;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import com.sizzler.common.extension.ExtensionFactory;

public class SpringExtensionFactory implements ExtensionFactory {

  private static final Set<ApplicationContext> contexts = new HashSet<ApplicationContext>();

  public static void addApplicationContext(ApplicationContext applicationContext) {
    contexts.add(applicationContext);
  }

  public static void removeApplicationContext(ApplicationContext applicationContext) {
    contexts.remove(applicationContext);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getExtension(Class<T> type, String name) {
    for (ApplicationContext context : contexts) {
      if (context.containsBean(name)) {
        Object object = context.getBean(name);
        if (type.isInstance(object)) {
          return (T) object;
        }
      }
    }
    return null;
  }

}
