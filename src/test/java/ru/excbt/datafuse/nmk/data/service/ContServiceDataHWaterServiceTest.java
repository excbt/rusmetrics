package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;

public class ContServiceDataHWaterServiceTest extends JpaSupportTest {

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
