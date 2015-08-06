package ru.excbt.datafuse.nmk.data.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.model.FullUserInfo;
import ru.excbt.datafuse.nmk.data.model.security.AuditUserPrincipal;
import ru.excbt.datafuse.nmk.data.repository.FullUserInfoRepository;
import ru.excbt.datafuse.nmk.data.service.AuditUserService;
import ru.excbt.datafuse.nmk.security.SubscriberUserDetails;

@Service
public class CurrentUserService {

	private static final Logger logger = LoggerFactory
			.getLogger(CurrentUserService.class);

	@Autowired
	private AuditUserService auditUserService;

	@Autowired
	private MockUserService mockUserService;

	@Autowired
	private FullUserInfoRepository fullUserInfoRepository;

	/**
	 * 
	 * @return
	 */
	public AuditUser getCurrentAuditUser() {
		SubscriberUserDetails subscriberUserDetails = getCurrentUserDetails();
		if (subscriberUserDetails == null) {
			return getMockAuditUser();
		}
		return new AuditUser(subscriberUserDetails);

	}

	/**
	 * 
	 * @return
	 */
	@Deprecated
	protected AuditUser getCurrentAuditUserOld() {
		AuditUserPrincipal userPrincipal = getCurrentAuditUserPrincipal();
		if (userPrincipal == null) {
			return getMockAuditUser();
		}
		return new AuditUser(userPrincipal);

	}

	/**
	 * 
	 * @return
	 */
	@Deprecated
	protected AuditUserPrincipal getCurrentAuditUserPrincipal() {
		Authentication auth = getContextAuth();
		if (auth == null) {
			return null;
		}

		AuditUserPrincipal userPrincipal = null;
		if (auth.getPrincipal() instanceof AuditUserPrincipal) {
			userPrincipal = (AuditUserPrincipal) auth.getPrincipal();
		} else {
			return null;
		}
		return userPrincipal;
	}

	/**
	 * 
	 * @return
	 */
	public SubscriberUserDetails getCurrentUserDetails() {
		Authentication auth = getContextAuth();
		if (auth == null) {
			logger.warn("auth is null");
			return null;
		}

		SubscriberUserDetails result = null;
		if (auth.getPrincipal() instanceof SubscriberUserDetails) {
			result = (SubscriberUserDetails) auth.getPrincipal();
		} else {
			logger.error(
					"Principal is not of type SubscriberUserDetails. Actual type: {}",
					auth.getPrincipal().getClass().getName());
			logger.error("Token Principal: {}", auth.getPrincipal().toString());
		}
		return result;
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

	/**
	 * 
	 * @return
	 */
	public FullUserInfo getFullUserInfo() {
		AuditUser user = getCurrentAuditUser();
		if (user == null) {
			return null;
		}

		FullUserInfo result = fullUserInfoRepository.findOne(user.getId());
		return result == null ? null : new FullUserInfo(result);
	}

	public Long getCurrentUserId() {
		return getCurrentAuditUser().getId();
	}

	public boolean isSystem() {
		FullUserInfo info = getFullUserInfo();
		if (info == null) {
			return false;
		}
		return info.is_system();
	}
}
