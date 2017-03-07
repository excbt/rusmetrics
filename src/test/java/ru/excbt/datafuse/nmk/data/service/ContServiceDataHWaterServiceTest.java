package ru.excbt.datafuse.nmk.data.service;

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
import ru.excbt.datafuse.nmk.config.jpa.JpaConfigTest;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;

import javax.transaction.Transactional;

@EnableAutoConfiguration(exclude = { RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
public class ContServiceDataHWaterServiceTest extends JpaConfigTest {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataHWaterServiceTest.class);

	@Autowired
	private ContServiceDataHWaterService contServiceDataHWaterService;

	@Transactional
	@Test
	public void testLastDataDate() throws Exception {
		List<TimeDetailLastDate> result = contServiceDataHWaterService.selectTimeDetailLastDate(111551183);
		assertTrue(!result.isEmpty());
		result.forEach(i -> {
			logger.info("Date: {}", i.toString());

		});
	}
}
