package ru.excbt.datafuse.nmk.data.service.support;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.AuditUserService;

@Service
public class CurrentUserService {

	private final String DEVELOPER_USER_NAME = "developer_user";
	// private final long DEVELOPER_USER_ID = 19748714L;

	private static final Logger logger = LoggerFactory
			.getLogger(CurrentUserService.class);

	@Autowired
	private AuditUserService auditUserService;

	private final ThreadLocal<Map<String, AuditUser>> currentUsersMap = new ThreadLocal<Map<String, AuditUser>>();

	/**
	 * 
	 */
	private Map<String, AuditUser> getMap() {
		Map<String, AuditUser> result = currentUsersMap.get();
		if (result == null) {
			result = new HashMap<>();
			currentUsersMap.set(result);
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public AuditUser getCurrentAuditUser() {
		Authentication auth = getUserAuth();

		if (auth == null) {
			logger.warn("Authentication of Spring security is NULL");
			return null;
		}

		String userName = DEVELOPER_USER_NAME;
		AuditUser srcUser = getMap().get(userName);

		if (srcUser == null) {
			srcUser = auditUserService.findByUserName(userName);
			if (srcUser != null) {
				getMap().put(userName, srcUser);
			}
			// scrObject = auditUserService.findOne(DEVELOPER_USER_ID);
		}
		// return safe copy of scrObject
		return srcUser != null ? new AuditUser(srcUser) : null;

	}

	/**
	 * 
	 * @return
	 */
	public Authentication getUserAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
