package ru.excbt.datafuse.nmk.data.auditor;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import ru.excbt.datafuse.nmk.data.model.AuditUser;

@Component
public class MockAuditorAware implements AuditorAware<AuditUser> {

	private AuditUser auditUser;
	
	public AuditUser getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(AuditUser auditUser) {
		this.auditUser = auditUser;
	}

	
	@Override
	public AuditUser getCurrentAuditor() {
		return auditUser;
	}

}
