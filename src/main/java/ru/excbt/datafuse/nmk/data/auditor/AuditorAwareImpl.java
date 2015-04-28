package ru.excbt.datafuse.nmk.data.auditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.support.CurrentAuditUserService;

public class AuditorAwareImpl implements AuditorAware<AuditUser> {

	@Autowired
	private CurrentAuditUserService currentUserService;
	
	@Override
	public AuditUser getCurrentAuditor() {
		return currentUserService.getCurrentAuditUser();
	}

}
