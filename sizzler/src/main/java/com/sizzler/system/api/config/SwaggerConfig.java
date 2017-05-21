package com.sizzler.system.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.sizzler.system.Constants;

@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan({ "com.sizzler.controller" })
public class SwaggerConfig {

  private SpringSwaggerConfig springSwaggerConfig;

  @Autowired
  public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
    this.springSwaggerConfig = springSwaggerConfig;
  }

  @Bean
  // Don't forget the @Bean annotation
  public SwaggerSpringMvcPlugin newImplementation() {
    // return new
    // SwaggerSpringMvcPlugin(this.springSwaggerConfig).includePatterns(".*pet.*");
    // return new
    // SwaggerSpringMvcPlugin(this.springSwaggerConfig).includePatterns(".*rest.*");
    // return new
    // SwaggerSpringMvcPlugin(this.springSwaggerConfig).includePatterns(".*?");
    // return new
    // SwaggerSpringMvcPlugin(this.springSwaggerConfig).apiInfo(apiInfo()).includePatterns(
    // ".api.*");
    return new SwaggerSpringMvcPlugin(this.springSwaggerConfig).apiInfo(apiInfo())
        .includePatterns(".*/\\{version\\}/.*").pathProvider(new PtoneApiPath())
        .swaggerGroup("api");
  }

  private ApiInfo apiInfo() {
    ApiInfo apiInfo = new ApiInfo("Sizzler Deck API", "Sizzler API Description",
        "Sizzler API terms of service", "Sizzler API Contact Email", "Sizzler API Licence Type",
        "Sizzler API License URL");
    return apiInfo;
  }

  class PtoneApiPath extends SwaggerPathProvider {

    @Override
    protected String applicationPath() {
      return "/api/";
    }

    @Override
    protected String getDocumentationPath() {
      return "/";
    }

    @Override
    public String sanitiseUrl(String candidate) {
      return candidate.replaceAll("(?<!(http:|https:))//", "/").replaceAll("\\{version\\}",
          Constants.API_VERSION_PERFIX + Constants.API_VERSION_1);
    }
  }

}
