package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;

public class ContZPointServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContZPointServiceTest.class);

	private final static long MANUAL_CONT_OBJECT_ID = 60695605;

	@Autowired
	private ContZPointService contZPointService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateManualZPoint() throws Exception {
		ContZPoint contZPoint = contZPointService
				.createManualZPoint(MANUAL_CONT_OBJECT_ID,
						ContServiceTypeKey.HEAT, LocalDate.now());
		assertNotNull(contZPoint);
		logger.info("Created ZPoint:{}", contZPoint.getId());
		contZPointService.deleteManualZPoint(contZPoint.getId());
	}
}
