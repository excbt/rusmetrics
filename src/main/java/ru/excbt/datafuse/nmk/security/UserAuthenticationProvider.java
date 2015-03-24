package ru.excbt.datafuse.nmk.security;

import java.util.ArrayList;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.SystemUser;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.SystemUserService;

public class UserAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory
			.getLogger(UserAuthenticationProvider.class);

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private SystemUserService systemUserService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * 
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		String name = authentication.getName();
		String password = authentication.getCredentials().toString();
		List<SubscrUser> subscrUsers = subscriberService
				.findUserByUsername(name);

		if (subscrUsers.size() == 0) {
			return checkSystemUser(authentication);
		}

		if (subscrUsers.size() > 1) {
			return null;
		}

		SubscrUser sUser = subscrUsers.get(0);

		if (!passwordEncoder.matches(password, sUser.getPassword())) {
			return null;
		}

		List<GrantedAuthority> grantedAuths = new ArrayList<>();

		List<SubscrRole> roles = subscriberService.selectSubscrRoles(sUser
				.getId());

		for (SubscrRole sr : roles) {
			String roleName = sr.getName();
			grantedAuths.add(new SimpleGrantedAuthority(roleName));
		}

		Authentication auth = new UsernamePasswordAuthenticationToken(name,
				password, grantedAuths);
		return auth;

	}

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
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

		SystemUser systemUser = systemUserService.findByUsername(name);
		if (systemUser == null) {
			return null;
		}

		if (!passwordEncoder.matches(password, systemUser.getPassword())) {
			return null;
		}

		List<GrantedAuthority> grantedAuths = new ArrayList<>();
		grantedAuths.add(new SimpleGrantedAuthority(SecuredRoles.ROLE_ADMIN));
		grantedAuths.add(new SimpleGrantedAuthority(
				SecuredRoles.SUBSCR_ROLE_ADMIN));

		Authentication auth = new UsernamePasswordAuthenticationToken(name,
				password, grantedAuths);
		return auth;

	}

}
