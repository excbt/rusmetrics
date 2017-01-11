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
 * @since 11.01.2017
 * 
 */
public class ElWidgetControllerTest extends AnyControllerTest {

	@Test
	public void testWidgetChartDataWeek() throws Exception {
		_testGetJson(String.format("/api/subscr/widgets/el/%d/chart/data/week", TestWidgetConstants.EL_ZPOINT_ID));
	}

}
