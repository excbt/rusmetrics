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
 * @since 27.12.2016
 * 
 */
public class HeatWidgetControllerTest extends AnyControllerTest {

	/*
	 * test query
	
	SELECT 	cont_zpoint_id,
	   	time_detail_type,
	   	b_date,
	   	e_date,
		data_date,
		t_in,
		t_out,
		chart_t_in,
		chart_t_out,
		t_ambience
	FROM widgets.get_heat_data(107365375, '2016-03-07 23:59:59', 'WEEK');  
	 */

	/**
	 * YESTERDAY, TODAY, WEEK
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHeatWidgetChart() throws Exception {

		_testGetJson("/api/subscr/widgets/heat/107365375/chart/data/week");
	}

	@Test
	public void testHeatWidgetMonitor() throws Exception {

		_testGetJson("/api/subscr/widgets/heat/107365375/status");
	}

}
