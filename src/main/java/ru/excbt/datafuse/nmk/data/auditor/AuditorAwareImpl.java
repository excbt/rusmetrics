package ru.excbt.datafuse.nmk.data.auditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.AuditUserService;

public class AuditorAwareImpl implements AuditorAware<AuditUser> {

	@Autowired
	private AuditUserService currentUserService;
	
	@Override
	public AuditUser getCurrentAuditor() {
		return currentUserService.getCurrentUser();
	}

}
