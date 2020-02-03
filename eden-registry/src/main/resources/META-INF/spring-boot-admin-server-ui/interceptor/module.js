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

(function (sbaModules, angular) {
  'use strict';

  var module = angular.module('sba-auth-interceptor', ['sba-core']);
  sbaModules.push(module.name);

  module.factory('authInterceptor', authInterceptor);

  authInterceptor.$inject = ['$rootScope', '$q'];

  function authInterceptor($rootScope, $q) {
    var service = {
      request: request,
      responseError: responseError
    };

    return service;

    function request(config) {
      config.headers = config.headers || {};
      var token = localStorage.getItem('JwtAuthorization') || sessionStorage.getItem('JwtAuthorization');
      if (token) {
        config.headers.JwtAuthorization = 'Bearer ' + token;
      }
      return config;
    }

    function responseError(response) {
      if (response.status === 401) {
        var token = localStorage.getItem('JwtAuthorization') || sessionStorage.getItem('JwtAuthorization');
        if (token) { // 非注销动作不跳转到登录页面
          console.warn('认证失败：%o', response);
        } else {
          top.window.location.href = "login.html?timeout";
        }
      }
      return $q.reject(response);
    }
  }

}(sbaModules, angular));
