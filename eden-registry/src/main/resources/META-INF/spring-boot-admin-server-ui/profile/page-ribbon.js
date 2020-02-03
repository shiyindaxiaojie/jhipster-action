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

$(function () {
  getProfileInfo();
});

function getProfileInfo() {
  $.ajax({
    type: 'GET',
    url: '/management/profiles',
    dataType: "json",
    error: function (xhr, status, error) {

    },
    success: function (result) {
      if (result) {
        if (result.hasOwnProperty('ribbonEnv')) {
          let ribbonEnvText = '';
          if (result.ribbonEnv === 'dev') {
            ribbonEnvText = '开发';
          } else if (result.ribbonEnv === 'test') {
            ribbonEnvText = '测试';
          } else if (result.ribbonEnv === 'demo') {
            ribbonEnvText = '演示';
          }
          $('.ribbon').show();
          $('#ribbonEnv').text(ribbonEnvText);
        }
        if (('#activeProfiles').length > 0 && result.hasOwnProperty('activeProfiles')) {

        }
      }
    }
  });
}
