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

import de.codecentric.boot.admin.config.EnableAdminServer;
import de.codecentric.boot.admin.config.RevereseZuulProxyConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.ZuulProxyConfiguration;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.discovery.ServiceRouteMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 应用自动配置
 *
 * @author gyl
 * @since 0.0.1
 */
@EnableAdminServer
@EnableCircuitBreaker
@EnableDiscoveryClient
@EnableEurekaServer
@EnableFeignClients
@EnableHystrixDashboard
@EnableTurbine
@EnableZuulProxy
@EnableConfigurationProperties({ApplicationProperties.class})
@Slf4j
@Configuration
public class ApplicationAutoConfiguration {

  @AutoConfigureBefore({RevereseZuulProxyConfiguration.class})
  @Slf4j
  @Configuration
  public static class FixedZuulProxyAutoConfiguration extends ZuulProxyConfiguration {

    /**
     * FIXME 在 RevereseZuulProxyConfiguration 之前装配服务注册路由，否则默认的 routes 路由配置会被 Spring Boot Admin 覆盖
     *
     * @see de.codecentric.boot.admin.config.RevereseZuulProxyConfiguration
     * @return DiscoveryClientRouteLocator
     */
    @Bean
    public DiscoveryClientRouteLocator discoveryClientRouteLocator(
        DiscoveryClient discoveryClient, ServiceRouteMapper serviceRouteMapper) {
      return new DiscoveryClientRouteLocator(
          this.server.getServletPrefix(), discoveryClient, this.zuulProperties, serviceRouteMapper);
    }
  }
}
