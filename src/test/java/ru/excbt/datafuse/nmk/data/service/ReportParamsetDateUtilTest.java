package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.report.ReportPeriodKey;

import java.time.LocalDateTime;


public class ReportParamsetDateUtilTest {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportParamsetDateUtilTest.class);

	private LocalDateTime[] testKey(ReportPeriodKey reportPeriodKey) {
		LocalDateTime[] result = new LocalDateTime[2];
		LocalDateTime dtStart = ReportParamsetDateUtil.getStartDateTime(
				LocalDateTime.now(), reportPeriodKey);
		LocalDateTime dtEnd = ReportParamsetDateUtil.getEndDateTime(
				LocalDateTime.now(), reportPeriodKey);

		assertNotNull(dtStart);
		assertNotNull(dtEnd);
		logger.info("testing :{}", reportPeriodKey);
		logger.info("dtStart:{}", dtStart);
		logger.info("dtEnd:{}", dtEnd);

		result[0] = dtStart;
		result[1] = dtEnd;

		return result;
	}

	@Test
	public void testToday() {
		LocalDateTime[] res = testKey(ReportPeriodKey.TODAY);
		assertNotNull(res);
	}

	@Test
	public void testYesterday() {
		LocalDateTime[] res = testKey(ReportPeriodKey.YESTERDAY);
		assertNotNull(res);
	}

	@Test
	public void testCurrentMonth() {
		LocalDateTime[] res = testKey(ReportPeriodKey.CURRENT_MONTH);
		assertNotNull(res);
	}

	@Test
	public void testLastMonth() {
		LocalDateTime[] res = testKey(ReportPeriodKey.LAST_MONTH);
		assertNotNull(res);
	}

}
