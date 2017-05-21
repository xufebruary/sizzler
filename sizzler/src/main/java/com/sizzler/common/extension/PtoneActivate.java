package com.sizzler.common.extension;

import java.lang.annotation.*;

/**
 * 如下是dubbo rpc access
 * log的过滤器，仅对服务提供方有效，且参数中需要带accesslog，也就是配置protocol或者serivce时配置的accesslog
 * ="d:/rpc_access.log"
 * 
 * @Activate(group = Constants.PROVIDER, value = Constants.ACCESS_LOG_KEY)
 *                 public class AccessLogFilter implements Filter {
 * 
 *                 }
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface PtoneActivate {

  /*
   * Group 过滤条件：即只有满足group指定的值时，该 扩展接口的实现 才会被激活 如果没有指定group，则 不对激活条件进行过滤
   */
  String[] group() default {};

  /*
   * Key过滤条件：即只有URL的参数中存在value指定的值时，才被激活 示例：<br/> 注解的值
   * <code>@Activate("cache,validatioin")</code>， 则{@link
   * ExtensionLoader#getActivateExtension
   * }的URL的参数有<code>cache</code>Key，或是<code>validatioin</ code>则返回扩展。 <br/>
   * 如没有设置，则不过滤。
   */
  String[] value() default {};

}
