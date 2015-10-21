package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.types.TimezoneDefKey;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaSubscriberControllerTest extends RmaControllerTest {

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSubscribers() throws Exception {
		_testJsonGet(apiRmaUrl("/subscribers"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSubscriber() throws Exception {
		_testJsonGet(apiRmaUrl("/subscribers", 64166467));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSubcriberCRUD() throws Exception {
		Subscriber subscriber = new Subscriber();
		subscriber.setSubscriberName("TEST_SUBSCRIBER");
		subscriber.setOrganizationId(EXCBT_ORGANIZATION_ID);
		subscriber.setTimezoneDefKeyname(TimezoneDefKey.GMT_M3.getKeyname());

		Long subscriberId = _testJsonCreate(apiRmaUrl("/subscribers"), subscriber);

		subscriber = subscriberService.findOne(subscriberId);
		subscriber.setComment("Updated By REST");
		_testJsonUpdate(apiRmaUrl("/subscribers/", subscriberId), subscriber);

		RequestExtraInitializer param = (builder) -> {
			builder.param("isPermanent", "true");
		};

		_testJsonGet(apiRmaUrl("/subscribers/", subscriberId));

		_testJsonDelete(apiRmaUrl("/subscribers/", subscriberId), param);
	}
}
