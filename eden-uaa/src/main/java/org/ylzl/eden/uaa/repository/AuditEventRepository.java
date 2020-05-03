package org.ylzl.eden.uaa.repository;

import org.springframework.stereotype.Repository;
import org.ylzl.eden.spring.boot.data.audit.repository.PersistenceAuditEventRepository;
import org.ylzl.eden.uaa.domain.AuditEvent;

/**
 * 审计事件数据仓库
 *
 * @author gyl
 * @since 0.0.1
 */
@Repository
public interface AuditEventRepository extends PersistenceAuditEventRepository<AuditEvent, Long> {}
