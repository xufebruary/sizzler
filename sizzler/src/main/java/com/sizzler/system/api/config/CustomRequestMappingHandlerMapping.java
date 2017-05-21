package com.sizzler.system.api.config;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.sizzler.system.api.annotation.ApiVersion;

public class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

  @Override
  protected RequestCondition<ApiVesrsionCondition> getCustomTypeCondition(Class<?> handlerType) {
    ApiVersion apiVersion = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
    return createCondition(apiVersion);
  }

  @Override
  protected RequestCondition<ApiVesrsionCondition> getCustomMethodCondition(Method method) {
    ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
    return createCondition(apiVersion);
  }

  private RequestCondition<ApiVesrsionCondition> createCondition(ApiVersion apiVersion) {
    return apiVersion == null ? null : new ApiVesrsionCondition(apiVersion.value());
  }
}
