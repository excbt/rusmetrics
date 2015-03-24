package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;

public class SubscriberServiceTest extends JpaSupportTest {

	private final static String ARGON_19 = "test-argon19";
	private final static String SIMPLE_PASSWORD = "12345";

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private PasswordService passwordService;

	@Test
	public void tesArgon19() {
		List<SubscrUser> subscrUsers = subscriberService
				.findUserByUsername(ARGON_19);
		assertTrue(subscrUsers.size() == 1);

		SubscrUser user = subscrUsers.get(0);

		assertEquals(passwordService.passwordEncoder().encode(SIMPLE_PASSWORD),
				user.getPassword());

		List<SubscrRole> subscrOrgs = subscriberService.selectSubscrRoles(user
				.getId());
		assertTrue(subscrOrgs.size() == 1);
		// subscrOrgs.get(0)

	}

}
