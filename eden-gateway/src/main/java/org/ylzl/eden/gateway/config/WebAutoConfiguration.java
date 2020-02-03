package org.ylzl.eden.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.ylzl.eden.spring.boot.framework.core.FrameworkProperties;
import org.ylzl.eden.spring.boot.framework.web.WebConfigurerAdapter;

/**
 * Web 自动配置
 *
 * @author gyl
 * @since 0.0.1
 */
@Slf4j
@Configuration
public class WebAutoConfiguration extends WebConfigurerAdapter {

	public WebAutoConfiguration(FrameworkProperties frameworkProperties, Environment environment) {
		super(frameworkProperties, environment);
	}
}
