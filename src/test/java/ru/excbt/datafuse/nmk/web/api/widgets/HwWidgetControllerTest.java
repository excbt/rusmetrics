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

	//	@Test
	//	public void testHeatWidgetChart() throws Exception {
	//		_testGetJson("/api/subscr/widgets/hw/71843421/chart/data/week");
	//	}

	@Test
	public void testHwWidgetStatus() throws Exception {
		_testGetJson("/api/subscr/widgets/hw/71843421/status");
	}

	@Test
	public void testHwWidgetChartDataWeek() throws Exception {
		_testGetJson("/api/subscr/widgets/hw/71843421/chart/data/week");
	}

	@Test
	public void testHwWidgetChartDataYesterday() throws Exception {
		_testGetJson("/api/subscr/widgets/hw/71843421/chart/data/yesterday");
	}

	@Test
	public void testHwWidgetChartDataToday() throws Exception {
		_testGetJson("/api/subscr/widgets/hw/71843421/chart/data/today");
	}

}
