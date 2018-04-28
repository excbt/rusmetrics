package ru.excbt.datafuse.nmk.data.repository;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.V_FullUserInfo;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class V_FullUserInfoRepositoryTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory
			.getLogger(V_FullUserInfoRepositoryTest.class);

	@Autowired
	private V_FullUserInfoRepository fullUserInfoRepository;

	@Test
    @Ignore
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
