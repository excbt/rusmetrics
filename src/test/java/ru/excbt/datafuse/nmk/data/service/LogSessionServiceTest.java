package ru.excbt.datafuse.nmk.data.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.model.vo.LogSessionVO;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
public class LogSessionServiceTest extends PortalDataTest {

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
    @Transactional
    @Ignore
	public void testLogSettionSelect() throws Exception {
		List<Long> ids = subscrDataSourceService.selectDataSourceIdsBySubscriber(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);

		logger.info("DataSourceIds: {}", ids.toString());

		List<LogSessionVO> logSessions = logSessionService.selectLogSessions(ids, LocalDatePeriod.lastWeek());
		assertFalse(logSessions.isEmpty());
	}

	/**
	 *
	 * @throws Exception
	 */
    @Ignore
	@Test
    @Transactional
	public void testLogSessionObjectsSelect() throws Exception {
		List<Long> ids = subscrDataSourceService.selectDataSourceIdsBySubscriber(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);
		List<LogSessionVO> logSessions = logSessionService.selectLogSessions(ids, LocalDatePeriod.lastWeek(),
				Arrays.asList(127858526L));
		assertFalse(logSessions.isEmpty());

	}

}
