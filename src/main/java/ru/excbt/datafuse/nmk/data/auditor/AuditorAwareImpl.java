package ru.excbt.datafuse.nmk.data.auditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import ru.excbt.datafuse.nmk.data.model.SystemUser;
import ru.excbt.datafuse.nmk.data.service.CurrentUserService;

public class AuditorAwareImpl implements AuditorAware<SystemUser> {

	@Autowired
	private CurrentUserService currentUserService;
	
	@Override
	public SystemUser getCurrentAuditor() {
		return currentUserService.getCurrentUser();
	}

}
