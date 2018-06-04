package ru.excbt.datafuse.nmk.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.SystemUser;
import ru.excbt.datafuse.nmk.data.service.SecuritySubscrUserService;
import ru.excbt.datafuse.nmk.data.service.SecuritySubscriberService;
import ru.excbt.datafuse.nmk.data.service.SubscrUserLoginLogService;
import ru.excbt.datafuse.nmk.data.service.SystemUserService;

/**
 * Сервис для работы по протоколу SAML
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 05.08.2015
 *
 */
@Service
public class
SAMLSubscriberUserDetailsService implements SAMLUserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(SAMLSubscriberUserDetailsService.class);

	public static final String DUMMY_PASSWORD = "DUMMY_PASSWORD";

	@Autowired
	private SecuritySubscriberService subscriberService;

	@Autowired
	private SecuritySubscrUserService subscrUserService;

	@Autowired
	private SystemUserService systemUserService;

	@Autowired
	private SubscrUserLoginLogService subscrUserLoginLogService;

	@Override
	public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {

		String attrUID = credential.getAttributeAsString("uid");
		String attrUserId = credential.getAttributeAsString("userId");

		logger.info("attr {}: {}", "uid", attrUID == null ? "NULL" : attrUID);
		logger.info("attr {}: {}", "attrUserId", attrUserId == null ? "NULL" : attrUserId);

		String username = attrUID;
		logger.debug("Starting processing SubscriberUserDetails for {}", username);

		SubscriberUserDetails subscriberUserDetails = checkSubscrUser(username);

		if (subscriberUserDetails == null) {
			throw new UsernameNotFoundException(
					String.format("subscriberUserDetails : username %s is not found", username));
		} else {
			logger.debug("subscriberUserDetails : {}", subscriberUserDetails.toString());
		}

		if (subscriberUserDetails.isBlocked()) {
			throw new UsernameNotFoundException(String.format("Username %s is BLOCKED", username));
		}

		subscrUserLoginLogService.registerLogin(subscriberUserDetails.getId(), subscriberUserDetails.getUsername());

		return subscriberUserDetails;
	}

	/**
	 *
	 * @param username
	 * @return
	 */
	private SubscriberUserDetails checkSystemUser(String username) {
		SystemUser sUser = systemUserService.findByUsername(username);

		if (sUser == null) {
			return null;
		}
		Collection<GrantedAuthority> gas = AdminUtils.makeAuths(AuthoritiesConstants.adminAuthorities());

		SubscriberUserDetails subscriberUserDetails = new SubscriberUserDetails(sUser, DUMMY_PASSWORD, gas);
		return subscriberUserDetails;
	}

	/**
	 *
	 * @param username
	 * @return
	 */
	private SubscriberUserDetails checkSubscrUser(String username) {

		List<SubscrUser> subscrUsers = subscriberService.findUserByUsername(username);

		if (subscrUsers.size() > 1) {
			return null;
		}

		if (subscrUsers.size() == 0) {
			return checkSystemUser(username);
		}

		SubscrUser sUser = subscrUsers.get(0);

		List<GrantedAuthority> grantedAuths = new ArrayList<>();

		List<SubscrRole> roles = subscrUserService.selectSubscrRoles(sUser.getId());

		for (SubscrRole sr : roles) {
			String roleName = sr.getRoleName();
			grantedAuths.add(new SimpleGrantedAuthority(roleName));
		}

		SubscriberUserDetails subscriberUserDetails = new SubscriberUserDetails(sUser, DUMMY_PASSWORD, grantedAuths);

		return subscriberUserDetails;
	}

}
