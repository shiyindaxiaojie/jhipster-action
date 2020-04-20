package org.ylzl.eden.uaa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 应用配置属性
 *
 * @author gyl
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
public class ApplicationProperties {

	private final User user = new User();

	@Data
	public class User {

		private Integer removeNotActivatedAmount;
	}
}
