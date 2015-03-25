package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.repository.AuditUserRepository;

@Service
public class CurrentUserService {

	private final String DEVELOPER_USER_NAME = "developer_user";
	private final long DEVELOPER_USER_ID = 19748714L;
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(CurrentUserService.class);
	
	
	@Autowired
	private AuditUserRepository auditUserRepository;
	
	
	
	private AuditUser getUser(String userName) {
		List<AuditUser> users = auditUserRepository.findByUserName(userName);
		if (users.size() == 1) {
			return users.get(0);
		}
		return null;
		
	}

	/**
	 * 
	 * @return
	 */
	public AuditUser getCurrentAuditUser() {
		Authentication auth = getUserAuth();
		logger.debug("auth {}", auth);
		
		//List<AuditUser> users = auditUserRepository.findByUserName(DEVELOPER_USER_NAME);
		
		return auditUserRepository.findOne(DEVELOPER_USER_ID);
//		Authentication auth = getUserAuth();
//		if (auth == null) {
//			//logger.warn("Security authentication object is not avaibale. Using instead of it DEVELOPER_USER");
//			return getUser(DEVELOPER_USER_NAME);
//			//return auditUserService.findByUsername(DEVELOPER_USER_NAME);
//		}
//	
//		return getUser(auth.getName());
	}

	public Authentication getUserAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
