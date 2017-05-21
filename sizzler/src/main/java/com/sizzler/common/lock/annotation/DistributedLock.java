package com.sizzler.common.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
 * 使用分布式锁
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

  String name() default ""; // 锁的名称

  long interval() default 200; // 请求锁的等待时间， 单位 ms ， 默认 200ms

  int timeout() default 5; // 锁的超时时间， 单位 s ， 默认 5s
}
