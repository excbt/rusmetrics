package ru.excbt.datafuse.nmk.data.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.model.security.AuditUserPrincipal;
import ru.excbt.datafuse.nmk.data.service.AuditUserService;

@Service
public class CurrentUserService {

	private static final Logger logger = LoggerFactory
			.getLogger(CurrentUserService.class);

	@Autowired
	private AuditUserService auditUserService;

	@Autowired
	private MockUserService mockUserService;

	/**
	 * 
	 * @return
	 */
	public AuditUser getCurrentAuditUser() {

		Authentication auth = getContextAuth();
		if (auth == null) {
			return getMockAuditUser();
		}

		AuditUserPrincipal userPrincipal = null;
		if (auth.getPrincipal() instanceof AuditUserPrincipal) {
			userPrincipal = (AuditUserPrincipal) auth.getPrincipal();
		} else {
			return getMockAuditUser();
		}

		return new AuditUser(userPrincipal);

	}

	/**
	 * 
	 * @return
	 */
	public Authentication getContextAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * 
	 * @return
	 */
	private AuditUser getMockAuditUser() {
		logger.warn("ATTENTION!!! Using MockUser");

		return mockUserService.getMockAuditUser();
	}

}
