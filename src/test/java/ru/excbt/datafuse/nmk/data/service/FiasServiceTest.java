package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

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
