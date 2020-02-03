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
package org.ylzl.eden.uaa.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ylzl.eden.uaa.domain.User;
import org.ylzl.eden.uaa.service.UserService;

import javax.servlet.http.HttpServletRequest;

/**
 * 帐号资源
 *
 * @author gyl
 * @since 0.0.1
 */
@Api(tags = {"帐号资源"})
@Slf4j
@RequestMapping("/api")
@RestController
public class AccountResource {

    private final UserService userService;

    public AccountResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST 请求获取当前用户是否已认证");
        return request.getRemoteUser();
    }

    @Timed
    @GetMapping("/account")
    public User readAccount() {
        return userService.findOneWithAuthorities();
    }
}
