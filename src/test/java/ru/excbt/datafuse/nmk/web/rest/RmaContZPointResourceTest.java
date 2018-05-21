package ru.excbt.datafuse.nmk.web.rest;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.ContZPointMetadata;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointFullVM;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.SubscriberAccessService;
import ru.excbt.datafuse.nmk.service.mapper.ContZPointMapper;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.api.RmaContZPointResource;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;


@RunWith(SpringRunner.class)
public class RmaContZPointResourceTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaContZPointResourceTest.class);

	private final static long MANUAL_HW_CONT_ZPOINT_ID = 49492462;
	private final static long MANUAL_CONT_OBJECT_ID = 733;

	private final static String[] RSO_ORGANIZATIONS = { "TEST_RSO", "RSO_1" };

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private ContZPointService contZPointService;

    @Autowired
	private ContZPointMapper contZPointMapper;


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private RmaContZPointResource rmaContZPointResource;
    @Autowired
    private ContServiceDataHWaterService contServiceDataHWaterService;
    @Autowired
    private ContServiceDataElService contServiceDataElService;
    @Autowired
    private ContZPointMetadataService contZPointMetadataService;
    @Autowired
    private MeasureUnitService measureUnitService;
    @Autowired
    private ObjectAccessService objectAccessService;
    @Autowired
    private SubscriberAccessService subscriberAccessService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        rmaContZPointResource = new RmaContZPointResource(contZPointService,
            contServiceDataHWaterService,
            contServiceDataElService,
            contZPointMetadataService,
            measureUnitService,
            organizationService,
            objectAccessService,
            portalUserIdsService,
            subscriberAccessService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(rmaContZPointResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	@Test
	//@Ignore
    @Transactional
	public void testZPointCRUD() throws Exception {

        ContZPointFullVM contZPointFullVM0 = new ContZPointFullVM();
        contZPointFullVM0.setDeviceObjectId(65836845L);
        contZPointFullVM0.setContServiceTypeKeyname(ContServiceTypeKey.HEAT.getKeyname());
        contZPointFullVM0.setStartDate(new Date());

        contZPointFullVM0.setRsoId(randomRsoOrganizationId());

//		String url = UrlUtils.apiRmaUrl(String.format("/contObjects/{id}/zpoints", MANUAL_CONT_OBJECT_ID));

		Long contZPointId = mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id}/zpoints", MANUAL_CONT_OBJECT_ID).testPost(contZPointFullVM0).getLastId();

//            _testCreateJson(url, contZPointFullVM0);

        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/zpoints/{id2}", MANUAL_CONT_OBJECT_ID, contZPointId).testGet();

//		_testGetJson(UrlUtils.apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)));

        ContZPointFullVM contZPointFullVM;

        {
            ContZPoint contZPoint = contZPointService.findOne(contZPointId);

            contZPointFullVM = contZPointMapper.toFullVM(contZPoint);
        }

		Long activeDeviceObjectId = contZPointFullVM.getDeviceObject() != null ?
            contZPointFullVM.getDeviceObject().getId() : null;
//        contZPointFullVM.getDeviceObjects().clear();
        contZPointFullVM.setContZPointComment("Modified by TEST");
        contZPointFullVM.setRsoId(randomRsoOrganizationId());
        //contZPointFullVM.setContObject(null);
        contZPointFullVM.setContServiceType(null);
        contZPointFullVM.setRso(null);
        contZPointFullVM.setDeviceObject(null);
        contZPointFullVM.setDeviceObjectId(activeDeviceObjectId);
        contZPointFullVM.setExCode("ex_code111");



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
        contZPointFullVM.setFlexData(object.toString());


        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/zpoints/{id2}", MANUAL_CONT_OBJECT_ID, contZPointId).testPut(contZPointFullVM);
//		_testUpdateJson(UrlUtils.apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)),
//            contZPointFullVM);


        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/zpoints/{id2}", MANUAL_CONT_OBJECT_ID, contZPointId).testDelete();

//		_testDeleteJson(UrlUtils.apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, contZPointId)));
	}

	@Test
	@Ignore
	public void testTemporaryGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/zpoints/{id2}", MANUAL_CONT_OBJECT_ID, 66183331L).testGet();
//		_testGetJson(UrlUtils.apiRmaUrl(String.format("/contObjects/%d/zpoints/%d", MANUAL_CONT_OBJECT_ID, 66183331L)));
	}

	@Test
    @Transactional
	public void testRsoOrganizations() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/rsoOrganizations").testGet();
//		_testGetJson(UrlUtils.apiRmaUrl("/contObjects/rsoOrganizations"));
	}

	private Long randomRsoOrganizationId() {
		int idx = ThreadLocalRandom.current().nextInt(0, 2);

		SubscriberParam param = SubscriberParam.builder().subscriberId(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID)
				.rmaSubscriber(TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID).build();
		Organization o = organizationService.selectByKeyname(param, RSO_ORGANIZATIONS[idx]);
		return o == null ? null : o.getId();
	}

	@Ignore
	@Test
    @Transactional
	public void testContZPointTemperatureChart() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contObjects/488501788/contZPointsEx").testGet();
//		_testGetJson("/api/subscr/contObjects/488501788/contZPointsEx");
	}

	@Test
    @Transactional
	public void testContZPointMetadata() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/725/zpoints/512084866/metadata").testGet();
//		_testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata");
	}

	@Test
    @Transactional
	public void testContZPointMetadataSrcProp() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/63030238/zpoints/63031662/metadata/srcProp").testGet();
//		_testGetJson("/api/rma/contObjects/63030238/zpoints/63031662/metadata/srcProp");
	}

    // TODO access_denied
	@Test
    @Transactional
	public void testContZPointMetadataDestProp() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/725/zpoints/512084866/metadata/destProp").testGet();
//		_testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata/destProp");
	}

    // TODO access_denied
	@Test
    @Transactional
	public void testContZPointMetadataDestDb() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/725/zpoints/512084866/metadata/destDb").testGet();
//		_testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata/destDb");
	}

	// TODO access_denied
	@Ignore
	@Test
    @Transactional
	public void testContZPointMetadataCRUD() throws Exception {
		final String content = mockMvcRestWrapper.restRequest("/api/rma/contObjects/725/zpoints/512084866/metadata").testGet().getStringContent();
//            _testGetJson("/api/rma/contObjects/725/zpoints/512084866/metadata");

		List<ContZPointMetadata> metadata = TestUtils.fromJSON(new TypeReference<List<ContZPointMetadata>>() {
		}, content);

		assertNotNull(metadata);

		logger.info("Found {} records", metadata.size());

		for (ContZPointMetadata m : metadata) {
			if (!m.isNew()) {
				m.setDevComment("Updated by REST at: " + System.currentTimeMillis());
			}
		}

        mockMvcRestWrapper.restRequest("/api/rma/contObjects/725/zpoints/512084866/metadata").testPut(metadata);
//		_testUpdateJson("/api/rma/contObjects/725/zpoints/512084866/metadata", metadata);

	}

}
