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

package org.ylzl.eden.registry.config;

import de.codecentric.boot.admin.config.AdminServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.PathMatcher;
import org.ylzl.eden.registry.filter.OAuth2ClientCredentialsFilter;
import org.ylzl.eden.registry.security.UnauthorizedEntryPoint;
import org.ylzl.eden.spring.boot.framework.core.FrameworkConstants;
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
 * @since 0.0.1
 */
@Slf4j
@Configuration
public class SecurityAutoConfiguration {

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint(AdminServerProperties adminServerProperties) {
		return new UnauthorizedEntryPoint(adminServerProperties);
	}

	@Primary // 覆盖 management.security.enabled 自动配置
	@Configuration
	public static class JwtWebSecurityAutoConfiguration extends JwtWebSecurityConfigurerAdapter {

		@Value(FrameworkConstants.NAME_PATTERN)
		private String applicationName;

		private final String managementServerContextPath;

		private final String configServerPrefix;

		private final String adminServerContextPath;

		private final ZuulProperties zuulProperties;

		public JwtWebSecurityAutoConfiguration(JwtTokenProvider jwtTokenProvider, JwtProperties jwtProperties, ManagementServerProperties managementServerProperties,
											   ConfigServerProperties configServerProperties, AdminServerProperties adminServerProperties,
											   ZuulProperties zuulProperties) {
			super(jwtTokenProvider, jwtProperties);
			this.managementServerContextPath = managementServerProperties.getContextPath();
			this.configServerPrefix = configServerProperties.getPrefix();
			this.adminServerContextPath = adminServerProperties.getContextPath();
			this.zuulProperties = zuulProperties;
		}

		@Override
		public void configure(WebSecurity web) {
			super.configure(web);

			web.ignoring()
				// Spring Boot Admin
				.antMatchers(adminServerContextPath + "/**/*.{js,css}")
				.antMatchers(adminServerContextPath + "/img/**")
				.antMatchers(adminServerContextPath + "/login.html")
				.antMatchers(adminServerContextPath + "/main.html")
				.antMatchers(adminServerContextPath + "/third-party/**");
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			super.configure(http);

			http.httpBasic()
				.realmName(applicationName)
				.and()
				.authorizeRequests()
				// Application
				.antMatchers("/api" + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
				// Eureka
				.antMatchers("/eureka" + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
				// Hystrix
				.antMatchers("/hystrix" + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
				// Swagger
				.antMatchers(SwaggerConstants.DEFAULT_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				.antMatchers(SwaggerConstants.RESOURCES_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				.antMatchers(SwaggerConstants.RESOURCES_CONF_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				// JWT
				.antMatchers(JwtConstants.ENDPOINT_TOKEN).permitAll()
				// Zuul
				.antMatchers(zuulProperties.getPrefix() + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
				// Spring Boot Actuator
				.antMatchers(managementServerContextPath + "/health").permitAll()
				.antMatchers(managementServerContextPath + "/jolokia/").permitAll()
				.antMatchers(managementServerContextPath + "/profiles").permitAll()
				.antMatchers(managementServerContextPath + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
				// Spring Cloud Config
				.antMatchers(configServerPrefix + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
				// Spring Boot Admin
				.antMatchers(adminServerContextPath + ApplicationConstants.SPRING_BOOT_ADMIN_PATTERN + "/*/jolokia/").permitAll()
				.antMatchers(adminServerContextPath + ApplicationConstants.SPRING_BOOT_ADMIN_PATTERN + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
				.anyRequest().authenticated()
				.and()
				.formLogin().loginPage(adminServerContextPath + "/login.html");
		}
	}

	@Configuration
	public static class OAuth2WebSecurityAutoConfiguration {

		@ConditionalOnExpression("'${" + SecurityConstants.PROP_PREFIX + ".oauth2.authorization.client-credentials.client-id}'.length() > 0")
		@Bean
		public OAuth2ClientCredentialsFilter oAuth2ClientCredentialsZuulFilter(ClientCredentialsTokenHolder clientCredentialsTokenHolder,
																			   ZuulProperties zuulProperties, AdminServerProperties adminServerProperties,
																			   OAuth2Properties oAuth2Properties, PathMatcher pathMatcher) {
			return new OAuth2ClientCredentialsFilter(clientCredentialsTokenHolder, zuulProperties, adminServerProperties, oAuth2Properties, pathMatcher);
		}
	}
}
