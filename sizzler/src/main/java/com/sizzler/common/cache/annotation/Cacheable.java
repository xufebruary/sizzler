package com.sizzler.common.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
  String key() default "";

  String keyPrefix() default "";

  String keySplit() default "_";

  String condition() default "";

  // cacheTime 设置的过期时间 的优先级 高于 cacheStrategy设置的过期策略所对应的过期时间
  CACHE_STRATEGY cacheStrategy() default CACHE_STRATEGY.FOREVER;

  // 默认为-1，即代表永远不会过期
  long cacheTime() default -1;

  public enum CACHE_STRATEGY {
    HOUR, DAY, FOREVER
  }
}
