package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.AuditUser;

@Service
public class CurrentUserService {

	@Autowired
	private AuditUserService auditUserService;

	public AuditUser getCurrentAuditUser() {
		AuditUser result = auditUserService.findByUsername(getUserAuth()
				.getName());
		return result;
	}

	public Authentication getUserAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
