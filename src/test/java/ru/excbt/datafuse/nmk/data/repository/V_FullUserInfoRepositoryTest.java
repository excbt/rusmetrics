package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.V_FullUserInfo;

public class V_FullUserInfoRepositoryTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(V_FullUserInfoRepositoryTest.class);

	@Autowired
	private V_FullUserInfoRepository fullUserInfoRepository;

	@Test
	public void testReporsitory() {
		List<V_FullUserInfo> lst = fullUserInfoRepository
				.findByUserName("test-argon19");
		assertTrue(lst.size() > 0);

		V_FullUserInfo result = new V_FullUserInfo(lst.get(0));

		assertNotNull(result.getUserName());
		logger.info("UserName: {}", result.getUserName());		
		assertNotNull(result.getSubscriber());
	}
}
