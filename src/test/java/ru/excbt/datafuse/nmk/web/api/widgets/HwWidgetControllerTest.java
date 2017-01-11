/**
 * 
 */
package ru.excbt.datafuse.nmk.web.api.widgets;

import org.junit.Test;

import ru.excbt.datafuse.nmk.web.AnyControllerTest;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 09.01.2017
 * 
 */
public class HwWidgetControllerTest extends AnyControllerTest {

	@Test
	public void testHwWidgetStatus() throws Exception {
		_testGetJson(String.format("/api/subscr/widgets/hw/%d/status", TestWidgetConstants.HW_ZPOINT_ID));
	}

	@Test
	public void testHwWidgetChartDataWeek() throws Exception {
		_testGetJson(String.format("/api/subscr/widgets/hw/%d/chart/data/week", TestWidgetConstants.HW_ZPOINT_ID));
	}

	@Test
	public void testHwWidgetChartDataYesterday() throws Exception {
		_testGetJson(String.format("/api/subscr/widgets/hw/%d/chart/data/yesterday", TestWidgetConstants.HW_ZPOINT_ID));
	}

	@Test
	public void testHwWidgetChartDataToday() throws Exception {
		_testGetJson(String.format("/api/subscr/widgets/hw/%d/chart/data/today", TestWidgetConstants.HW_ZPOINT_ID));
	}

}
