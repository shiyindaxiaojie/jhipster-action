/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ylzl.eden.admin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.PathMatcher;
import org.ylzl.eden.admin.security.UnauthorizedEntryPoint;
import org.ylzl.eden.admin.web.filter.OAuth2ClientCredentialsFilter;
import org.ylzl.eden.spring.boot.framework.core.util.PathMatcherConstants;
import org.ylzl.eden.spring.boot.integration.swagger.SwaggerConstants;
import org.ylzl.eden.spring.boot.security.core.SecurityConstants;
import org.ylzl.eden.spring.boot.security.jwt.JwtConstants;
import org.ylzl.eden.spring.boot.security.jwt.JwtProperties;
import org.ylzl.eden.spring.boot.security.jwt.configurer.JwtWebSecurityConfigurerAdapter;
import org.ylzl.eden.spring.boot.security.jwt.token.JwtTokenProvider;
import org.ylzl.eden.spring.boot.security.oauth2.OAuth2Properties;
import org.ylzl.eden.spring.boot.security.oauth2.token.store.ClientCredentialsTokenHolder;

/**
 * 安全自动配置
 *
 * @author gyl
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class SecurityAutoConfiguration {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint(
      AdminServerProperties adminServerProperties) {
    return new UnauthorizedEntryPoint(adminServerProperties);
  }

  @Primary
  @Configuration
  public static class JwtWebSecurityAutoConfiguration extends JwtWebSecurityConfigurerAdapter {

    private final String webEndpointBasePath;

    private final String adminServerContextPath;

    private final ZuulProperties zuulProperties;

    public JwtWebSecurityAutoConfiguration(
        JwtTokenProvider jwtTokenProvider,
        JwtProperties jwtProperties,
        WebEndpointProperties webEndpointProperties,
        AdminServerProperties adminServerProperties,
        ZuulProperties zuulProperties) {
      super(jwtTokenProvider, jwtProperties);
      this.webEndpointBasePath = webEndpointProperties.getBasePath();
      this.adminServerContextPath = adminServerProperties.getContextPath();
      this.zuulProperties = zuulProperties;
    }

    @Override
    public void configure(WebSecurity web) {
      super.configure(web);

      //			web.ignoring()
      //				// Spring Boot Admin
      //				.antMatchers(adminServerContextPath + "/**/*.{js,css,html}")
      //				.antMatchers(adminServerContextPath + "/assets/**")
      //				.antMatchers(adminServerContextPath + "/extemsions/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
      //			super.configure(http);

      SavedRequestAwareAuthenticationSuccessHandler successHandler =
          new SavedRequestAwareAuthenticationSuccessHandler();
      successHandler.setTargetUrlParameter("redirectTo");
      successHandler.setDefaultTargetUrl(this.adminServerContextPath + "/");

      http.authorizeRequests(
              (authorizeRequests) ->
                  authorizeRequests
                      // Application
                      .antMatchers("/api" + PathMatcherConstants.ALL_CHILD_PATTERN)
                      .authenticated()
                      // Eureka
                      .antMatchers("/eureka" + PathMatcherConstants.ALL_CHILD_PATTERN)
                      .authenticated()
                      // Hystrix
                      .antMatchers("/hystrix" + PathMatcherConstants.ALL_CHILD_PATTERN)
                      .authenticated()
                      // Swagger
                      .antMatchers(
                          SwaggerConstants.DEFAULT_URL + PathMatcherConstants.ALL_CHILD_PATTERN)
                      .permitAll()
                      .antMatchers(
                          SwaggerConstants.RESOURCES_URL + PathMatcherConstants.ALL_CHILD_PATTERN)
                      .permitAll()
                      .antMatchers(
                          SwaggerConstants.RESOURCES_CONF_URL
                              + PathMatcherConstants.ALL_CHILD_PATTERN)
                      .permitAll()
                      // JWT
                      .antMatchers(JwtConstants.ENDPOINT_TOKEN)
                      .permitAll()
                      // Zuul
                      .antMatchers(
                          this.zuulProperties.getPrefix() + PathMatcherConstants.ALL_CHILD_PATTERN)
                      .authenticated()
                      // Spring Boot Actuator
                      .antMatchers(this.webEndpointBasePath + "/health")
                      .permitAll()
                      .antMatchers(this.webEndpointBasePath + "/profiles")
                      .permitAll()
                      .antMatchers(
                          this.webEndpointBasePath + PathMatcherConstants.ALL_CHILD_PATTERN)
                      .authenticated()
                      // Spring Boot Admin
                      .antMatchers(this.adminServerContextPath + "/assets/**")
                      .permitAll()
                      .antMatchers(this.adminServerContextPath + "/login")
                      .permitAll()
                      .anyRequest()
                      .authenticated())
          .formLogin(
              (formLogin) ->
                  formLogin
                      .loginPage(this.adminServerContextPath + "/login")
                      .successHandler(successHandler))
          .logout((logout) -> logout.logoutUrl(this.adminServerContextPath + "/logout"))
          .csrf(
              (csrf) ->
                  csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                      .ignoringRequestMatchers(
                          new AntPathRequestMatcher(
                              this.adminServerContextPath + "/instances",
                              HttpMethod.POST.toString()),
                          new AntPathRequestMatcher(
                              this.adminServerContextPath + "/instances/**",
                              HttpMethod.DELETE.toString()),
                          new AntPathRequestMatcher(this.adminServerContextPath + "/actuator/**")));
    }
  }

  @Configuration
  public static class OAuth2WebSecurityAutoConfiguration {

    @ConditionalOnExpression(
        "'${"
            + SecurityConstants.PROP_PREFIX
            + ".oauth2.authorization.client-credentials.client-id}'.length() > 0")
    @Bean
    public OAuth2ClientCredentialsFilter oAuth2ClientCredentialsZuulFilter(
        ClientCredentialsTokenHolder clientCredentialsTokenHolder,
        AdminServerProperties adminServerProperties,
        OAuth2Properties oAuth2Properties,
        PathMatcher pathMatcher) {
      return new OAuth2ClientCredentialsFilter(
          clientCredentialsTokenHolder, adminServerProperties, oAuth2Properties, pathMatcher);
    }
  }
}
