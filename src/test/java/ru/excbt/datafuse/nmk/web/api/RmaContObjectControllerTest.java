package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.RmaSubscriberService;
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

public class RmaContObjectControllerTest extends RmaControllerTest {

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private RmaSubscriberService rmaSubscriberService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	private Long testSubscriberId;

	/**
	 * 
	 */
	@Before
	public void initTestSubscriberId() {
		List<Subscriber> subscribers = rmaSubscriberService.selectRmaSubscribers(EXCBT_RMA_SUBSCRIBER_ID);
		assertTrue(subscribers.size() > 0);
		testSubscriberId = subscribers.get(0).getId();
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRmaContObjectGet() throws Exception {
		_testGetJson(apiRmaUrl("/contObjects"));
	}

	@Test
	public void testContObjectCRUD() throws Exception {
		ContObject contObject = new ContObject();
		contObject.setComment("Created by Test");
		contObject.setTimezoneDefKeyname("MSK");
		contObject.setName("Cont Object TEST");

		Long contObjectId = _testCreateJson(apiRmaUrl("/contObjects"), contObject);

		_testGetJson(apiRmaUrl("/contObjects/" + contObjectId));

		contObject = contObjectService.findContObject(contObjectId);
		contObject.setCurrentSettingMode("summer");
		_testUpdateJson(apiRmaUrl("/contObjects/" + contObjectId), contObject);

		_testDeleteJson(apiRmaUrl("/contObjects/" + contObjectId));
	}

	@Test
	public void testAvailableContObjects() throws Exception {
		_testGetJson(apiRmaUrl("/64166467/availableContObjects"));
	}

	@Test
	public void testSubscrContObjectsGet() throws Exception {
		_testGetJson(apiRmaUrl(String.format("/%d/subscrContObjects", testSubscriberId)));
	}

	@Test
	public void testSubscrContObjectsUpdate() throws Exception {
		List<ContObject> availableContObjects = subscrContObjectService.selectAvailableContObjects(testSubscriberId,
				EXCBT_RMA_SUBSCRIBER_ID);
		List<Long> addContObjects = new ArrayList<>();
		List<Long> currContObjects = subscrContObjectService.selectSubscriberContObjectIds(testSubscriberId);
		for (int i = 0; i < availableContObjects.size(); i++) {
			if (i > 1) {
				break;
			}
			addContObjects.add(availableContObjects.get(i).getId());
		}

		addContObjects.addAll(currContObjects);
		_testUpdateJson(apiRmaUrl(String.format("/%d/subscrContObjects", testSubscriberId)), addContObjects);
		_testUpdateJson(apiRmaUrl(String.format("/%d/subscrContObjects", testSubscriberId)), currContObjects);
	}

}
