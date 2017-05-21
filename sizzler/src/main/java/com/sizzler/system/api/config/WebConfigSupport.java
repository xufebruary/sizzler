package com.sizzler.system.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@EnableWebMvc
public class WebConfigSupport extends WebMvcConfigurationSupport {

  @Override
  @Bean
  public RequestMappingHandlerMapping requestMappingHandlerMapping() {
    RequestMappingHandlerMapping handlerMapping = new CustomRequestMappingHandlerMapping();
    handlerMapping.setOrder(0);
    handlerMapping.setInterceptors(getInterceptors());
    return handlerMapping;
  }

  @Override
  protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    super.configureContentNegotiation(configurer);
  }

  /*
   * @Bean public ContentNegotiationManagerFactoryBean
   * contentNegotiationManagerFactoryBean(){
   * ContentNegotiationManagerFactoryBean contentNegotiationManager = new
   * ContentNegotiationManagerFactoryBean();
   * contentNegotiationManager.setFavorPathExtension(false); return
   * contentNegotiationManager; }
   */
}
