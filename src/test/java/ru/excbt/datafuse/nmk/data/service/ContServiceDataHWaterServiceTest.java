package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.data.model.support.ContServiceDataHWaterTotals;
import ru.excbt.datafuse.nmk.data.model.types.TimeDetailKey;

public class ContServiceDataHWaterServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ContServiceDataHWaterServiceTest.class);

	private final static long ZPOINT_ID = 18811557;

	@Autowired
	private ContServiceDataHWaterService service;

	@Test
	public void testSelectByZPoint() {
		List<?> resultList = service.selectByContZPoint(ZPOINT_ID,
				TimeDetailKey.TYPE_24H);
		assertTrue(resultList.size() > 0);
		logger.info("ZPoint (ID:{}) Found {} records", ZPOINT_ID,
				resultList.size());
	}

	@Test
	public void testSelectByZPointDated() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		LocalDateTime srcDate = formatter.parseLocalDateTime("2013-10-01");
		checkNotNull(srcDate);
		LocalDateTime beginDate = srcDate.dayOfMonth().withMinimumValue();
		LocalDateTime endDate = srcDate.dayOfMonth().withMaximumValue();

		List<?> resultList = service.selectByContZPoint(ZPOINT_ID,
				TimeDetailKey.TYPE_24H, beginDate, endDate);
		assertTrue(resultList.size() > 0);

		logger.info("ZPoint (ID:{}) Found {} records on period: [{}...{}]",
				ZPOINT_ID, resultList.size(), beginDate.toDate(),
				endDate.toDate());
	}

	@Test
	public void testLastData() {
		ContServiceDataHWater resultList = service.selectLastData(ZPOINT_ID);
		assertNotNull(resultList);
	}

	@Test
	public void testTotals() {
		ContServiceDataHWaterTotals result = service.selectContZPointTotals(
				ZPOINT_ID, TimeDetailKey.TYPE_1H,
				LocalDateTime.now().minusMonths(1).withDayOfMonth(1),
				LocalDateTime.now().withDayOfMonth(1).minusDays(1));
		assertNotNull(result);
		//assertNotNull(result.getM_in());
	}

}
