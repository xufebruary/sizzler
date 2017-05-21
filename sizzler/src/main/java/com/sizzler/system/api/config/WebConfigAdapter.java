package com.sizzler.system.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfigAdapter extends WebMvcConfigurerAdapter {

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    // 关闭spring mvc url后缀正则验证
    configurer.favorPathExtension(false);
  }

}
