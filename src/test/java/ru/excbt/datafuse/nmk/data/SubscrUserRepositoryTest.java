package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;

public class SubscrUserRepositoryTest extends RepositoryTest {

	
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
		logger.debug("subscrUser roles {}", su.getSubscrOrgs().size());

	}
	


}
