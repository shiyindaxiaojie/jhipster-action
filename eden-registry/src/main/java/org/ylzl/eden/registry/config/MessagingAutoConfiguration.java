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

import de.codecentric.boot.admin.notify.LoggingNotifier;
import de.codecentric.boot.admin.notify.Notifier;
import de.codecentric.boot.admin.notify.RemindingNotifier;
import de.codecentric.boot.admin.notify.filter.FilteringNotifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

/**
 * 消息自动配置
 *
 * @author gyl
 * @since 0.0.1
 */
@EnableScheduling
@Configuration
public class MessagingAutoConfiguration {

  @Bean
  @Primary
  public RemindingNotifier remindingNotifier() {
    RemindingNotifier remindingNotifier =
        new RemindingNotifier(filteringNotifier(loggerNotifier()));
    remindingNotifier.setReminderPeriod(TimeUnit.MINUTES.toMillis(10));
    return remindingNotifier;
  }

  @Bean
  public FilteringNotifier filteringNotifier(Notifier delegate) {
    return new FilteringNotifier(delegate);
  }

  @Bean
  public LoggingNotifier loggerNotifier() {
    return new LoggingNotifier();
  }

  @Scheduled(fixedRate = 60_000)
  public void remind() {
    remindingNotifier().sendReminders();
  }
}
