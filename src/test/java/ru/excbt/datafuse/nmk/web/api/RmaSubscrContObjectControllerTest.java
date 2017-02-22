package ru.excbt.datafuse.nmk.web.api;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.MeterPeriodSetting;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectMeterPeriodSettingsDTO;
import ru.excbt.datafuse.nmk.data.model.dto.MeterPeriodSettingDTO;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.MeterPeriodSettingService;
import ru.excbt.datafuse.nmk.data.service.RmaSubscriberService;
import ru.excbt.datafuse.nmk.data.service.SubscrContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RmaSubscrContObjectControllerTest extends RmaControllerTest {

	private static final Logger log = LoggerFactory.getLogger(RmaSubscrContObjectControllerTest.class);
	
	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private ContObjectRepository contObjectRepository;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private RmaSubscriberService rmaSubscriberService;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

	@Autowired
	private MeterPeriodSettingService meterPeriodSettingService;

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
		List<Long> result = subscrContObjectService.selectSubscriberContObjectIds(getSubscriberId());
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
		_testGetJson(apiRmaUrl("/contObjects"));
	}

	@Test
	@Transactional
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
	@Transactional
	public void testAvailableContObjects() throws Exception {
		_testGetJson(apiRmaUrl("/64166467/availableContObjects"));
	}

	@Test
	@Transactional
	public void testSubscrContObjectsGet() throws Exception {
		_testGetJson(apiRmaUrl(String.format("/%d/subscrContObjects", testSubscriberId)));
	}

	@Test
	@Transactional
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

	@Test
	@Transactional
	public void testUpdateContObjectMeterSettingsDTO() throws Exception {
		Long contObjectId = findSubscriberContObjectIds().get(0);
		MeterPeriodSettingDTO setting = meterPeriodSettingService
				.save(MeterPeriodSettingDTO.builder().name("MySetting").build());
		ContObjectMeterPeriodSettingsDTO coSetting = ContObjectMeterPeriodSettingsDTO.builder()
				.contObjectId(contObjectId).build();
		coSetting.putSetting(ContServiceTypeKey.CW.getKeyname(), setting.getId());
		_testUpdateJson(apiRmaUrlTemplate("/contObjects/%d/meterPeriodSettings", contObjectId), coSetting);

		ContObject contObject = contObjectService.findContObject(contObjectId);
		assertTrue(contObject.getMeterPeriodSettings() != null);
		MeterPeriodSetting meterPeriod = contObject.getMeterPeriodSettings().get(ContServiceTypeKey.CW.getKeyname());
		assertTrue(meterPeriod != null);
		assertTrue(meterPeriod.getId().equals(setting.getId()));
	}

	@Test
	@Transactional
	public void testGetOneContObjectMeterSettingsDTO() throws Exception {
		Long contObjectId = findSubscriberContObjectIds().get(0);
		MeterPeriodSettingDTO setting = meterPeriodSettingService
				.save(MeterPeriodSettingDTO.builder().name("MySetting").build());
		ContObject contObject = contObjectRepository.findOne(contObjectId);
		MeterPeriodSetting meterPeriod = new MeterPeriodSetting().id(setting.getId());

		contObject.getMeterPeriodSettings().put(ContServiceTypeKey.CW.getKeyname(), meterPeriod);
		contObjectRepository.saveAndFlush(contObject);
		_testGetJsonResultActions(apiRmaUrlTemplate("/contObjects/%d/meterPeriodSettings", contObjectId))
				.andDo((result) -> {
				});
	}

	@Test
	@Transactional
	public void testGetAllContObjectMeterSettingsDTO() throws Exception {
		Long contObjectId = findSubscriberContObjectIds().get(0);
		MeterPeriodSettingDTO setting = meterPeriodSettingService
				.save(MeterPeriodSettingDTO.builder().name("MySetting").build());
		ContObject contObject = contObjectRepository.findOne(contObjectId);
		MeterPeriodSetting meterPeriod = new MeterPeriodSetting().id(setting.getId());

		contObject.getMeterPeriodSettings().put(ContServiceTypeKey.CW.getKeyname(), meterPeriod);
		contObjectRepository.saveAndFlush(contObject);
		_testGetJsonResultActions(apiRmaUrlTemplate("/contObjects/meterPeriodSettings")).andDo((result) -> {
		});
	}

	@Test
	@Transactional	
	public void testUpdateAllContObjectMeterSettingsDTO() throws Exception {
		MeterPeriodSettingDTO setting = meterPeriodSettingService
				.save(MeterPeriodSettingDTO.builder().name("MySetting").build());
		
		ContObjectMeterPeriodSettingsDTO contObjectSettings = new ContObjectMeterPeriodSettingsDTO()
				.contObjectIds(findSubscriberContObjectIds()).putSetting("cw", setting.getId());
		
		_testPutJson(apiRmaUrlTemplate("/contObjects/meterPeriodSettings"), contObjectSettings).andDo((result) -> {
		});
	}

}
