package ru.excbt.datafuse.nmk.data;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;

public class ContObjRepositoryTest extends RepositoryTest {

	
	private static final Logger logger = LoggerFactory
			.getLogger(ContObjRepositoryTest.class);
	
	@Autowired
	private ContObjectRepository contObjectRepository;

	
	@Test
	public void testContObject() {
		assertNotNull(contObjectRepository);
		
		List<ContObject> res = contObjectRepository.selectByUserName(725L);
		
		logger.info("Result count {}", res.size());
		logger.info("Full Address {}", res.get(0).getFullAddress());
	}
	
}
