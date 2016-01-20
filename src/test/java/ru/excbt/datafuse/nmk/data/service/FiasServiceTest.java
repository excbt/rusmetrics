package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;

public class FiasServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(FiasServiceTest.class);

	@Autowired
	private FiasService fiasService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testName() throws Exception {
		UUID uuid = UUID.fromString("7442ea7c-d310-4d47-a074-352d4b43140b");
		UUID result = fiasService.getCityUUID(uuid);
		assertNotNull(result);
		logger.info("City UUID: {}", result);

		String cityName = fiasService.getCityName(result);
		logger.info("City Name: {}", cityName);

	}

}
