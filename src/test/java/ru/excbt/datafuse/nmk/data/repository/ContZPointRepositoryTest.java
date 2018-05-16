package ru.excbt.datafuse.nmk.data.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class ContZPointRepositoryTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(ContZPointRepositoryTest.class);

	@Autowired
	private ContZPointRepository contZPointRepository;

	@Test
	public void testZPoint() {
		List<ContZPoint> zpList = contZPointRepository.findByContObjectId(725L);
		assertNotNull(zpList);

		assertTrue(zpList.size() > 0);

		logger.debug("Found {} zpoints", zpList.size());

		ContZPoint zp = zpList.get(0);


		logger.debug("deviceObject number {}", zp.getDeviceObject().getNumber());

	}

}
