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

package org.ylzl.eden.registry.config.swagger;

import de.codecentric.boot.admin.config.AdminServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.ylzl.eden.registry.config.ApplicationConstants;
import org.ylzl.eden.spring.boot.commons.lang.StringConstants;
import org.ylzl.eden.spring.boot.commons.lang.StringUtils;
import org.ylzl.eden.spring.boot.framework.core.FrameworkConstants;
import org.ylzl.eden.spring.boot.framework.core.ProfileConstants;
import org.ylzl.eden.spring.boot.framework.core.util.PathMatcherConstants;
import org.ylzl.eden.spring.boot.integration.swagger.SwaggerConstants;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Discovery Swagger 资源提供服务
 *
 * @author gyl
 * @since 1.0.0
 */
@Profile(ProfileConstants.SPRING_PROFILE_DEVELOPMENT)
@Slf4j
@Primary
@Component
public class DiscoverySwaggerResourcesProvider implements SwaggerResourcesProvider {

  @Value(FrameworkConstants.NAME_PATTERN)
  private String applicationName;

  private final RouteLocator routeLocator;

  private final AdminServerProperties adminServerProperties;

  private final PathMatcher pathMatcher;

  public DiscoverySwaggerResourcesProvider(
      RouteLocator routeLocator,
      AdminServerProperties adminServerProperties,
      PathMatcher pathMatcher) {
    this.routeLocator = routeLocator;
    this.adminServerProperties = adminServerProperties;
    this.pathMatcher = pathMatcher;
  }

  @Override
  public List<SwaggerResource> get() {
    List<SwaggerResource> resources = new ArrayList<>();
    resources.add(swaggerResource(applicationName, SwaggerConstants.DEFAULT_URL));

    List<Route> routes = routeLocator.getRoutes();
    for (Route route : routes) {
      if (shouldFilter(route)) {
        String path = route.getFullPath();
        for (Route tempRoute : routes) {
          // 判断是否配置了 server.context-path
          if (!tempRoute.getId().equals(route.getId())
              && tempRoute.getLocation().endsWith(route.getId())) {
            path =
                route
                    .getFullPath()
                    .replace(
                        StringConstants.SLASH + route.getId(),
                        StringUtils.repeat(StringConstants.SLASH + route.getId(), 2));
            break;
          }
        }
        resources.add(
            swaggerResource(route.getId(), path.replace("**", SwaggerConstants.DEFAULT_URL)));
      }
    }
    return resources;
  }

  private boolean shouldFilter(Route route) {
    // 过滤掉 Spring Boot Admin 自定义的路由
    String prefix = route.getPrefix();
    boolean isSpringBootAdmin =
        pathMatcher.match(
            adminServerProperties.getContextPath()
                + ApplicationConstants.SPRING_BOOT_ADMIN_PATTERN
                + PathMatcherConstants.ALL_CHILD_PATTERN,
            prefix);
    boolean isSpringBootAdminTurbine =
        pathMatcher.match(
            PathMatcherConstants.ALL_CHILD_PATTERN
                + adminServerProperties.getContextPath()
                + ApplicationConstants.SPRING_BOOT_ADMIN_TURBINE_PATTERN
                + PathMatcherConstants.ALL_CHILD_PATTERN,
            prefix);
    return !isSpringBootAdmin && !isSpringBootAdminTurbine;
  }

  private SwaggerResource swaggerResource(String name, String location) {
    SwaggerResource swaggerResource = new SwaggerResource();
    swaggerResource.setName(name);
    swaggerResource.setLocation(location);
    swaggerResource.setSwaggerVersion(SwaggerConstants.VERSION);
    return swaggerResource;
  }
}
