package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.support.ServiceTypeInfoART;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;

public class ContObjectHWaterServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContObjectHWaterServiceTest.class);

	@Autowired
	private ContObjectHWaterService contObjectHWaterService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDeltaAgr() throws Exception {

		List<Object[]> result = contObjectHWaterService
				.selectContObjectHWaterDeltaAgr(728L,
						LocalDatePeriod.lastMonth(), ContServiceTypeKey.HW,
						TimeDetailKey.TYPE_1H, null);

		assertNotNull(result);
	}

	@Test
	public void testDeltaInfo() throws Exception {

		Map<Long, ServiceTypeInfoART> resultMap =

		contObjectHWaterService.selectContObjectHWaterDeltaART(728L,
				LocalDatePeriod.lastMonth(), ContServiceTypeKey.HW,
				TimeDetailKey.TYPE_1H);

		assertNotNull(resultMap);
		resultMap.forEach((k, v) -> {
			logger.info("ContObjectId:{}. ART: {}", k, v);
		});

	}

}
