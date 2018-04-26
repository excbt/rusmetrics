package ru.excbt.datafuse.nmk.data.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.support.TimeDetailLastDate;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class ContServiceDataHWaterServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(ContServiceDataHWaterServiceTest.class);

	@Autowired
	private ContServiceDataHWaterService contServiceDataHWaterService;

	@Test
    @Ignore
	public void testLastDataDate() throws Exception {
		List<TimeDetailLastDate> result = contServiceDataHWaterService.selectTimeDetailLastDate(111551183);
		assertTrue(!result.isEmpty());
		result.forEach(i -> {
			logger.info("Date: {}", i.toString());

		});
	}
}
