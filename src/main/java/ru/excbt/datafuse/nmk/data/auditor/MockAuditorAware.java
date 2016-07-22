package ru.excbt.datafuse.nmk.data.auditor;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import ru.excbt.datafuse.nmk.data.model.V_AuditUser;

/**
 * Заглушка для аудита сущностей
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.05.2015
 *
 */
@Component
public class MockAuditorAware implements AuditorAware<V_AuditUser> {

	private V_AuditUser auditUser;

	public V_AuditUser getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(V_AuditUser auditUser) {
		this.auditUser = auditUser;
	}

	@Override
	public V_AuditUser getCurrentAuditor() {
		return auditUser;
	}

}
