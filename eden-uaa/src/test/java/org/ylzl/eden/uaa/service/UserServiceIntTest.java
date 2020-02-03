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

package org.ylzl.eden.uaa.service;

import org.junit.Before;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.ylzl.eden.spring.boot.commons.lang.RandomStringUtils;
import org.ylzl.eden.spring.boot.commons.lang.time.DateUtils;
import org.ylzl.eden.uaa.Application;
import org.ylzl.eden.uaa.service.dto.UserDTO;

import java.util.Date;

import static org.mockito.Mockito.when;

/**
 * 用户业务实现集成测试
 *
 * @author gyl
 * @since 0.0.1
 */
@SpringBootTest(classes = Application.class)
public class UserServiceIntTest {

    private static final String DEFAULT_LOGIN = "gyl";

    private static final String DEFAULT_EMAIL = "1813986321@qq.com";

    private static final String DEFAULT_LANGKEY = "zh-cn";

    @Autowired
    private UserService userService;

    @Autowired
    private AuditingHandler auditingHandler;

    @Mock
    DateTimeProvider dateTimeProvider;

    private UserDTO userDTO;

    @Before
    public void init() {
        userDTO = new UserDTO();
        userDTO.setLogin(DEFAULT_LOGIN);
        userDTO.setPassword(RandomStringUtils.random(60));
        userDTO.setActivated(true);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setLangKey(DEFAULT_LANGKEY);

        when(dateTimeProvider.getNow()).thenReturn(DateUtils.toCalendar(new Date()));
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }
}
