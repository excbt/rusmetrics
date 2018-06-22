package ru.excbt.datafuse.nmk.data.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class ContObjectRepositoryTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContObjectRepositoryTest.class);

	@Autowired
	private ContObjectRepository contObjectRepository;

	@Test
	public void testContObject() {
		assertNotNull(contObjectRepository);

		Optional<ContObject> res = contObjectRepository.findById(725L);

		assertTrue(res.isPresent());

		logger.info("Full Address {}", res.get().getFullAddress());
	}

}
