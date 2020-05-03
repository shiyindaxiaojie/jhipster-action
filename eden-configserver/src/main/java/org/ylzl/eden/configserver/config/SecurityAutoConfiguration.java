package org.ylzl.eden.configserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.ylzl.eden.spring.boot.framework.core.FrameworkConstants;
import org.ylzl.eden.spring.boot.framework.core.util.PathMatcherConstants;
import org.ylzl.eden.spring.boot.integration.swagger.SwaggerConstants;
import org.ylzl.eden.spring.boot.security.jwt.JwtConstants;
import org.ylzl.eden.spring.boot.security.jwt.JwtProperties;
import org.ylzl.eden.spring.boot.security.jwt.configurer.JwtWebSecurityConfigurerAdapter;
import org.ylzl.eden.spring.boot.security.jwt.token.JwtTokenProvider;

/**
 * 安全自动配置
 *
 * @author gyl
 * @since 0.0.1
 */
@Slf4j
@Configuration
public class SecurityAutoConfiguration {

  @Primary // 覆盖 management.security.enabled 自动配置
  @Configuration
  public static class JwtWebSecurityAutoConfiguration extends JwtWebSecurityConfigurerAdapter {

    @Value(FrameworkConstants.NAME_PATTERN)
    private String applicationName;

    private final String managementServerContextPath;

    private final String configServerPrefix;

    public JwtWebSecurityAutoConfiguration(
        JwtTokenProvider jwtTokenProvider,
        JwtProperties jwtProperties,
        ManagementServerProperties managementServerProperties,
        ConfigServerProperties configServerProperties) {
      super(jwtTokenProvider, jwtProperties);
      this.managementServerContextPath = managementServerProperties.getContextPath();
      this.configServerPrefix = configServerProperties.getPrefix();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
      super.configure(http);

      http.httpBasic()
          .realmName(applicationName)
          .and()
          .authorizeRequests()
          // Application
          .antMatchers("/api" + PathMatcherConstants.ALL_CHILD_PATTERN)
          .authenticated()
          // Swagger
          .antMatchers(SwaggerConstants.DEFAULT_URL + PathMatcherConstants.ALL_CHILD_PATTERN)
          .permitAll()
          .antMatchers(SwaggerConstants.RESOURCES_URL + PathMatcherConstants.ALL_CHILD_PATTERN)
          .permitAll()
          .antMatchers(SwaggerConstants.RESOURCES_CONF_URL + PathMatcherConstants.ALL_CHILD_PATTERN)
          .permitAll()
          // JWT
          .antMatchers(JwtConstants.ENDPOINT_TOKEN)
          .permitAll()
          // Spring Boot Actuator
          .antMatchers(managementServerContextPath + "/health")
          .permitAll()
          .antMatchers(managementServerContextPath + "/jolokia/")
          .permitAll()
          .antMatchers(managementServerContextPath + "/profiles")
          .permitAll()
          .antMatchers(managementServerContextPath + PathMatcherConstants.ALL_CHILD_PATTERN)
          .authenticated()
          // Spring Cloud Config
          .antMatchers(configServerPrefix + PathMatcherConstants.ALL_CHILD_PATTERN)
          .authenticated();
    }
  }
}
