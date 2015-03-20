package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;

public class ContObjectRepositoryTest extends JpaSupportTest {

	
	private static final Logger logger = LoggerFactory
			.getLogger(ContObjectRepositoryTest.class);
	
	@Autowired
	private ContObjectRepository contObjectRepository;

	
	@Test
	public void testContObject() {
		assertNotNull(contObjectRepository);
		
		List<ContObject> res = contObjectRepository.selectByUserName(725L);
		
		logger.info("Result count {}", res.size());
		logger.info("Full Address {}", res.get(0).getFullAddress());
	}

	@Test
	public void testFindContObjects() {
		List<ContObject> contObjects = contObjectRepository.selectSubscrContObjects(729);
		assertTrue(contObjects.size() > 0);
		
		logger.debug("find {} contObjects", contObjects.size());
		
		for (ContObject co : contObjects) {
			logger.debug("contObject fullAddress: {}", co.getFullAddress());
		}
		
	}	
	
}
