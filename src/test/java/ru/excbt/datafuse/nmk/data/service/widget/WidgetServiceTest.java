/**
 * 
 */
package ru.excbt.datafuse.nmk.data.service.widget;

import static org.junit.Assert.assertNotNull;

import java.time.ZonedDateTime;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.excbt.datafuse.nmk.utils.DateInterval;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.01.2017
 * 
 */
public class WidgetServiceTest {

	private static final Logger log = LoggerFactory.getLogger(WidgetServiceTest.class);

	@Test
	public void testModeDate() throws Exception {
		DateInterval dateInterval = WidgetService.calculateModeDateInterval(ZonedDateTime.now(), "MONTH");
		assertNotNull(dateInterval);
		log.debug("DateInterval for MONTH: fromDate = {}, toDate {}", dateInterval.getFromDateStr(),
				dateInterval.getToDateStr());
	}

}
