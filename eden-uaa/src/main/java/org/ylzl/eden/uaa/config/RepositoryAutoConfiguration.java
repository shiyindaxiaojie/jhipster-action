package org.ylzl.eden.uaa.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.ylzl.eden.spring.boot.data.audit.EnableAuditorAware;
import org.ylzl.eden.spring.boot.data.audit.EnablePersistenceAuditEvent;

/**
 * 数据仓库自动配置
 *
 * @author gyl
 * @since 1.0.0
 */
@EnableAuditorAware
@EnableJpaRepositories(ApplicationConstants.JPA_PACKAGE)
@EnablePersistenceAuditEvent
@EnableTransactionManagement
@Slf4j
@Configuration
public class RepositoryAutoConfiguration {}
