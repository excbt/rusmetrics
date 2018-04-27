package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.ContObjectDTO;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.service.util.FlexDataFactory;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.service.RmaSubscriberService;
import ru.excbt.datafuse.nmk.service.SubscriberTimeService;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class RmaContObjectResourceTest extends PortalApiTest {

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


	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;


	private RmaContObjectResource rmaContObjectResource;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Autowired
    private ContGroupService contGroupService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ContObjectFiasService contObjectFiasService;
    @Autowired
    private MeterPeriodSettingService meterPeriodService;
    @Autowired
    private SubscriberAccessService subscriberAccessService;
    @Autowired
    private SubscriberTimeService subscriberTimeService;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        rmaContObjectResource = new RmaContObjectResource(
            contObjectService,
            contGroupService,
            organizationService,
            contObjectFiasService,
            meterPeriodService,
            portalUserIdsService,
            subscriberAccessService,
            objectAccessService,
            subscriberTimeService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(rmaContObjectResource)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}

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
		log.debug("Finding objects for subscriberId:{}", portalUserIdsService.getCurrentIds().getSubscriberId());
		List<Long> result = objectAccessService.findContObjectIds(portalUserIdsService.getCurrentIds().getSubscriberId());
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
        mockMvcRestWrapper.restRequest("/api/rma/contObjects").testGet();
//	    _testGetJson(UrlUtils.apiRmaUrl("/contObjects"));
	}

	@Test
	@Transactional
	public void testContObjectCRUD() throws Exception {
		ContObjectDTO contObjectDTO = new ContObjectDTO();
        contObjectDTO.setComment("Created by Test");
        contObjectDTO.setTimezoneDefKeyname("MSK");
        contObjectDTO.setName("Cont Object TEST");

		Long contObjectId = mockMvcRestWrapper.restRequest("/api/rma/contObjects").testPost(contObjectDTO).getLastId();
//            _testCreateJson(UrlUtils.apiRmaUrl("/contObjects"), contObjectDTO);

        mockMvcRestWrapper.restRequest("/api/rma/contObjects/").testGet();
//		_testGetJson(UrlUtils.apiRmaUrl("/contObjects/" + contObjectId));

        {
            ContObject contObject = contObjectService.findContObjectChecked(contObjectId);

            contObjectDTO = contObjectMapper.toDto(contObject);
        }

        contObjectDTO.setFlexData(FlexDataFactory.createFlexData1().toString());

        contObjectDTO.setCurrentSettingMode("summer");
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id}", contObjectId).testPut(contObjectDTO);
//		_testUpdateJson("/api/rma/contObjects/" + contObjectId, contObjectDTO);

        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id}", contObjectId).testDelete();
//		_testDeleteJson("/api/rma/contObjects/" + contObjectId);
	}

	@Test
	@Transactional
	public void testAvailableContObjects() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/64166467/availableContObjects").testGet();
//		_testGetJson(UrlUtils.apiRmaUrl("/64166467/availableContObjects"));
	}

	@Test
	@Transactional
	public void testSubscrContObjectsGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/{id}/subscrContObjects", testSubscriberId).testGet();
//		_testGetJson(UrlUtils.apiRmaUrl(String.format("/%d/subscrContObjects", testSubscriberId)));
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
        mockMvcRestWrapper.restRequest("/api/rma/{id}/subscrContObjects", testSubscriberId)
            .testPut(addContObjects)
            .testPut(currContObjects);
//		_testUpdateJson(UrlUtils.apiRmaUrl(String.format("/%d/subscrContObjects", testSubscriberId)), addContObjects);
//		_testUpdateJson(UrlUtils.apiRmaUrl(String.format("/%d/subscrContObjects", testSubscriberId)), currContObjects);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void testContObjectSubscribers() throws Exception {
//		_testGetJson("/api/rma/contObjects/29863789/subscribers");
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/29863789/subscribers").testGet();
		//64166466L, 29863789L
	}



}
