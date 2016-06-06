package ru.excbt.datafuse.nmk.ldap.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.service.SubscrUserService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

public class LdapServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(LdapServiceTest.class);

	@Autowired
	private LdapService ldapService;

	@Autowired
	private LdapConfig ldapConfig;

	@Autowired
	private SubscrUserService subscrUserService;

	@Test
	public void testAuth() throws Exception {
		assertNotNull(ldapService);
		assertNotNull(ldapConfig);

		boolean authResult = ldapService.doAuthentificate("test_admin", "admin");
		assertTrue(authResult);

		boolean badAuthResult = ldapService.doAuthentificate("test_admin", "bad_password");
		assertFalse(badAuthResult);

		String dn = ldapService.getDnForUser("test_admin");
		logger.info("dn:{}", dn);

		ldapService.changePassword("test_admin", "admin", "admin1");

		authResult = ldapService.doAuthentificate("test_admin", "admin1");
		assertTrue(authResult);

		badAuthResult = ldapService.doAuthentificate("test_admin", "bad_password");
		assertFalse(badAuthResult);

		ldapService.changePassword("test_admin", "admin");

		authResult = ldapService.doAuthentificate("test_admin", "admin");
		assertTrue(authResult);

	}

	@Test
	@Ignore
	public void testChangeEmail() throws Exception {
		ldapService.updateEMail("RMA-Izhevsk", "west-snab");
		ldapService.updateEMail("RMA-Izhevsk", "vsegingeo");
		ldapService.updateEMail("RMA-Izhevsk", "okryabrskaya_45");
		ldapService.updateEMail("RMA-Izhevsk", "zhek");
		ldapService.updateEMail("RMA-Izhevsk", "kompribor");
		ldapService.updateEMail("RMA-Izhevsk", "aspek");
		ldapService.updateEMail("RMA-Izhevsk", "okeania");
		ldapService.updateEMail("RMA-Izhevsk", "ooo_kts");
		ldapService.updateEMail("RMA-Izhevsk", "turion");
		ldapService.updateEMail("RMA-Izhevsk", "votkinsk");
		ldapService.updateEMail("RMA-Izhevsk", "chekh");

		// String psw = "psw_12345";
		//
		// ldapService.changePassword("west-snab",psw);
		// ldapService.changePassword("vsegingeo",psw);
		// ldapService.changePassword("okryabrskaya_45",psw);
		// ldapService.changePassword("zhek",psw);
		// ldapService.changePassword("kompribor",psw);
		// ldapService.changePassword("aspek",psw);
		// ldapService.changePassword("okeania",psw);
		// ldapService.changePassword("ooo_kts",psw);
		// ldapService.changePassword("turion",psw);
		// ldapService.changePassword("votkinsk",psw);
		// ldapService.changePassword("chekh",psw);
	}

	@Ignore
	@Test
	public void testCreateUser() throws Exception {
		String username = "usr_" + System.currentTimeMillis();
		LdapUserAccount user = new LdapUserAccount(1L, username, new String[] { "user_firstName", "user_secondName" },
				new String[] { "EXCBT-NMK" }, null, null, null, null);
		ldapService.createUser(user);
		ldapService.blockLdapUser(user);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateCabinetsOU() throws Exception {
		SubscrUser subscrUser = subscrUserService.findOne(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID);

		String subscriberName = subscrUser.getSubscriber().getSubscriberName();
		Long subscriberid = subscrUser.getSubscriberId();
		String newOuName = "Cabinets-" + subscriberid;

		LdapUserAccount ldapUserAccount = subscrUserService.ldapAccountFactory(subscrUser,
				TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);
		ldapService.createOU(ldapUserAccount.getOrgUnits(), newOuName,
				"Объектные пользователи (кабинеты) для " + subscriberName);

	}

}
