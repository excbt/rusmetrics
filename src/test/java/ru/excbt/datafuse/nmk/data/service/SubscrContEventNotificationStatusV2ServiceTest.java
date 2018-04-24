package ru.excbt.datafuse.nmk.data.service;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.data.model.support.CityMonitorContEventsStatusV2;
import ru.excbt.datafuse.nmk.data.model.support.LocalDatePeriod;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class SubscrContEventNotificationStatusV2ServiceTest extends PortalDataTest {

	@Autowired
	private SubscrContEventNotificationStatusV2Service subscrContEventNotificationStatusV2Service;

    @Autowired
	private ObjectAccessService objectAccessService;

	@Test
	public void testStatus() throws Exception {

		SubscriberParam sp = SubscriberParam.builder().subscriberId(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID)
				.subscrUserId(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_USER_ID).build();

		List<ContObject> contObjects = objectAccessService.findContObjects(sp.getSubscriberId());

		assertTrue(contObjects.size() > 0);

		LocalDatePeriod dp =

				LocalDatePeriod.builder().dateFrom("2016-07-01").dateTo(LocalDateTime.now()).build();

		List<CityMonitorContEventsStatusV2> result = subscrContEventNotificationStatusV2Service
				.selectCityMonitoryContEventsStatusV2(sp, contObjects, dp,
						false);

		assertTrue(result.size() > 0);
	}
}
