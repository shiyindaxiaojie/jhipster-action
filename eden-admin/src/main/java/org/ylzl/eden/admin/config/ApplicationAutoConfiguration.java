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

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.ylzl.eden.spring.boot.framework.core.FrameworkConstants;
import org.ylzl.eden.spring.boot.security.jwt.JwtProperties;
import org.ylzl.eden.spring.boot.security.jwt.token.JwtTokenProvider;

import java.util.Map;

/**
 * 应用自动配置
 *
 * @author gyl
 * @since 1.0.0
 */
@EnableAdminServer
@EnableCircuitBreaker
@EnableDiscoveryClient
@EnableFeignClients
@EnableTurbine
@EnableZuulProxy
@EnableConfigurationProperties({ApplicationProperties.class})
@Slf4j
@Configuration
public class ApplicationAutoConfiguration {

	@Bean
	public HttpHeadersProvider jwtHttpHeadersProvider(JwtProperties jwtProperties, JwtTokenProvider jwtTokenProvider) {
		log.debug("Inject HttpHeadersProvider（jwtHttpHeadersProvider）");
		return (instance) -> {
			Map<String, String> metaData = instance.getRegistration().getMetadata();
			String userName = metaData.get("user.name");
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add(jwtProperties.getAuthorization().getHeader(),
				jwtTokenProvider.build(userName, FrameworkConstants.SYSTEM, "", true));
			return httpHeaders;
		};
	}
}
