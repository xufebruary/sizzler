package com.sizzler.common.extension;

import java.lang.annotation.*;

/**
 * 该注解用于标识某个接口为 扩展接口
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SPI {

  // 扩展接口默认的实现类的名称
  String value() default "";

}
