package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaConfigTest;
import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;

import java.util.List;

import static org.junit.Assert.assertTrue;

@EnableAutoConfiguration(exclude = { RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class ContServiceDataHWaterServiceTest extends JpaConfigTest {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataHWaterServiceTest.class);

	@Autowired
	private ContServiceDataHWaterService contServiceDataHWaterService;

	@Test
	public void testLastDataDate() throws Exception {
		List<TimeDetailLastDate> result = contServiceDataHWaterService.selectTimeDetailLastDate(111551183);
		assertTrue(!result.isEmpty());
		result.forEach(i -> {
			logger.info("Date: {}", i.toString());

		});
	}
}
