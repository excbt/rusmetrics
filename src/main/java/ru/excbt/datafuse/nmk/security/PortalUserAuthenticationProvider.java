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
import ru.excbt.datafuse.nmk.data.service.SecuritySubscrUserService;
import ru.excbt.datafuse.nmk.data.service.SecuritySubscriberService;
import ru.excbt.datafuse.nmk.data.service.SubscrUserLoginLogService;
import ru.excbt.datafuse.nmk.data.service.SystemUserService;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;
import ru.excbt.datafuse.nmk.data.service.PasswordService;

/**
 * Компонент для авторизации
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.03.2015
 *
 */
@Component("userAuthenticationProvider")
public class PortalUserAuthenticationProvider implements AuthenticationProvider {

	private static final boolean USE_LDAP_PASSWORD = true;

	private static final Logger logger = LoggerFactory.getLogger(PortalUserAuthenticationProvider.class);

	@Autowired
	private SecuritySubscriberService subscriberService;

	@Autowired
	private SecuritySubscrUserService subscrUserService;

	@Autowired
	private SystemUserService systemUserService;

	@Autowired
	private PasswordService passwordService;

	@Autowired
	private LdapService ldapService;

	@Autowired
	private SubscrUserLoginLogService subscrUserLoginLogService;

	/**
	 *
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		logger.debug("UserAuthenticationProvider.authenticate");

		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		List<SubscrUser> subscrUsers = subscriberService.findUserByUsername(username);

		if (subscrUsers.size() > 1) {
			return null;
		}

		if (subscrUsers.size() == 0) {
			return checkSystemUser(authentication);
		}

		SubscrUser sUser = subscrUsers.get(0);

		if (Boolean.TRUE.equals(sUser.getIsBlocked())) {
			logger.warn("User {} is blocked", username);
			return null;
		}

		if (!doAuthenticate(username, password, sUser.getPassword())) {
			return null;
		}

		List<GrantedAuthority> grantedAuths = new ArrayList<>();

		List<SubscrRole> roles = subscrUserService.selectSubscrRoles(sUser.getId());

		for (SubscrRole sr : roles) {
			String roleName = sr.getRoleName();
			grantedAuths.add(new SimpleGrantedAuthority(roleName));
		}

		SubscriberUserDetails subscriberUserDetails = new SubscriberUserDetails(sUser, password, grantedAuths);

		subscrUserLoginLogService.registerLogin(subscriberUserDetails.getId(), subscriberUserDetails.getUsername());

		return buildAuthenticationToken(subscriberUserDetails, password, grantedAuths);
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

		List<GrantedAuthority> grantedAuths = AdminUtils.makeAuths(AuthoritiesConstants.adminAuthorities());

		SubscriberUserDetails subscriberUserDetails = new SubscriberUserDetails(sUser, password, grantedAuths);

		subscrUserLoginLogService.registerLogin(subscriberUserDetails.getId(), subscriberUserDetails.getUsername());

		return buildAuthenticationToken(subscriberUserDetails, password, grantedAuths);
	}

	/**
	 *
	 * @param subscriberUserDetails
	 * @param password
	 * @param grantedAuths
	 * @return
	 */
	private UsernamePasswordAuthenticationToken buildAuthenticationToken(SubscriberUserDetails subscriberUserDetails,
			Object password, Collection<? extends GrantedAuthority> grantedAuths) {

		logger.debug("Login {}: {} ", subscriberUserDetails.getIsSystem() ? "SystemUser" : "SubscrUser",
				subscriberUserDetails.getUsername());

		grantedAuths.forEach((i) -> {
			logger.debug("User {} Granted Authority:", subscriberUserDetails.getUsername(), i.getAuthority());
		});

		return new UsernamePasswordAuthenticationToken(subscriberUserDetails, password, grantedAuths);

	}

	/**
	 *
	 * @param username
	 * @param password
	 * @param actualPassword
	 * @return
	 */
	private boolean doAuthenticate(String username, String password, String actualPassword) {

		if (USE_LDAP_PASSWORD) {
			if (ldapService.doAuthentificate(username, password)) {
				return true;
			}
		} else {
			if (passwordService.passwordEncoder().matches(password, actualPassword)) {
				return true;
			}
		}

		return false;
	}

}
