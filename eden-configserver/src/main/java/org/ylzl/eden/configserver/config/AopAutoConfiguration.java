package org.ylzl.eden.configserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.ylzl.eden.configserver.aop.LoggingAspect;
import org.ylzl.eden.spring.boot.framework.core.ProfileConstants;

/**
 * 切面自动配置
 *
 * @author gyl
 * @since 1.0.0
 */
@EnableAspectJAutoProxy
@Slf4j
@Configuration
public class AopAutoConfiguration {

    /**
     * 注入日志切面
     *
     * @return {@link LoggingAspect}
     */
    /*@Profile(ProfileConstants.SPRING_PROFILE_DEVELOPMENT)
    @Bean
    public LoggingAspect loggingAspect() {
        log.debug("Inject Logging aspect");
        return new LoggingAspect();
    }*/
}
