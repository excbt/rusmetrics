package ru.excbt.datafuse.nmk.data.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class SubscrUserRepositoryTest extends PortalDataTest {


	private static final Logger logger = LoggerFactory
			.getLogger(SubscrUserRepositoryTest.class);

	@Autowired
	private SubscrUserRepository subscrUserRepository;


	@Test
	public void testFindUserName() {
		List<SubscrUser> suList = subscrUserRepository.findByUserNameIgnoreCase("s_admin");
		assertNotNull(suList);

		SubscrUser su = suList.get(0);

		logger.debug("Find subscrUser {}", su.getUserName());
		logger.debug("subscrUser roles {}", su.getSubscrRoles().size());

	}



}
