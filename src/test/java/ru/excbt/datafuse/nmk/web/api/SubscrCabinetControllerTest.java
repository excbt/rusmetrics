package ru.excbt.datafuse.nmk.web.api;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrCabinetControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetControllerTest.class);

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @return
	 */
	@Override
	protected long getSubscriberId() {
		return 512156297L;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	protected long getSubscrUserId() {
		return 512156325L;
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testContObjectInfoList() throws Exception {
		_testGetJson("/api/subscr/subscrCabinet/contObjectCabinetInfo");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateCabinetSubscribers() throws Exception {
		List<Long> contObjectIds = Arrays.asList(29863789L, 29863938L, 29863933L);

		_testUpdateJson("/api/subscr/subscrCabinet/create", contObjectIds);
		/**
		 * 29863789
		 * 29863938
		 * 29863933
		 * 29863488
		 * 29863487
		 * 29863944
		 * 66948436
		 * 29863950
		 * 29863957
		 * 29863952
		 * 29863953
		 * 29863949
		 * 29863965
		 * 29863964
		 * 29863934
		 * 29863503
		 * 29863502
		 */

	}

	@Test
	public void testDeleteCabinetSubscriber() throws Exception {

		List<Subscriber> subscribers = subscriberService.selectChildSubscribers(getSubscriberId());

		List<Long> childSubscriberIds = subscribers.stream().map(i -> i.getId()).collect(Collectors.toList());
		_testUpdateJson("/api/subscr/subscrCabinet/delete", childSubscriberIds);
	}
}
