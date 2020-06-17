package org.ylzl.eden.uaa.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;
import org.ylzl.eden.spring.boot.framework.core.FrameworkProperties;
import org.ylzl.eden.spring.boot.framework.core.util.PathMatcherConstants;
import org.ylzl.eden.spring.boot.framework.web.WebConfigurerAdapter;
import org.ylzl.eden.spring.boot.framework.web.filter.CorsFilterBuilder;
import org.ylzl.eden.spring.boot.integration.swagger.SwaggerConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Web 自动配置
 *
 * @author gyl
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class WebAutoConfiguration extends WebConfigurerAdapter {

  public WebAutoConfiguration(FrameworkProperties frameworkProperties, Environment environment) {
    super(frameworkProperties, environment);
  }

  @Bean
  public CorsFilter corsFilter(
      FrameworkProperties frameworkProperties,
      ManagementServerProperties managementServerProperties) {
    CorsConfiguration corsConfiguration = frameworkProperties.getCors();
    List<String> paths = new ArrayList<>();
    if (corsConfiguration.getAllowedOrigins() != null
        && !corsConfiguration.getAllowedOrigins().isEmpty()) {
      paths.add("/api" + PathMatcherConstants.ALL_CHILD_PATTERN);
      paths.add(
          managementServerProperties.getContextPath() + PathMatcherConstants.ALL_CHILD_PATTERN);
      paths.add(SwaggerConstants.DEFAULT_URL);
    }
    return CorsFilterBuilder.builder().corsConfiguration(corsConfiguration).paths(paths).build();
  }
}
