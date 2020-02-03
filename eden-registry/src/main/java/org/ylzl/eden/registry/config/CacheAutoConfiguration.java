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

package org.ylzl.eden.registry.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.MapConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存自动配置
 *
 * @author gyl
 * @since 0.0.1
 */
@Configuration
public class CacheAutoConfiguration {

    private static final String HAZELCASE_JMX = "hazelcast.jmx";

    private static final String ADMIN_APPLICATION_STORE = "spring-boot-admin-application-store";

    private static final String ADMIN_EVENT_STORE = "spring-boot-admin-event-store";

    @Bean
    public Config hazelcastConfig() {
        return new Config().setProperty(HAZELCASE_JMX, "true")
			.addMapConfig(new MapConfig(ADMIN_APPLICATION_STORE).setBackupCount(1).setEvictionPolicy(EvictionPolicy.LRU))
			.addListConfig(new ListConfig(ADMIN_EVENT_STORE).setBackupCount(1).setMaxSize(1000));
    }
}
