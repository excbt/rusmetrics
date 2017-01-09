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
 * @since dd.12.2016
 * 
 */
public class HwWidgetControllerTest extends AnyControllerTest {

	//	@Test
	//	public void testHeatWidgetChart() throws Exception {
	//		_testGetJson("/api/subscr/widgets/hw/71843421/chart/data/week");
	//	}

	@Test
	public void testHeatWidgetStatus() throws Exception {
		_testGetJson("/api/subscr/widgets/hw/71843421/status");
	}

}
