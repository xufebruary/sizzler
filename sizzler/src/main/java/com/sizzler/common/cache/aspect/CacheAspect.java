package com.sizzler.common.cache.aspect;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.sizzler.common.cache.annotation.Cacheable;
import com.sizzler.common.cache.expression.CacheExpressionProcessor;
import com.sizzler.common.cache.provider.CacheManager;
import com.sizzler.common.reflect.ReflectUtil;

@Component
@Aspect
public class CacheAspect {

  private Logger logger = LoggerFactory.getLogger(CacheAspect.class);

  @Autowired
  private CacheManager cacheManager;

  private CacheExpressionProcessor cacheExpressionProcessor = new CacheExpressionProcessor();

  private final String CACHE_ABLE = "@annotation(cacheable)";
  @SuppressWarnings("rawtypes")
  private Map<String, Class> methodReturnClassCache = new ConcurrentHashMap<String, Class>();

  @SuppressWarnings("unchecked")
  @Around(CACHE_ABLE)
  public Object processCacheableAnnotation(ProceedingJoinPoint proceedingJoinPoint,
      Cacheable cacheable) throws Throwable {
    String key = generateKey(proceedingJoinPoint, cacheable);
    String keyId = keyString(proceedingJoinPoint, key);

    String methodKey = ReflectUtil.methodKeyString(((MethodSignature) proceedingJoinPoint
        .getSignature()).getMethod());

    Object value = cacheManager.getCacheProvider().get(key);
    if (value != null) {
      logger.warn(keyId + " hit the cache!");
      // 如果 返回的是 List等类型需要调用别的方法
      value = JSON.parseObject(value.toString(), methodReturnClassCache.get(methodKey));
    }

    if (value == null) {
      logger.warn(keyId + " miss the cache!");
      value = proceedingJoinPoint.proceed();
      if (ifCache(proceedingJoinPoint, cacheable)) {
        int expire = calculateCacheExpire(cacheable);
        // 需要进行判断，如果之前已经存在，则不需要put
        // 方法重载会存在冲突问题
        methodReturnClassCache.put(methodKey, value.getClass());
        cacheManager.getCacheProvider().put(key, value, expire);
      } else {
        logger.warn(keyId + " cannot satisfy the cache condition!");
      }
    }

    return value;

  }

  private String generateKey(ProceedingJoinPoint proceedingJoinPoint, Cacheable cacheable) {
    // 取得要执行的method
    Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
    // proceedingJoinPoint.getArgs() 返回的是 参数值 列表
    Object[] args = proceedingJoinPoint.getArgs();

    String keyExpression = cacheable.key();

    String keyPrefix = cacheable.keyPrefix();
    String keySplit = cacheable.keySplit();

    StringBuilder keyBuilder = new StringBuilder("");

    if (!keyPrefix.equals("")) {
      keyBuilder.append(keyPrefix).append(keySplit);
    }

    // 如果没有显示的指定 key，则默认使用所有参数的组合做为key

    if (!keyExpression.equals("")) {
      if (keyExpression.contains(",")) {
        String[] keyExpressionArray = keyExpression.split(",");
        int keyLen = keyExpressionArray.length;
        for (int i = 0; i < keyLen; i++) {
          keyBuilder.append(cacheExpressionProcessor.evaluateKey(keyExpressionArray[i], method,
              args));
          if (i < keyLen - 1) {
            keyBuilder.append(keySplit);
          }
        }
      } else {
        String tmpKey = (String) cacheExpressionProcessor.evaluateKey(keyExpression, method, args);
        keyBuilder.append(tmpKey);
      }

    }

    return keyBuilder.toString();
  }

  private boolean ifCache(ProceedingJoinPoint proceedingJoinPoint, Cacheable cacheable) {
    // 取得要执行的method
    Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
    // proceedingJoinPoint.getArgs() 返回的是 参数值 列表
    Object[] args = proceedingJoinPoint.getArgs();

    boolean isCache = true;
    String condition = cacheable.condition();
    // 如果设置了缓存的条件，则根据设置的条件来决定是否进行缓存
    if (!condition.equals("")) {
      System.out.println("condition=" + condition);
      isCache = cacheExpressionProcessor.evaluateCondition(condition, method, args);

    }

    return isCache;
  }

  private String keyString(ProceedingJoinPoint proceedingJoinPoint, String key) {
    // 取得要执行的method
    Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();

    StringBuilder stringBuilder = new StringBuilder("");
    stringBuilder.append(method.getDeclaringClass().getName()).append("#").append(method.getName())
        .append("#").append(key);
    return stringBuilder.toString();
  }

  private int calculateCacheExpire(Cacheable cacheable) {
    long cacheTime = cacheable.cacheTime();
    if (cacheTime != -1) {
      return (int) cacheTime;
    }
    switch (cacheable.cacheStrategy()) {
    case FOREVER:
      return -1;
    case DAY:
      return 24 * 60 * 60;
    case HOUR:
      return 60 * 60;
    default:
      return -1;
    }

  }

}
