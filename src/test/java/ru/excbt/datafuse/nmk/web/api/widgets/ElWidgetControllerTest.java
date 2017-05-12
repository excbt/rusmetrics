/**
 *
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import org.junit.Test;

import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 11.01.2017
 *
 */
@Transactional
public class ElWidgetControllerTest extends AnyControllerTest {

	@Test
	public void testWidgetChartDataWeek() throws Exception {
		_testGetJson(String.format("/api/subscr/widgets/el/%d/chart/data/week", TestWidgetConstants.EL_ZPOINT_ID));
	}

	@Test
	public void testWidgetChartDataToday() throws Exception {
		_testGetJson(String.format("/api/subscr/widgets/el/%d/chart/data/today", TestWidgetConstants.EL_ZPOINT_ID));
	}

	@Test
	public void testWidgetChartDataYesterday() throws Exception {
		_testGetJson(String.format("/api/subscr/widgets/el/%d/chart/data/yesterday", TestWidgetConstants.EL_ZPOINT_ID));
	}

}
