package ru.excbt.datafuse.nmk.data.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceTypeInfoART;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class ContObjectHWaterServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContObjectHWaterServiceTest.class);

	@Autowired
	private ContServiceDataHWaterDeltaService contObjectHWaterService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDeltaAgr() throws Exception {

		List<Object[]> result = contObjectHWaterService
				.selectRawHWaterDeltaAgr(728L,
						LocalDatePeriod.lastMonth(), ContServiceTypeKey.HW,
						TimeDetailKey.TYPE_1H, null);

		assertNotNull(result);
	}

	@Test
    @Transactional
	public void testDeltaInfo() throws Exception {

		Map<Long, ContServiceTypeInfoART> resultMap =

		contObjectHWaterService.selectHWaterDeltaART(728L,
				LocalDatePeriod.lastMonth(), ContServiceTypeKey.HW,
				TimeDetailKey.TYPE_1H);

		assertNotNull(resultMap);
		resultMap.forEach((k, v) -> {
			logger.info("ContObjectId:{}. ART: {}", k, v);
		});

	}

	@Test
    @Transactional
	public void testDeltaInfoByCity() throws Exception {
		Map<Long, ContServiceTypeInfoART> resultMap =

		contObjectHWaterService.selectHWaterDeltaART_ByCity(728L,
				LocalDatePeriod.lastMonth(), ContServiceTypeKey.HW,
				TimeDetailKey.TYPE_1H,
				UUID.fromString("deb1d05a-71ce-40d1-b726-6ba85d70d58f"));

		assertNotNull(resultMap);
		resultMap.forEach((k, v) -> {
			logger.info("ContObjectId:{}. ART: {}", k, v);
		});

	}
}
