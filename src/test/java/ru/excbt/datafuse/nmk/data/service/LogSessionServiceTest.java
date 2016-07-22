package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.vo.LogSessionVO;

public class LogSessionServiceTest extends JpaSupportTest {

	private static final Logger logger = LoggerFactory.getLogger(LogSessionServiceTest.class);

	@Autowired
	private LogSessionService logSessionService;

	@Autowired
	private SubscrDataSourceService subscrDataSourceService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLogSettionSelect() throws Exception {
		List<Long> ids = subscrDataSourceService.selectDataSourceIdsBySubscriber(EXCBT_RMA_SUBSCRIBER_ID);

		logger.info("DataSourceIds: {}", ids.toString());

		List<LogSessionVO> logSessions = logSessionService.selectLogSessions(ids, LocalDatePeriod.lastWeek());
		assertFalse(logSessions.isEmpty());
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLogSessionObjectsSelect() throws Exception {
		List<Long> ids = subscrDataSourceService.selectDataSourceIdsBySubscriber(EXCBT_RMA_SUBSCRIBER_ID);
		List<LogSessionVO> logSessions = logSessionService.selectLogSessions(ids, LocalDatePeriod.lastWeek(),
				Arrays.asList(127858526L));
		assertFalse(logSessions.isEmpty());

	}

}
