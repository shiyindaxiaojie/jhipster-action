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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ylzl.eden.spring.boot.commons.regex.RegexPattern;
import org.ylzl.eden.uaa.domain.User;
import org.ylzl.eden.uaa.service.UserService;
import org.ylzl.eden.uaa.service.dto.UserDTO;
import org.ylzl.eden.uaa.service.mapper.UserMapper;
import org.ylzl.eden.uaa.web.rest.vm.UserVM;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Function;

/**
 * 用户接口
 *
 * @author gyl
 * @since 1.0.0
 */
@Api(tags = {"用户接口"})
@Slf4j
@RequestMapping("/api/users")
@RestController
public class UserResource {

  private final UserService userService;

  public UserResource(UserService userService) {
    this.userService = userService;
  }

  @ApiOperation(value = "创建用户")
  @PostMapping
  public ResponseEntity<UserVM> create(@Valid @RequestBody UserDTO dto) throws URISyntaxException {
    User createdUser = userService.create(dto);
    UserVM vm = UserMapper.INSTANCE.userToUserVM(createdUser);
    return ResponseEntity.created(new URI("/api/users/" + vm.getLogin())).body(vm);
  }

  @ApiOperation(value = "更新用户")
  @PutMapping
  public ResponseEntity<UserVM> update(@Valid @RequestBody UserDTO dto) {
    User modifiedUser = userService.update(dto);
    UserVM vm = UserMapper.INSTANCE.userToUserVM(modifiedUser);
    return ResponseEntity.ok().body(vm);
  }

  @ApiOperation(value = "删除用户")
  @ApiImplicitParam(
      value = "账户",
      name = "login",
      required = true,
      paramType = "path",
      dataType = "String")
  @DeleteMapping("/{login:" + RegexPattern.REGEX_USERNAME + "}")
  public ResponseEntity<Void> delete(@PathVariable String login) {
    userService.delete(login);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "获取用户详情")
  @ApiImplicitParam(
      value = "账户",
      name = "login",
      required = true,
      paramType = "path",
      dataType = "String")
  @GetMapping("/{login:" + RegexPattern.REGEX_USERNAME + "}")
  public ResponseEntity<UserVM> get(@PathVariable String login) {
    User user = userService.findOneWithAuthoritiesByLogin(login);
    UserVM vm = UserMapper.INSTANCE.userToUserVM(user);
    return ResponseEntity.ok().body(vm);
  }

  @ApiOperation(value = "获取用户列表")
  @GetMapping
  public ResponseEntity<List<UserVM>> getAll(Pageable pageable) {
    Page<User> users = userService.findAllManagedUsers(pageable);
    Page<UserVM> vms =
        users.map(
            new Function<User, UserVM>() {

              @Override
              public UserVM apply(User user) {
                return UserMapper.INSTANCE.userToUserVM(user);
              }
            });
    return new ResponseEntity<>(vms.getContent(), HttpStatus.OK);
  }
}
