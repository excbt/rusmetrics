package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.AuditUser;

@Service
public class CurrentUserService {

	private final String DEVELOPER_USER_NAME = "developer_user";
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(CurrentUserService.class);
	
	@Autowired
	private AuditUserService auditUserService;

	/**
	 * 
	 * @return
	 */
	public AuditUser getCurrentAuditUser() {
		Authentication auth = getUserAuth();
		if (auth == null) {
			//logger.warn("Security authentication object is not avaibale. Using instead of it DEVELOPER_USER");
			return null;
			//return auditUserService.findByUsername(DEVELOPER_USER_NAME);
		}
		
		AuditUser result = auditUserService.findByUsername(
				auth.getName());
		return result;
	}

	public Authentication getUserAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
