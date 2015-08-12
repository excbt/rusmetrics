package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;

public class LdapServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(LdapServiceTest.class);

	@Autowired
	private LdapService ldapService;

	@Autowired
	private LdapConfig ldapConfig;

	@Test
	public void testAuth() throws Exception {
		assertNotNull(ldapService);
		assertNotNull(ldapConfig);

		boolean authResult = ldapService
				.doAuthentificate("test_admin", "admin");
		assertTrue(authResult);

		boolean badAuthResult = ldapService.doAuthentificate("test_admin",
				"bad_password");
		assertFalse(badAuthResult);

		String dn = ldapService.getDnForUser("test_admin");
		logger.info("dn:{}", dn);

		ldapService.changePassword("test_admin", "admin", "admin1");

		authResult = ldapService.doAuthentificate("test_admin", "admin1");
		assertTrue(authResult);

		badAuthResult = ldapService.doAuthentificate("test_admin",
				"bad_password");
		assertFalse(badAuthResult);

		ldapService.changePassword("test_admin", "admin");

		authResult = ldapService.doAuthentificate("test_admin", "admin");
		assertTrue(authResult);

	}
}
