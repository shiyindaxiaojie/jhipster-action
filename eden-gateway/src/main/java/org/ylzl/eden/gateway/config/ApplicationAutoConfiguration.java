package org.ylzl.eden.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulServer;
import org.springframework.context.annotation.Configuration;

/**
 * 应用自动配置
 *
 * @author gyl
 * @since 1.0.0
 */
@EnableDiscoveryClient
@EnableZuulServer
@EnableConfigurationProperties({ApplicationProperties.class})
@Slf4j
@Configuration
public class ApplicationAutoConfiguration {}
