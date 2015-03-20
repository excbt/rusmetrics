package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;

public class ContZPointRepositoryTest extends JpaSupportTest {

	
	private static final Logger logger = LoggerFactory
			.getLogger(ContZPointRepositoryTest.class);
	
	@Autowired
	private ContZPointRepository contZPointRepository;

	@Test
	public void testZPoint() {
		List<ContZPoint> zpList = contZPointRepository.findByContObjectId(725);
		assertNotNull(zpList);
		
		assertTrue(zpList.size() > 0);
		
		logger.debug("Found {} zpoints", zpList.size());
		
		ContZPoint zp = zpList.get(0);
		
		logger.debug("Found {} deviceObjects", zp.getDeviceObjects().size());
		
		for (DeviceObject d : zp.getDeviceObjects()) {
			logger.debug("deviceObject number {}", d.getNumber());
		}
		
	}

}
