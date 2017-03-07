package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import ru.excbt.datafuse.nmk.data.model.LogSession;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

/**
 *
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 02.06.2016
 *
 */
public class RmaSubscrLogSessionControllerTest extends RmaControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscrLogSessionControllerTest.class);

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testLogSession() throws Exception {
		RequestExtraInitializer params = (b) -> {
			LocalDatePeriod period = LocalDatePeriod.lastWeek();
			b.param("fromDate", period.getDateFromStr());
			b.param("toDate", period.getDateToStr());
		};

		String content = _testGetJson("/api/rma/logSessions", params);
		List<LogSession> result = TestUtils.fromJSON(new TypeReference<List<LogSession>>() {
		}, content);

		assertNotNull(result);

		assertFalse(result.isEmpty());

		for (LogSession logSession : result) {
			_testGetJson(String.format("/api/rma/logSessions/%d/steps", logSession.getId()));
		}

	}

	@Test
	public void testLogSessionObject() throws Exception {

		//127858526

		RequestExtraInitializer params = (b) -> {
			LocalDatePeriod period = LocalDatePeriod.lastWeek();
			b.param("fromDate", period.getDateFromStr());
			b.param("toDate", period.getDateToStr());
			b.param("contObjectIds", TestUtils.listToString(Arrays.asList(127858526L)));
		};

		String content = _testGetJson("/api/rma/logSessions", params);
		List<LogSession> result = TestUtils.fromJSON(new TypeReference<List<LogSession>>() {
		}, content);

		assertNotNull(result);

		assertFalse(result.isEmpty());

	}

}
