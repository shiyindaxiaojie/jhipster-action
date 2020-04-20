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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.ylzl.eden.spring.boot.framework.core.FrameworkConstants;
import org.ylzl.eden.spring.boot.framework.core.util.PathMatcherConstants;
import org.ylzl.eden.spring.boot.integration.swagger.SwaggerConstants;
import org.ylzl.eden.spring.boot.security.jwt.JwtConstants;
import org.ylzl.eden.spring.boot.security.jwt.JwtProperties;
import org.ylzl.eden.spring.boot.security.jwt.configurer.JwtWebSecurityConfigurerAdapter;
import org.ylzl.eden.spring.boot.security.jwt.token.JwtTokenProvider;
import org.ylzl.eden.spring.boot.security.web.authentication.UnauthorizedEntryPointAdapter;

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
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new UnauthorizedEntryPointAdapter();
	}

	@Primary
	@Configuration
	public static class JwtWebSecurityAutoConfiguration extends JwtWebSecurityConfigurerAdapter {

		private final String webEndpointBasePath;

		public JwtWebSecurityAutoConfiguration(JwtTokenProvider jwtTokenProvider, JwtProperties jwtProperties,
											   WebEndpointProperties webEndpointProperties) {
			super(jwtTokenProvider, jwtProperties);
			this.webEndpointBasePath = webEndpointProperties.getBasePath();
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			super.configure(http);
			http.authorizeRequests((authorizeRequests) -> authorizeRequests
					// Application
					.antMatchers("/api" + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
					// Eureka
					.antMatchers("/eureka" + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
					// Swagger
					.antMatchers(SwaggerConstants.DEFAULT_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
					.antMatchers(SwaggerConstants.RESOURCES_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
					.antMatchers(SwaggerConstants.RESOURCES_CONF_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
					// JWT
					.antMatchers(JwtConstants.ENDPOINT_TOKEN).permitAll()
					// Spring Boot Actuator
					.antMatchers(this.webEndpointBasePath + "/health").permitAll()
					.antMatchers(this.webEndpointBasePath + "/profiles").permitAll()
					.antMatchers(this.webEndpointBasePath + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
					.anyRequest().authenticated());
		}
	}
}
