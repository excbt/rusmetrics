package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class SubscriberServiceTest extends JpaSupportTest implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscriberServiceTest.class);

	private final static String ARGON_19 = "test-argon19";
	private final static String SIMPLE_PASSWORD = "12345";

	private final static long RMA_SUBSCRIBER = 103926764L;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private SubscrUserService subscrUserService;

	@Autowired
	private PasswordService passwordService;

	@Autowired
	private CurrentSubscriberService currentSubscriberService;

	@Autowired
	protected SubscrRoleService subscrRoleService;

	@Autowired
	private ReportParamsetService reportParamsetService;

	@Ignore
	@Test
	public void tesArgon19() {
		Optional<SubscrUser> userOptional = subscrUserService.findByUsername("ARGON_19");
		assertTrue(userOptional.isPresent());

		SubscrUser user = userOptional.get();

		assertEquals(passwordService.passwordEncoder().encode(SIMPLE_PASSWORD), user.getPassword());

		List<SubscrRole> subscrRoles = subscrUserService.selectSubscrRoles(user.getId());
		assertTrue(subscrRoles.size() == 2);

		// subscrOrgs.get(0)

	}

	@Test
	public void testRmaOu() throws Exception {
		Long id = currentSubscriberService.getSubscriberId();
		logger.info("Check Ldap OU for: {}", id);
		String ldapOu = subscriberService.getRmaLdapOu(id);
		assertNotNull(ldapOu);
		logger.info("Rma LDAP ou={}", ldapOu);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testCreateRmaUser() throws Exception {

		SubscrUser subscrUser = new SubscrUser();

		boolean isAdmin = true;
		boolean isRma = true;
		boolean isReadonly = false;

		subscrUser.setUserName("rma-77-admin");
		subscrUser.setUserNickname("rma-77-admin");
		subscrUser.setSubscriberId(RMA_SUBSCRIBER);
		subscrUser.getSubscrRoles().clear();
		subscrUser.setIsAdmin(isAdmin);
		subscrUser.setIsReadonly(false);

		if (Boolean.TRUE.equals(isReadonly)) {
			subscrUser.getSubscrRoles().addAll(subscrRoleService.subscrReadonlyRoles());
			subscrUser.setIsAdmin(false);
		} else {
			if (Boolean.TRUE.equals(isAdmin)) {
				subscrUser.getSubscrRoles().addAll(subscrRoleService.subscrAdminRoles(true));
				if (Boolean.TRUE.equals(isRma)) {
					subscrUser.getSubscrRoles().addAll(subscrRoleService.subscrRmaAdminRoles());
				}
			} else {
				subscrUser.getSubscrRoles().addAll(subscrRoleService.subscrUserRoles());
			}
		}

		subscrUserService.createSubscrUser(subscrUser, "exbt_123456", true);

	}

	@Ignore
	@Test
	public void testInitDefaultReportParamsets() throws Exception {
		Subscriber subscriber = subscriberService.selectSubscriber(RMA_SUBSCRIBER);
		reportParamsetService.createDefaultReportParamsets(subscriber);
	}

}
