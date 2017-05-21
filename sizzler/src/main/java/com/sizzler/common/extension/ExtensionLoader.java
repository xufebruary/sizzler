package com.sizzler.common.extension;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ExtensionLoader<T> {

  // 扩展接口的实现类的定义文件所在的目录
  private static final String SERVICES_DIRECTORY = "META-INF/services/";

  private static final String PTONE_DIRECTORY = "META-INF/sizzler/";

  private static final String PTONE_INTERNAL_DIRECTORY = PTONE_DIRECTORY + "internal/";

  private final Class<?> type;

  private final ExtensionFactory objectFactory;

  // 每一个 扩展接口 对应着一个 ExtensionLoader
  private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<Class<?>, ExtensionLoader<?>>();
  private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<Class<?>, Object>();

  // 每一个 扩展接口 都对应着多个实现类，每个实现类都有一个 name，cachedClasses中就保存了 扩展接口的 所有实现类的kv
  private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<Map<String, Class<?>>>();

  // 每一个 扩展接口 都有一个对应的 适配类；适配类的生成分两种情况，一种是通过jassit或者jdk来动态的创建；一种是显示的在某个实现类上标注
  // Adaptive注解
  private volatile Class<?> cachedAdaptiveClass = null;

  private final Holder<Object> cachedAdaptiveInstance = new Holder<Object>();

  private Set<Class<?>> cachedWrapperClasses;

  private final Map<String, PtoneActivate> cachedActivates = new ConcurrentHashMap<String, PtoneActivate>();

  private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<Class<?>, String>();

  private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<String, Holder<Object>>();

  private String cachedDefaultName;

  private ExtensionLoader(Class<?> type) {
    this.type = type;
    objectFactory = type == ExtensionFactory.class ? null : ExtensionLoader.getExtensionLoader(
        ExtensionFactory.class).getAdaptiveExtension();
  }

  // 只有被 SPI 注解的接口 才能进行扩展
  private static <T> boolean withExtensionAnnotation(Class<T> type) {
    return type.isAnnotationPresent(SPI.class);
  }

  // 获取某个 扩展接口的 ExtensionLoader
  public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
    if (type == null) {
      throw new IllegalArgumentException("Extension == null");
    }

    if (!type.isInterface()) {
      throw new IllegalArgumentException("Extension type(" + type + ") is not interface!");
    }

    if (!withExtensionAnnotation(type)) {
      throw new IllegalArgumentException("Extension type(" + type + ") without @"
          + SPI.class.getSimpleName() + " Annotation");
    }

    ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);

    if (extensionLoader == null) {
      EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
      extensionLoader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
    }

    return extensionLoader;
  }

  public T getAdaptiveExtension() {
    Object instance = cachedAdaptiveInstance.get();
    if (instance == null) {
      synchronized (cachedAdaptiveInstance) {
        instance = cachedAdaptiveInstance.get();
        if (instance == null) {
          instance = createAdaptiveExtension();
          cachedAdaptiveInstance.set(instance);
        }
      }
    }
    return (T) instance;
  }

  private T createAdaptiveExtension() {
    try {
      return injectExtension((T) (getAdaptiveExtensionClass().newInstance()));
    } catch (Exception e) {
      throw new IllegalStateException("can not create adaptive extension " + type + ", cause "
          + e.getMessage());
    }

  }

  private Class<?> getAdaptiveExtensionClass() {
    getExtensionClasses();
    if (cachedAdaptiveClass != null) {
      return cachedAdaptiveClass;
    }
    throw new IllegalStateException("Can't find " + type + " adaptive class");
  }

  public Set<String> getSupportedExtensions() {
    Map<String, Class<?>> classes = getExtensionClasses();
    return classes.keySet();
  }

  private Map<String, Class<?>> getExtensionClasses() {
    Map<String, Class<?>> classes = cachedClasses.get();
    if (classes == null) {
      synchronized (cachedClasses) {
        classes = cachedClasses.get();
        if (classes == null) {
          classes = loadExtensionClasses();
          cachedClasses.set(classes);
        }
      }
    }

    return classes;
  }

  public T getExtension(String name) {
    if (name == null || name.length() == 0)
      throw new IllegalArgumentException("Extension name == null");

    Holder<Object> holder = cachedInstances.get(name);
    if (holder == null) {
      cachedInstances.putIfAbsent(name, new Holder<Object>());
      holder = cachedInstances.get(name);
    }
    Object instance = holder.get();
    if (instance == null) {
      synchronized (holder) {
        instance = holder.get();
        if (instance == null) {
          instance = createExtension(name);
          holder.set(instance);
        }
      }
    }
    return (T) instance;
  }

  private T createExtension(String name) {
    Class<?> clazz = getExtensionClasses().get(name);

    try {
      T instance = (T) EXTENSION_INSTANCES.get(clazz);
      if (instance == null) {
        EXTENSION_INSTANCES.putIfAbsent(clazz, (T) clazz.newInstance());
        instance = (T) EXTENSION_INSTANCES.get(clazz);
      }
      injectExtension(instance);
      Set<Class<?>> wrapperClasses = cachedWrapperClasses;
      if (wrapperClasses != null && wrapperClasses.size() > 0) {
        for (Class<?> wrapperClass : wrapperClasses) {
          instance = injectExtension((T) wrapperClass.getConstructor(type).newInstance(instance));
        }
      }
      return instance;
    } catch (Throwable t) {
      throw new IllegalStateException("Extension instance(name: " + name + ", class: " + type
          + ")  could not be instantiated: " + t.getMessage(), t);
    }
  }

  private Map<String, Class<?>> loadExtensionClasses() {

    SPI ptoneSpi = type.getAnnotation(SPI.class);
    if (ptoneSpi != null) {
      String name = ptoneSpi.value();
      cachedDefaultName = name;
    }

    Map<String, Class<?>> extensionClasses = new HashMap<String, Class<?>>();
    loadFile(extensionClasses, PTONE_INTERNAL_DIRECTORY);
    loadFile(extensionClasses, PTONE_DIRECTORY);
    loadFile(extensionClasses, SERVICES_DIRECTORY);
    return extensionClasses;
  }

  // 从配置文件中加载 扩展接口 的实现类的定义
  private void loadFile(Map<String, Class<?>> extensionClasses, String dir) {
    String fileName = dir + type.getName();

    try {
      Enumeration<URL> urls;
      // 通过ClassLoader从类路径中查找对应的文件
      ClassLoader classLoader = findClassLoader();
      if (classLoader != null) {
        urls = classLoader.getResources(fileName);
      } else {
        urls = ClassLoader.getSystemResources(fileName);
      }
      if (urls != null) {
        while (urls.hasMoreElements()) {
          URL url = urls.nextElement();
          try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(),
                "utf-8"));
            try {
              String line = null;
              while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                  String name = null;
                  int i = line.indexOf("=");
                  if (i > 0) {
                    name = line.substring(0, i).trim();
                    line = line.substring(i + 1).trim();
                  }
                  // true 代表 加载时运行静态区
                  Class<?> clazz = Class.forName(line, true, classLoader);
                  if (!type.isAssignableFrom(clazz)) {
                    throw new IllegalStateException("Error when load extension class(interface:"
                        + type + ",class line:" + clazz.getName() + "),class " + clazz.getName()
                        + " is not subtype of interface");
                  }

                  // 判断实现类是否存在 Adaptive 注解,如果存在，则将其做为 该扩展接口的 适配类
                  if (clazz.isAnnotationPresent(PtoneAdaptive.class)) {
                    if (cachedAdaptiveClass == null) {
                      cachedAdaptiveClass = clazz;
                    } else if (!cachedAdaptiveClass.equals(clazz)) {
                      throw new IllegalStateException("More Than 1 adaptive class found");
                    }
                  } else {
                    // 判断实现类中是否存在 以扩展接口为参数的构造方法,如果存在，则将该实现类当作 扩展接口的 包装类对待。
                    try {
                      clazz.getConstructor(type);
                      if (cachedWrapperClasses == null) {
                        cachedWrapperClasses = new HashSet<Class<?>>();
                      }
                      cachedWrapperClasses.add(clazz);

                    } catch (NoSuchMethodException e) {
                      PtoneActivate activate = clazz.getAnnotation(PtoneActivate.class);

                      if (!cachedNames.containsKey(clazz)) {
                        cachedNames.putIfAbsent(clazz, name);
                      }
                      Class<?> c = extensionClasses.get(name);
                      if (c == null) {
                        extensionClasses.put(name, clazz);
                      } else if (c != clazz) {
                        throw new IllegalStateException("");
                      }
                    }

                  }

                }
              }
            } finally {
              reader.close();
            }

          } catch (Throwable t) {
            System.out.println("Exception when load extension class(interface:" + type
                + ",class file:" + url + ").");
          }

        }

      }

    } catch (Throwable t) {
      System.out.println("Exception when load extension class(interface:" + type + ",desc file:"
          + fileName + ").");
    }

  }

  private static ClassLoader findClassLoader() {
    return ExtensionLoader.class.getClassLoader();
  }

  private T injectExtension(T instance) {
    if (objectFactory != null) {
      // 找到所有的public set方法
      Method[] methods = instance.getClass().getMethods();
      for (Method method : methods) {
        // 以set开头，参数的长度为1，而且为public访问限制
        if (method.getName().startsWith("set") && method.getParameterTypes().length == 1
            && Modifier.isPublic(method.getModifiers())) {
          Class pt = method.getParameterTypes()[0];
          String beanName = method.getName().length() > 3 ? method.getName().substring(3, 4)
              .toLowerCase()
              + method.getName().substring(4) : "";
          Object object = objectFactory.getExtension(pt, beanName);
          if (object != null) {
            try {
              method.invoke(instance, object);
            } catch (Exception e) {
              e.printStackTrace();
              System.out.println("fail to inject " + instance.getClass().getName() + ",via method"
                  + method.getName());
            }

          }
        }
      }
    }
    return instance;
  }
}
