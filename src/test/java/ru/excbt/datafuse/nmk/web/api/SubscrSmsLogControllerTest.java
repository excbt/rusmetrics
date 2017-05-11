package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.SubscrSmsLog;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerSubscriberTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

@Transactional
public class SubscrSmsLogControllerTest extends AnyControllerSubscriberTest {

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSmsLog() throws Exception {
		RequestExtraInitializer params = (b) -> {
			LocalDatePeriod period = LocalDatePeriod.lastWeek();
			b.param("fromDate", period.getDateFromStr());
			b.param("toDate", period.getDateToStr());
		};

		String content = _testGetJson("/api/subscr/smsLog", params);
		List<SubscrSmsLog> result = TestUtils.fromJSON(new TypeReference<List<SubscrSmsLog>>() {
		}, content);

		assertNotNull(result);
	}

}
