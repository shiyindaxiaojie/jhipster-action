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

package org.ylzl.eden.registry.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import de.codecentric.boot.admin.config.AdminServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.PathMatcher;
import org.ylzl.eden.registry.config.ApplicationConstants;
import org.ylzl.eden.spring.boot.cloud.zuul.ZuulConstants;
import org.ylzl.eden.spring.boot.framework.core.util.PathMatcherConstants;
import org.ylzl.eden.spring.boot.security.core.enums.AuthenticationTypeEnum;
import org.ylzl.eden.spring.boot.security.oauth2.OAuth2Properties;
import org.ylzl.eden.spring.boot.security.oauth2.token.store.ClientCredentialsTokenHolder;

/**
 * UAA 令牌过滤器
 *
 * @author gyl
 * @since 1.0.0
 */
@Slf4j
public class OAuth2ClientCredentialsFilter extends ZuulFilter {

  private final ClientCredentialsTokenHolder clientCredentialsTokenHolder;

  private final ZuulProperties zuulProperties;

  private final AdminServerProperties adminServerProperties;

  private final OAuth2Properties oAuth2Properties;

  private final PathMatcher pathMatcher;

  public OAuth2ClientCredentialsFilter(
      ClientCredentialsTokenHolder clientCredentialsTokenHolder,
      ZuulProperties zuulProperties,
      AdminServerProperties adminServerProperties,
      OAuth2Properties oAuth2Properties,
      PathMatcher pathMatcher) {
    this.clientCredentialsTokenHolder = clientCredentialsTokenHolder;
    this.zuulProperties = zuulProperties;
    this.adminServerProperties = adminServerProperties;
    this.oAuth2Properties = oAuth2Properties;
    this.pathMatcher = pathMatcher;
  }

  @Override
  public String filterType() {
    return ZuulConstants.FILTER_TYPE_PRE;
  }

  @Override
  public int filterOrder() {
    return 0;
  }

  @Override
  public Object run() {
    try {
      OAuth2AccessToken oAuth2AccessToken = clientCredentialsTokenHolder.get();
      if (oAuth2AccessToken != null) {
        RequestContext.getCurrentContext()
            .addZuulRequestHeader(
                oAuth2Properties.getAuthorization().getHeader(),
                AuthenticationTypeEnum.BEARER_TOKEN.getAuthorization(oAuth2AccessToken.getValue()));
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public boolean shouldFilter() {
    String requestURI = RequestContext.getCurrentContext().getRequest().getRequestURI();
    // 过滤掉 Spring Boot Admin 自定义的路由
    boolean isSpringBootAdmin =
        pathMatcher.match(
            adminServerProperties.getContextPath()
                + ApplicationConstants.SPRING_BOOT_ADMIN_PATTERN
                + PathMatcherConstants.ALL_CHILD_PATTERN,
            requestURI);
    boolean isSpringBootAdminTurbine =
        pathMatcher.match(
            PathMatcherConstants.ALL_CHILD_PATTERN
                + adminServerProperties.getContextPath()
                + ApplicationConstants.SPRING_BOOT_ADMIN_TURBINE_PATTERN
                + PathMatcherConstants.ALL_CHILD_PATTERN,
            requestURI);
    return isSpringBootAdmin
        || isSpringBootAdminTurbine
        || pathMatcher.match(
            zuulProperties.getPrefix() + PathMatcherConstants.ALL_CHILD_PATTERN, requestURI);
  }
}
