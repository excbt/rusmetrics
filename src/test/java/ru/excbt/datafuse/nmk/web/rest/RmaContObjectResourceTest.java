package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.RmaSubscriberService;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Transactional
public class RmaContObjectResourceTest extends RmaControllerTest {

	private static final Logger log = LoggerFactory.getLogger(RmaContObjectResourceTest.class);

	@Autowired
	private ContObjectService contObjectService;


	@Autowired
	private RmaSubscriberService rmaSubscriberService;


    @Autowired
	private ObjectAccessService objectAccessService;

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
	 * @return
	 */
	private List<Long> findSubscriberContObjectIds() {
		log.debug("Finding objects for subscriberId:{}", getSubscriberId());
		List<Long> result = objectAccessService.findContObjectIds(getSubscriberId());
		assertFalse(result.isEmpty());
		return result;
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void testRmaContObjectGet() throws Exception {
		_testGetJson(UrlUtils.apiRmaUrl("/contObjects"));
	}

	@Test
	@Transactional
	public void testContObjectCRUD() throws Exception {
		ContObject contObject = new ContObject();
		contObject.setComment("Created by Test");
		contObject.setTimezoneDefKeyname("MSK");
		contObject.setName("Cont Object TEST");

		Long contObjectId = _testCreateJson(UrlUtils.apiRmaUrl("/contObjects"), contObject);

		_testGetJson(UrlUtils.apiRmaUrl("/contObjects/" + contObjectId));

		contObject = contObjectService.findContObjectChecked(contObjectId);
		contObject.setCurrentSettingMode("summer");
		_testUpdateJson(UrlUtils.apiRmaUrl("/contObjects/" + contObjectId), contObject);

		_testDeleteJson(UrlUtils.apiRmaUrl("/contObjects/" + contObjectId));
	}

	@Test
	@Transactional
	public void testAvailableContObjects() throws Exception {
		_testGetJson(UrlUtils.apiRmaUrl("/64166467/availableContObjects"));
	}

	@Test
	@Transactional
	public void testSubscrContObjectsGet() throws Exception {
		_testGetJson(UrlUtils.apiRmaUrl(String.format("/%d/subscrContObjects", testSubscriberId)));
	}

	@Test
	@Transactional
	public void testSubscrContObjectsUpdate() throws Exception {
		List<ContObject> availableContObjects = objectAccessService.findRmaAvailableContObjects(testSubscriberId, EXCBT_RMA_SUBSCRIBER_ID);
		List<Long> addContObjects = new ArrayList<>();
		List<Long> currContObjects = objectAccessService.findContObjectIds(testSubscriberId);
		for (int i = 0; i < availableContObjects.size(); i++) {
			if (i > 1) {
				break;
			}
			addContObjects.add(availableContObjects.get(i).getId());
		}

		addContObjects.addAll(currContObjects);
		_testUpdateJson(UrlUtils.apiRmaUrl(String.format("/%d/subscrContObjects", testSubscriberId)), addContObjects);
		_testUpdateJson(UrlUtils.apiRmaUrl(String.format("/%d/subscrContObjects", testSubscriberId)), currContObjects);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void testContObjectSubscribers() throws Exception {
		_testGetJson("/api/rma/contObjects/29863789/subscribers");
		//64166466L, 29863789L
	}



}
