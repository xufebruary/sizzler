package com.sizzler.common.extension;

import java.lang.annotation.*;

/**
 * 由于每个扩展接口 可能会对应着多个具体的实现，而在运行过程中 需要按照某种预先定义好的规则来选择使用某个具体的实现； 每个扩展接口，都对应存在一个
 * 适配类，而适配类的主要作用就是 根据某个规则来 找到对应的具体实现； Adaptive可以添加到类上，也可以添加到方法中
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface PtoneAdaptive {

  String[] value() default {};

}
