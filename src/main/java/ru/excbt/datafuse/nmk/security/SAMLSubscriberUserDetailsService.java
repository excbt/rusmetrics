package ru.excbt.datafuse.nmk.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opensaml.saml2.core.NameIDType;
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
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.service.SystemUserService;

@Service
public class SAMLSubscriberUserDetailsService implements SAMLUserDetailsService {

	private static final Logger logger = LoggerFactory
			.getLogger(SAMLSubscriberUserDetailsService.class);

	public static final String DUMMY_PASSWORD = "DUMMY_PASSWORD";

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private SubscrUserService subscrUserService;

	@Autowired
	private SystemUserService systemUserService;

	@Override
	public Object loadUserBySAML(SAMLCredential credential)
			throws UsernameNotFoundException {

		String username = credential.getNameID().getValue();
		logger.debug("Starting processing SubscriberUserDetails for {}",
				username);

		logger.info("attr {}: {}", NameIDType.NAME_QUALIFIER_ATTRIB_NAME,
				credential.getAttribute(NameIDType.NAME_QUALIFIER_ATTRIB_NAME));

		SubscriberUserDetails subscriberUserDetails = checkSubscrUser(username);

		if (subscriberUserDetails == null) {
			logger.error("subscriberUserDetails : INVALID");
		} else {
			logger.debug("subscriberUserDetails : {}",
					subscriberUserDetails.toString());
		}

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
		Collection<GrantedAuthority> gas = AdminUtils.makeAdminAuths();

		SubscriberUserDetails subscriberUserDetails = new SubscriberUserDetails(
				sUser, DUMMY_PASSWORD, gas);
		return subscriberUserDetails;
	}

	/**
	 * 
	 * @param username
	 * @return
	 */
	private SubscriberUserDetails checkSubscrUser(String username) {

		List<SubscrUser> subscrUsers = subscriberService
				.findUserByUsername(username);

		if (subscrUsers.size() > 1) {
			return null;
		}

		if (subscrUsers.size() == 0) {
			return checkSystemUser(username);
		}

		SubscrUser sUser = subscrUsers.get(0);

		List<GrantedAuthority> grantedAuths = new ArrayList<>();

		List<SubscrRole> roles = subscrUserService.selectSubscrRoles(sUser
				.getId());

		for (SubscrRole sr : roles) {
			String roleName = sr.getRoleName();
			grantedAuths.add(new SimpleGrantedAuthority(roleName));
		}

		SubscriberUserDetails subscriberUserDetails = new SubscriberUserDetails(
				sUser, DUMMY_PASSWORD, grantedAuths);

		return subscriberUserDetails;
	}

}
