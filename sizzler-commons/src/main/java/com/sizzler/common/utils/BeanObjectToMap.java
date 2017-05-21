package com.sizzler.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 将实体Bean转换成Map
 */
public class BeanObjectToMap {

  @SuppressWarnings("rawtypes")
  public static Map<String, Object> convertBean(Object bean) throws Exception {
    Map<String, Object> returnMap = new HashMap<String, Object>();
    if (bean != null) {
      Class type = bean.getClass();
      BeanInfo beanInfo = Introspector.getBeanInfo(type);
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      for (int i = 0; i < propertyDescriptors.length; i++) {
        PropertyDescriptor descriptor = propertyDescriptors[i];
        String propertyName = descriptor.getName();
        if (!propertyName.equals("class")) {
          Method readMethod = descriptor.getReadMethod();
          Object result = readMethod.invoke(bean, new Object[0]);
          if (result != null) {
            returnMap.put(propertyName, result);
          } else {
            returnMap.put(propertyName, null);
          }
        }
      }
    }
    return returnMap;
  }
}
