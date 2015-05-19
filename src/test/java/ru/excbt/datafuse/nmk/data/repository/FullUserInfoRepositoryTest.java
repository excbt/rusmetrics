package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.FullUserInfo;

public class FullUserInfoRepositoryTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(FullUserInfoRepositoryTest.class);

	@Autowired
	private FullUserInfoRepository fullUserInfoRepository;

	@Test
	public void testReporsitory() {
		List<FullUserInfo> lst = fullUserInfoRepository
				.findByUserName("test-argon19");
		assertTrue(lst.size() > 0);

		FullUserInfo result = new FullUserInfo(lst.get(0));

		assertNotNull(result.getUserName());
		logger.info("UserName: {}", result.getUserName());		
		assertNotNull(result.getSubscriber());
	}
}
