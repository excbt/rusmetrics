package ru.excbt.datafuse.nmk.data.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.security.SubscriberUserDetails;

@Service
public class CurrentSubscriberUserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(CurrentSubscriberUserDetailsService.class);

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
			logger.error("Principal is not of type SubscriberUserDetails. Actual type: {}",
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
	public boolean isSystem() {

		SubscriberUserDetails userDetails = getCurrentUserDetails();

		if (userDetails == null) {
			return false;
		}
		return Boolean.TRUE.equals(userDetails.getIsSystem());
	}

	/**
	 * 
	 * @return
	 */
	public Authentication getAuthentication() {
		Authentication auth = getContextAuth();
		return auth;
	}

}