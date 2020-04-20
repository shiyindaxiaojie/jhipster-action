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
package org.ylzl.eden.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.ylzl.eden.spring.boot.framework.core.FrameworkConstants;
import org.ylzl.eden.spring.boot.framework.core.util.PathMatcherConstants;
import org.ylzl.eden.spring.boot.integration.swagger.SwaggerConstants;
import org.ylzl.eden.spring.boot.security.oauth2.OAuth2Constants;
import org.ylzl.eden.spring.boot.security.oauth2.configurer.OAuth2ResourceServerConfigurerAdapter;

/**
 * 安全自动配置
 *
 * @author gyl
 * @since 1.0.0
 */
@Configuration
public class SecurityAutoConfiguration {

	@Configuration
	public static class OAuth2SecurityAutoConfiguration extends OAuth2ResourceServerConfigurerAdapter {

		@Value(FrameworkConstants.NAME_PATTERN)
		private String applicationName;

		private final TokenStore tokenStore;

		private final String managementServerContextPath;

		public OAuth2SecurityAutoConfiguration(TokenStore tokenStore, ManagementServerProperties managementServerProperties) {
			super(tokenStore);
			this.tokenStore = tokenStore;
			this.managementServerContextPath = managementServerProperties.getServlet().getContextPath();
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			super.configure(http);

			http.authorizeRequests()
				// Application
				.antMatchers("/api" + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
				// Swagger
				.antMatchers(SwaggerConstants.DEFAULT_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				.antMatchers(SwaggerConstants.RESOURCES_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				.antMatchers(SwaggerConstants.RESOURCES_CONF_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				// OAuth2
				.antMatchers(OAuth2Constants.ENDPOINT_LOGIN).permitAll()
				.antMatchers(OAuth2Constants.ENDPOINT_LOGOUT).authenticated()
				// Spring Boot Actuator
				.antMatchers(managementServerContextPath + "/health").permitAll()
				.antMatchers(managementServerContextPath + "/jolokia/").permitAll()
				.antMatchers(managementServerContextPath + "/profiles").permitAll()
				.antMatchers(managementServerContextPath + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated();
		}

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(applicationName).tokenStore(tokenStore);
		}
	}
}
