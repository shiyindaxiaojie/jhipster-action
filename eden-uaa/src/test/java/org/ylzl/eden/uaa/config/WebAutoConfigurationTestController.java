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
package org.ylzl.eden.uaa.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Web 自动配置测试控制器
 *
 * @author gyl
 * @since 1.0.0
 */
@RestController
public class WebAutoConfigurationTestController {

  /** API 跨域接口 */
  @GetMapping("/api/test-cors")
  public void testCorsOnApiPath() {}

  /** 其他跨域接口 */
  @GetMapping("/other/test-cors")
  public void testCorsOnOtherPath() {}
}
