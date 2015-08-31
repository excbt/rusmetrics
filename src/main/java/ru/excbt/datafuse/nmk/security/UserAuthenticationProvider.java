package ru.excbt.datafuse.nmk.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.SystemUser;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.SystemUserService;
import ru.excbt.datafuse.nmk.data.service.support.PasswordService;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;

@Component("userAuthenticationProvider")
public class UserAuthenticationProvider implements AuthenticationProvider {

	private static final boolean USE_LDAP_PASSWORD = true;

	private static final Logger logger = LoggerFactory
			.getLogger(UserAuthenticationProvider.class);

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private SubscrUserService subscrUserService;

	@Autowired
	private SystemUserService systemUserService;

	@Autowired
	private PasswordService passwordService;

	@Autowired
	private LdapService ldapService;

	/**
	 * 
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		logger.info("UserAuthenticationProvider.authenticate");

		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		List<SubscrUser> subscrUsers = subscriberService
				.findUserByUsername(username);

		if (subscrUsers.size() > 1) {
			return null;
		}

		if (subscrUsers.size() == 0) {
			return checkSystemUser(authentication);
		}

		SubscrUser sUser = subscrUsers.get(0);

		if (!doAuthenticate(username, password, sUser.getPassword())) {
			return null;
		}

		List<GrantedAuthority> grantedAuths = new ArrayList<>();

		List<SubscrRole> roles = subscrUserService.selectSubscrRoles(sUser
				.getId());

		for (SubscrRole sr : roles) {
			String roleName = sr.getRoleName();
			grantedAuths.add(new SimpleGrantedAuthority(roleName));
		}

		SubscriberUserDetails subscriberUserDetails = new SubscriberUserDetails(
				sUser, password, grantedAuths);

		return buildAuthenticationToken(subscriberUserDetails, password,
				grantedAuths);
	}

	/**
	 * 
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	/**
	 * 
	 * @param authentication
	 * @return
	 */
	private Authentication checkSystemUser(Authentication authentication) {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		SystemUser sUser = systemUserService.findByUsername(username);
		if (sUser == null) {
			return null;
		}

		if (!doAuthenticate(username, password, sUser.getPassword())) {
			return null;
		}

		List<GrantedAuthority> grantedAuths = AdminUtils.makeAdminAuths();

		SubscriberUserDetails subscriberUserDetails = new SubscriberUserDetails(
				sUser, password, grantedAuths);

		return buildAuthenticationToken(subscriberUserDetails, password,
				grantedAuths);
	}

	/**
	 * 
	 * @param subscriberUserDetails
	 * @param password
	 * @param grantedAuths
	 * @return
	 */
	private UsernamePasswordAuthenticationToken buildAuthenticationToken(
			SubscriberUserDetails subscriberUserDetails, Object password,
			Collection<? extends GrantedAuthority> grantedAuths) {

		logger.info(
				"Login {}: {} ",
				subscriberUserDetails.getIsSystem() ? "SystemUser" : "SubscrUser",
				subscriberUserDetails.getUsername());

		grantedAuths.forEach((i) -> {
			logger.debug("User {} Granted Authority:",
					subscriberUserDetails.getUsername(), i.getAuthority());
		});

		return new UsernamePasswordAuthenticationToken(subscriberUserDetails,
				password, grantedAuths);

	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @param actualPassword
	 * @return
	 */
	private boolean doAuthenticate(String username, String password,
			String actualPassword) {

		if (USE_LDAP_PASSWORD) {
			if (ldapService.doAuthentificate(username, password)) {
				return true;
			}
		} else {
			if (passwordService.passwordEncoder().matches(password,
					actualPassword)) {
				return true;
			}
		}

		return false;
	}

}
