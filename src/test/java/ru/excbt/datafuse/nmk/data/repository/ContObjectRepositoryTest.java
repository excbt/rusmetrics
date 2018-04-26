package ru.excbt.datafuse.nmk.data.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class ContObjectRepositoryTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContObjectRepositoryTest.class);

	@Autowired
	private ContObjectRepository contObjectRepository;

	@Test
	public void testContObject() {
		assertNotNull(contObjectRepository);

		ContObject res = contObjectRepository.findOne(725L);

		assertNotNull(res);

		logger.info("Full Address {}", res.getFullAddress());
	}

}
