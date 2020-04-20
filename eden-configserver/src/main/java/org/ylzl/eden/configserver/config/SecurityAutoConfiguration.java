package org.ylzl.eden.configserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.ylzl.eden.spring.boot.framework.core.FrameworkConstants;
import org.ylzl.eden.spring.boot.framework.core.util.PathMatcherConstants;
import org.ylzl.eden.spring.boot.integration.swagger.SwaggerConstants;
import org.ylzl.eden.spring.boot.security.jwt.JwtConstants;
import org.ylzl.eden.spring.boot.security.jwt.JwtProperties;
import org.ylzl.eden.spring.boot.security.jwt.configurer.JwtWebSecurityConfigurerAdapter;
import org.ylzl.eden.spring.boot.security.jwt.token.JwtTokenProvider;

/**
 * 安全自动配置
 *
 * @author gyl
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class SecurityAutoConfiguration {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Primary
	@Configuration
	public static class JwtWebSecurityAutoConfiguration extends JwtWebSecurityConfigurerAdapter {

		private final String webEndpointBasePath;

		private final String configServerPrefix;

		public JwtWebSecurityAutoConfiguration(JwtTokenProvider jwtTokenProvider, JwtProperties jwtProperties,
											   WebEndpointProperties webEndpointProperties, ConfigServerProperties configServerProperties) {
			super(jwtTokenProvider, jwtProperties);
			this.webEndpointBasePath = webEndpointProperties.getBasePath();
			this.configServerPrefix = configServerProperties.getPrefix();
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			super.configure(http);

			http.authorizeRequests()
				// Application
				.antMatchers("/api" + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
				// Swagger
				.antMatchers(SwaggerConstants.DEFAULT_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				.antMatchers(SwaggerConstants.RESOURCES_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				.antMatchers(SwaggerConstants.RESOURCES_CONF_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				// JWT
				.antMatchers(JwtConstants.ENDPOINT_TOKEN).permitAll()
				// Spring Boot Actuator
				.antMatchers(webEndpointBasePath + "/health").permitAll()
				.antMatchers(webEndpointBasePath + "/profiles").permitAll()
				.antMatchers(webEndpointBasePath + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
				// Spring Cloud Config
				.antMatchers(configServerPrefix + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated();
		}
	}
}
