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
 * @since 10.01.2017
 *
 */
@Transactional
public class CwWidgetControllerTest extends AnyControllerTest {

	@Test
	public void testWidgetChartDataWeek() throws Exception {
		_testGetJson(String.format("/api/subscr/widgets/cw/%d/chart/data/week", TestWidgetConstants.CW_ZPOINT_ID));
	}

	@Test
	public void testWidgetChartDataToday() throws Exception {
		_testGetJson(String.format("/api/subscr/widgets/cw/%d/chart/data/today", TestWidgetConstants.CW_ZPOINT_ID));
	}

	@Test
	public void testWidgetChartDataYesterday() throws Exception {
		_testGetJson(String.format("/api/subscr/widgets/cw/%d/chart/data/yesterday", TestWidgetConstants.CW_ZPOINT_ID));
	}

}
