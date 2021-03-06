package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;

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
import ru.excbt.datafuse.nmk.data.model.ContObject;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class ContObjectRepositoryTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContObjectRepositoryTest.class);

	@Autowired
	private ContObjectRepository contObjectRepository;

	@Test
	public void testContObject() {
		assertNotNull(contObjectRepository);

		ContObject res = contObjectRepository.findOne(725L);

		assertNotNull(res);

		logger.info("Full Address {}", res.getFullAddress());
	}

}
