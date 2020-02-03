package org.ylzl.eden.gateway.config;

import org.springframework.context.annotation.Profile;
import org.ylzl.eden.spring.boot.framework.core.ProfileConstants;
import org.ylzl.eden.gateway.aop.LoggingAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 切面自动配置
 *
 * @author gyl
 * @since 0.0.1
 */
@EnableAspectJAutoProxy
@Slf4j
@Configuration
public class AopAutoConfiguration {

    /**
     * 配置日志切面
     *
     * @return
     */
    @Profile(ProfileConstants.SPRING_PROFILE_DEVELOPMENT)
    @Bean
    public LoggingAspect loggingAspect() {
		log.debug("Inject Logging aspect");
		return new LoggingAspect();
    }
}
