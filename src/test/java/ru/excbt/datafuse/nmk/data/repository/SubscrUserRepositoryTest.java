package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;

public class SubscrUserRepositoryTest extends JpaSupportTest {

	
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
