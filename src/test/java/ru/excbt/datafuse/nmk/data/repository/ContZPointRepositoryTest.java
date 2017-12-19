package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class ContZPointRepositoryTest extends JpaSupportTest {

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
