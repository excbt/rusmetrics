package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

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
public class KeyStoreServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory
			.getLogger(KeyStoreServiceTest.class);

	private final static String DROOLS_HEARTBEAT= "drools_heartbeat";

	@Autowired
	private KeyStoreService keyStoreService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testPropValue() throws Exception {
		String propValue = keyStoreService.getKeyStoreProperty(
				DROOLS_HEARTBEAT, "drools_heartbeat_date");

		assertNotNull(propValue);
		logger.debug("propValue: {}", propValue);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testKeyValue() throws Exception {
		String propValue = keyStoreService.getKeyStoreValue(DROOLS_HEARTBEAT);
		assertNotNull(propValue);
		logger.debug("keyValue: {}", propValue);
	}
}
