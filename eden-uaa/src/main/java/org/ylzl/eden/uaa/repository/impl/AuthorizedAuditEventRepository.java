package org.ylzl.eden.uaa.repository.impl;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.ylzl.eden.spring.boot.data.audit.event.AuditEventConverter;
import org.ylzl.eden.spring.boot.data.audit.event.PersistentAuditEvent;
import org.ylzl.eden.spring.boot.data.audit.repository.PersistenceAuditEventRepository;
import org.ylzl.eden.spring.boot.data.audit.repository.PersistenceAuditEventRepositoryAdapter;
import org.ylzl.eden.spring.boot.framework.core.FrameworkConstants;

/**
 * 认证的审计事件数据仓库
 *
 * @author gyl
 * @since 1.0.0
 */
@Repository
public class AuthorizedAuditEventRepository extends PersistenceAuditEventRepositoryAdapter {

    private static final String AUTHORIZATION_FAILURE = "AUTHORIZATION_FAILURE";

    public AuthorizedAuditEventRepository(PersistenceAuditEventRepository persistenceAuditEventRepository, AuditEventConverter auditEventConverter) {
		super(persistenceAuditEventRepository, auditEventConverter);
    }

	@Override
	public PersistentAuditEvent createPersistentAuditEvent(AuditEvent event) {
		if (!AUTHORIZATION_FAILURE.equals(event.getType()) && !FrameworkConstants.ANONYMOUS_USER.equals(event.getPrincipal())) {
			return new org.ylzl.eden.uaa.domain.AuditEvent();
		}
    	return null;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void add(AuditEvent event) {
        super.add(event);
    }
}
