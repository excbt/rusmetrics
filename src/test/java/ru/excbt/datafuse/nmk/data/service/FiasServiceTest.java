package ru.excbt.datafuse.nmk.data.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class FiasServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(FiasServiceTest.class);

	@Autowired
	private FiasService fiasService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testName() throws Exception {
		UUID uuid = UUID.fromString("7442ea7c-d310-4d47-a074-352d4b43140b");
		UUID result = fiasService.getCityUUID(uuid);
		assertNotNull(result);
		logger.info("City UUID: {}", result);

		String cityName = fiasService.getCityName(result);
		logger.info("City Name: {}", cityName);

	}

}
