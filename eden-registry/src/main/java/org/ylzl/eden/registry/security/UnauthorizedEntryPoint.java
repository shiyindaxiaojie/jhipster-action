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

package org.ylzl.eden.registry.security;

import de.codecentric.boot.admin.config.AdminServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.ylzl.eden.spring.boot.framework.web.util.RequestContextHolderUtils;
import org.ylzl.eden.spring.boot.security.web.authentication.UnauthorizedEntryPointAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未认证处理
 *
 * @author gyl
 * @since 0.0.1
 */
@Slf4j
public class UnauthorizedEntryPoint extends UnauthorizedEntryPointAdapter {

	private final AdminServerProperties adminServerProperties;

	public UnauthorizedEntryPoint(AdminServerProperties adminServerProperties) {
		this.adminServerProperties = adminServerProperties;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
		log.debug("Pre-authenticated entry point called. Rejecting access: {}", request.getRequestURI());
		if (RequestContextHolderUtils.isAjaxRequest()) {
			super.commence(request, response, exception);
		} else {
			response.sendRedirect(request.getContextPath() + adminServerProperties.getContextPath() + "/login.html");
		}
	}
}
