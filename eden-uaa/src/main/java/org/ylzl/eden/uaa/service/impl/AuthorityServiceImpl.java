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
package org.ylzl.eden.uaa.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ylzl.eden.spring.boot.support.service.impl.JpaServiceImpl;
import org.ylzl.eden.uaa.domain.Authority;
import org.ylzl.eden.uaa.repository.AuthorityRepository;
import org.ylzl.eden.uaa.service.AuthorityService;

/**
 * 权限业务实现
 *
 * @author gyl
 * @since 1.0.0
 */
@Slf4j
@Service("authorityService")
public class AuthorityServiceImpl extends JpaServiceImpl<Authority, Long>
    implements AuthorityService {

  private final AuthorityRepository authorityRepository;

  public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
    super(authorityRepository);
    this.authorityRepository = authorityRepository;
  }

  @Override
  public Authority findOneByCode(String code) {
    return authorityRepository.findOneByCode(code);
  }
}
