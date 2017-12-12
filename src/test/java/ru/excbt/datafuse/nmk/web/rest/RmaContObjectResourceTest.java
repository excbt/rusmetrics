package ru.excbt.datafuse.nmk.web.rest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.RmaSubscriberService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
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

    @Autowired
    private ContObjectMapper contObjectMapper;

	private Long testSubscriberId;

	/**
	 *
	 */
	@Before
	public void initTestSubscriberId() {
		List<Subscriber> subscribers = rmaSubscriberService.selectRmaSubscribers(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);
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
		ContObjectDTO contObjectDTO = new ContObjectDTO();
        contObjectDTO.setComment("Created by Test");
        contObjectDTO.setTimezoneDefKeyname("MSK");
        contObjectDTO.setName("Cont Object TEST");

		Long contObjectId = _testCreateJson(UrlUtils.apiRmaUrl("/contObjects"), contObjectDTO);

		_testGetJson(UrlUtils.apiRmaUrl("/contObjects/" + contObjectId));

        {
            ContObject contObject = contObjectService.findContObjectChecked(contObjectId);

            contObjectDTO = contObjectMapper.toDto(contObject);
        }


        JSONObject object = new JSONObject();

        try {
            object.put("firstName", "John")
                .put("lastName", "Smith")
                .put("age", 25)
                .put("address", new JSONObject()
                    .put("streetAddress", "21 2nd Street")
                    .put("city", "New York")
                    .put("state", "NY")
                    .put("postalCode", "10021"))
                .put("phoneNumber",  new JSONArray()
                    .put(new JSONObject()
                        .put("type", "home")
                        .put("number", "212 555-1234"))
                    .put(new JSONObject()
                        .put("type", "fax")
                        .put("number", "646 555-4567")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        contObjectDTO.setFlexData(object.toString());


        contObjectDTO.setCurrentSettingMode("summer");
		_testUpdateJson("/api/rma/contObjects/" + contObjectId, contObjectDTO);

		_testDeleteJson("/api/rma/contObjects/" + contObjectId);
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
		List<ContObject> availableContObjects = objectAccessService.findRmaAvailableContObjects(testSubscriberId, TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID);
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
