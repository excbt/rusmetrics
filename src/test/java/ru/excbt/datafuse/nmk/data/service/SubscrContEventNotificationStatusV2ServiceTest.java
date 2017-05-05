package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.support.CityMonitorContEventsStatusV2;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class SubscrContEventNotificationStatusV2ServiceTest extends JpaSupportTest {

	@Autowired
	private SubscrContEventNotificationStatusV2Service subscrContEventNotificationStatusV2Service;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	@Test
	public void testStatus() throws Exception {

		SubscriberParam sp = SubscriberParam.builder().subscriberId(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID)
				.subscrUserId(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID).build();

		List<ContObject> contObjects = subscrContObjectService.selectSubscriberContObjects(sp);

		assertTrue(contObjects.size() > 0);

		LocalDatePeriod dp =

				LocalDatePeriod.builder().dateFrom("2016-07-01").dateTo(LocalDateTime.now()).build();

		List<CityMonitorContEventsStatusV2> result = subscrContEventNotificationStatusV2Service
				.selectCityMonitoryContEventsStatusV2(sp, contObjects, dp,
						false);

		assertTrue(result.size() > 0);
	}
}
