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

package org.ylzl.eden.gateway.scheduling;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContextHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Zuul 调度器
 *
 * @author gyl
 * @since 1.0.0
 */
@Slf4j
@Service
public class ZuulScheduled {

    private final RouteLocator routeLocator;

    private final ZuulProperties zuulProperties;

    private final ApplicationEventPublisher applicationEventPublisher;

    public ZuulScheduled(RouteLocator routeLocator, ZuulProperties zuulProperties, ApplicationEventPublisher applicationEventPublisher) {
        this.routeLocator = routeLocator;
        this.zuulProperties = zuulProperties;
        this.applicationEventPublisher = applicationEventPublisher;
    }

	/**
	 * 更新 Zuul 路由
	 */
	@Scheduled(fixedDelay = 60_000)
    public void updateZuulRoutes() {
        boolean isDirty = false;

        List<Application> applications = EurekaServerContextHolder.getInstance().getServerContext().getRegistry()
            .getApplications().getRegisteredApplications();

        if (putIfDirty(applications)) {
            isDirty = true;
        }

        if (removeIfDirty(applications)) {
            isDirty = true;
        }

        if (isDirty) {
            log.info("Zuul routes have changed, refreshing the configuration");
            this.applicationEventPublisher.publishEvent(new RoutesRefreshedEvent(routeLocator));
        }
    }

    private boolean putIfDirty(List<Application> applications) {
        boolean isDirty = false;
        for (Application application : applications) {
            for (InstanceInfo instanceInfo : application.getInstances()) {
                String status = instanceInfo.getStatus().toString();
                if (!status.equals(InstanceInfo.InstanceStatus.UP.name()) &&
                    !status.equals(InstanceInfo.InstanceStatus.STARTING.name())) {
                    continue;
                }
                String instanceId = instanceInfo.getId();
                String url = instanceInfo.getHomePageUrl();
                log.debug("Checking instanceId: {}, url: {}", instanceId, url);

                EnhancedZuulRoute route = new EnhancedZuulRoute(instanceId, "/" + application.getName() + "/" +
                    instanceId + "/**", null, url, zuulProperties.isStripPrefix(),
                    zuulProperties.getRetryable(), Collections.<String>emptySet(), status);

                if (zuulProperties.getRoutes().containsKey(instanceId)) {
                    log.debug("Instance '{}' already registered", instanceId);
                    if (!zuulProperties.getRoutes().get(instanceId).getUrl().equals(url) ||
                        !((EnhancedZuulRoute) zuulProperties.getRoutes().get(instanceId)).getStatus()
                            .equals(instanceInfo.getStatus().toString())) {
                        log.debug("Updating instance '{}' with new URL: {}", instanceId, url);
                        zuulProperties.getRoutes().put(instanceId, route);
                        isDirty = true;
                    }
                } else {
                    log.debug("Adding instance '{}' with URL: {}", instanceId, url);
                    zuulProperties.getRoutes().put(instanceId, route);
                    isDirty = true;
                }
            }
        }
        return isDirty;
    }

	private boolean removeIfDirty(List<Application> applications) {
        boolean isDirty = false;
        List<String> zuulRoutesToRemove = new ArrayList<>();
        for (String key : zuulProperties.getRoutes().keySet()) {
            int count = 0;
            for (Application application: applications) {
                for (InstanceInfo instanceInfo: application.getInstances()) {
                    if (key.equals(instanceInfo.getId())) {
                        count++;
                        break;
                    }
                }
                if (count > 0) {
                    break;
                }
            }
            if (count == 0) {
                log.debug("Removing instance '{}'", key);
                zuulRoutesToRemove.add(key);
                isDirty = true;
            }
        }
        for (String key : zuulRoutesToRemove) {
            zuulProperties.getRoutes().remove(key);
        }
        return isDirty;
    }

	@Data
	public static class EnhancedZuulRoute extends ZuulProperties.ZuulRoute {

		private String status;

		public EnhancedZuulRoute(String id, String path, String serviceId, String url, boolean stripPrefix, Boolean retryable,
							Set<String> sensitiveHeaders, String status) {
			super(id, path, serviceId, url, stripPrefix, retryable, sensitiveHeaders);
			this.status = status;
		}
	}
}
