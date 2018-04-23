/**
 *
 */
package ru.excbt.datafuse.nmk.data.service.widget;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.widget.HeatWidgetTemperatureDto;

/**
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.12.2016
 *
 */
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class HeatWidgetServiceTest extends JpaSupportTest {

	private static final Logger log = LoggerFactory.getLogger(HeatWidgetServiceTest.class);

	@Autowired
	private HeatWidgetService heatWidgetService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testGetWidgetData() throws Exception {

		List<HeatWidgetTemperatureDto> result = heatWidgetService.selectChartData(107365375L,
				ZonedDateTime.of(LocalDate.of(2016, 03, 07), LocalTime.now(), ZoneId.systemDefault()), "WEEK");
		result.forEach(i -> log.info(i.toString()));
	}



}
