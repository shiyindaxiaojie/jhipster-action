package org.ylzl.eden.uaa.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.ylzl.eden.spring.boot.framework.core.FrameworkConstants;
import org.ylzl.eden.spring.boot.framework.core.util.PathMatcherConstants;
import org.ylzl.eden.spring.boot.integration.swagger.SwaggerConstants;
import org.ylzl.eden.spring.boot.security.oauth2.OAuth2Constants;
import org.ylzl.eden.spring.boot.security.oauth2.OAuth2Properties;
import org.ylzl.eden.spring.boot.security.oauth2.configurer.OAuth2AuthorizationServerConfigurerAdapter;
import org.ylzl.eden.spring.boot.security.oauth2.configurer.OAuth2ResourceServerConfigurerAdapter;

import java.util.List;

/**
 * 安全自动配置
 *
 * @author gyl
 * @since 0.0.1
 */
@Slf4j
@Configuration
public class SecurityAutoConfiguration {

	@Configuration
	public static class OAuth2SecurityAutoConfiguration extends OAuth2ResourceServerConfigurerAdapter {

		@Value(FrameworkConstants.NAME_PATTERN)
		private String applicationName;

		private final TokenStore tokenStore;

		private final String managementServerContextPath;

		public OAuth2SecurityAutoConfiguration(TokenStore tokenStore, ManagementServerProperties managementServerProperties) {
			super(tokenStore);
			this.tokenStore = tokenStore;
			this.managementServerContextPath = managementServerProperties.getContextPath();
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			super.configure(http);

			http.authorizeRequests()
				// Application
				.antMatchers("/api/account/activate").permitAll()
				.antMatchers("/api/account/register").permitAll()
				.antMatchers("/api/account/reset-password/init").permitAll()
				.antMatchers("/api/account/reset-password/finish").permitAll()
				.antMatchers("/api/authenticate").permitAll()
				.antMatchers("/api" + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated()
				// Swagger
				.antMatchers(SwaggerConstants.DEFAULT_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				.antMatchers(SwaggerConstants.RESOURCES_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				.antMatchers(SwaggerConstants.RESOURCES_CONF_URL + PathMatcherConstants.ALL_CHILD_PATTERN).permitAll()
				// OAuth2
				.antMatchers(OAuth2Constants.ENDPOINT_LOGIN).permitAll()
				.antMatchers(OAuth2Constants.ENDPOINT_LOGOUT).authenticated()
				// Spring Boot Actuator
				.antMatchers(managementServerContextPath + "/health").permitAll()
				.antMatchers(managementServerContextPath + "/jolokia/").permitAll()
				.antMatchers(managementServerContextPath + "/profiles").permitAll()
				.antMatchers(managementServerContextPath + PathMatcherConstants.ALL_CHILD_PATTERN).authenticated();
		}

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(applicationName).tokenStore(tokenStore);
		}

		/*@Bean
        public AccessDecisionManager accessDecisionManager() {
            List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays.asList(
                new WebExpressionVoter(),
                new CustomRoleVoter(),
                new AuthenticatedVoter());
            return new UnanimousBased(decisionVoters);
        }*/
	}

	@Configuration
	public static class UaaSecurityAutoConfiguration extends OAuth2AuthorizationServerConfigurerAdapter {

		public UaaSecurityAutoConfiguration(AuthenticationManager authenticationManager, TokenStore tokenStore,
											List<TokenEnhancer> tokenEnhancers, OAuth2Properties oAuth2Properties) {
			super(authenticationManager, tokenStore, tokenEnhancers, oAuth2Properties);
		}
	}
}
