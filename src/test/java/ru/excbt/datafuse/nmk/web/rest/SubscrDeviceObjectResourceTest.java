package ru.excbt.datafuse.nmk.web.rest;

import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.app.PortalApplication;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;
import ru.excbt.datafuse.nmk.data.model.types.ContServiceTypeKey;
import ru.excbt.datafuse.nmk.data.repository.ContZPointDeviceHistoryRepository;
import ru.excbt.datafuse.nmk.data.repository.DeviceObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.VzletSystemRepository;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.service.util.EntityAutomation;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.DeviceObjectMapper;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PortalApplication.class)
@WithMockUser(username = "admin", password = "admin",
    roles = { "ADMIN", "SUBSCR_ADMIN", "SUBSCR_USER", "CONT_OBJECT_ADMIN", "ZPOINT_ADMIN", "DEVICE_OBJECT_ADMIN",
        "RMA_CONT_OBJECT_ADMIN", "RMA_ZPOINT_ADMIN", "RMA_DEVICE_OBJECT_ADMIN", "SUBSCR_CREATE_CABINET",
        "CABINET_USER" })
@Transactional
public class SubscrDeviceObjectResourceTest {

	private static final Logger log = LoggerFactory.getLogger(SubscrDeviceObjectResourceTest.class);

	private final static Long DEV_CONT_OBJECT = 733L;
	private final static Long DEV_DEVICE_OBJECT = 54209288L;


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private DeviceObjectService deviceObjectService;

    @Autowired
    private SubscrDeviceObjectResource subscrDeviceObjectResource;

    @Autowired
    private DeviceObjectLoadingSettingsService deviceObjectLoadingSettingsService;

    @Autowired
    private DeviceObjectLoadingLogService deviceObjectLoadingLogService;

    @Autowired
    private VzletSystemRepository vzletSystemRepository;

    @Autowired
    private DeviceModelService deviceModelService;

    @Autowired
    private ContObjectService contObjectService;

    @Autowired
    private SubscrDataSourceService subscrDataSourceService;

    @Autowired
    private DeviceMetadataService deviceMetadataService;

    @Autowired
    private SubscrDataSourceLoadingSettingsService subscrDataSourceLoadingSettingsService;

    @Autowired
    private HeatRadiatorTypeService heatRadiatorTypeService;

    @Autowired
    private DeviceDataTypeService deviceDataTypeService;

    @Autowired
    private DeviceObjectMapper deviceObjectMapper;

    @Autowired
    private ObjectAccessService objectAccessService;

    @Autowired
    private ContZPointDeviceHistoryService contZPointDeviceHistoryService;

    @Autowired
    private ContZPointService contZPointService;

    @Autowired
    private ContZPointDeviceHistoryRepository contZPointDeviceHistoryRepository;

    @Autowired
    private DeviceObjectRepository deviceObjectRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        subscrDeviceObjectResource = new SubscrDeviceObjectResource(deviceObjectService,
            deviceObjectLoadingSettingsService,
            deviceObjectLoadingLogService,
            vzletSystemRepository,
            deviceModelService,
            contObjectService,
            subscrDataSourceService,
            deviceMetadataService,
            subscrDataSourceLoadingSettingsService,
            heatRadiatorTypeService,
            deviceDataTypeService,
            deviceObjectMapper,
            objectAccessService,
            portalUserIdsService,
            contZPointDeviceHistoryService);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(subscrDeviceObjectResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }


    /**
     *
     * @throws Exception
     */
	@Test
    @Transactional
	public void testDeviceObjectsGet() throws Exception {

        ContObject contObject = EntityAutomation.createContObject(
            "Test Cont Object",
            contObjectService,
            portalUserIdsService.getCurrentIds());

        log.info("Created ContObject: {}", contObject.getId());

        List<DeviceObject> createdDeviceObjects = EntityAutomation.createDeviceObjects(contObject, deviceObjectService, "test-1111111", "test-2222");
        assertThat(createdDeviceObjects).hasSize(2);

        createdDeviceObjects.forEach(i -> log.info("Created DeviceObject: {}", i.getId()));

        restPortalContObjectMockMvc.perform(
            get("/api/subscr/contObjects/{contObjectId}/deviceObjects", contObject.getId()))
            .andDo(MockMvcResultHandlers.print())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[*].id").value(hasItem(createdDeviceObjects.get(0).getId().intValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(createdDeviceObjects.get(0).getNumber())))
            .andExpect(jsonPath("$.[*].id").value(hasItem(createdDeviceObjects.get(1).getId().intValue())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(createdDeviceObjects.get(1).getNumber())));
	}

    /**
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testDeviceObjectUpdate() throws Exception {

        ContObject contObject = EntityAutomation.createContObject(
            "Test Cont Object",
            contObjectService,
            portalUserIdsService.getCurrentIds());

        assertThat(contObject).isNotNull();
        assertThat(contObject.getId()).isNotNull();

        log.info("Created ContObject: {}", contObject.getId());

        DeviceObject deviceObject = EntityAutomation.createDeviceObject(
            contObject,
            deviceObjectService,
            "1111111");

        assertThat(deviceObject).isNotNull();
        assertThat(deviceObject.getId()).isNotNull();


        final long id = deviceObject.getId();
        DeviceObjectDTO deviceObjectDTO = deviceObjectService.findDeviceObjectDTO(id);
        TestUtils.objectToJson(deviceObjectDTO);
        deviceObjectDTO.createDeviceLoginIngo();
        deviceObjectDTO.getDeviceLoginInfo().setDeviceLogin("user");
        deviceObjectDTO.getDeviceLoginInfo().setDevicePassword("pass");
	    deviceObjectDTO.setIsTimeSyncEnabled(true);
	    assertThat(deviceObjectDTO.getEditDataSourceInfo()).isNotNull();
	    assertThat(deviceObjectDTO.getEditDataSourceInfo().getSubscrDataSourceId()).isNotNull();
	    if (deviceObjectDTO.getEditDataSourceInfo() != null) {
            deviceObjectDTO.getEditDataSourceInfo().setSubscrDataSourceAddr("123");
        }

        restPortalContObjectMockMvc.perform(
            put("/api/subscr/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}",contObject.getId(),deviceObject.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(deviceObjectDTO)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(deviceObject.getId().intValue()))
            .andExpect(jsonPath("$.deviceLoginInfo.deviceLogin").value("user"))
            .andExpect(jsonPath("$.deviceLoginInfo.devicePassword").value("pass"));

    }

    /**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjectMetaDataVzletGet() throws Exception {
        restPortalContObjectMockMvc.perform(
            get("/api/subscr/contObjects/{contObjectId}/deviceObjects/{deviceObject}/metaVzlet", DEV_CONT_OBJECT, DEV_DEVICE_OBJECT))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjectMetaVzletCRUD() throws Exception {
		deviceObjectService.deleteDeviceObjectMetaVzlet(DEV_DEVICE_OBJECT);

		DeviceObjectMetaVzlet metaVzlet = new DeviceObjectMetaVzlet();
		metaVzlet.setVzletTableDay("Day XXX");
		metaVzlet.setVzletTableHour("Hour XXX");

		String url = UrlUtils.apiSubscrUrl(
				String.format("/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet", DEV_CONT_OBJECT, DEV_DEVICE_OBJECT));


        ResultActions resultActions = restPortalContObjectMockMvc.perform(
            post("/api/subscr/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet", DEV_CONT_OBJECT, DEV_DEVICE_OBJECT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(metaVzlet)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated());

        String jsonContent = resultActions.andReturn().getResponse().getContentAsString();

		Integer metaId = JsonPath.read(jsonContent, "$.id");

		metaVzlet.setId(Long.valueOf(metaId));
		metaVzlet.setVzletTableDay("Day YYY");
		metaVzlet.setVzletTableHour("Hour YYY");

        restPortalContObjectMockMvc.perform(
            put("/api/subscr/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet", DEV_CONT_OBJECT, DEV_DEVICE_OBJECT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(metaVzlet)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());


        restPortalContObjectMockMvc.perform(
            delete("/api/subscr/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/metaVzlet", DEV_CONT_OBJECT, DEV_DEVICE_OBJECT)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(metaVzlet)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNoContent());

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjectsVzletSystemGet() throws Exception {

        restPortalContObjectMockMvc.perform(
            get("/api/subscr/deviceObjects/metaVzlet/system"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(jsonPath("$.[*].contServiceType.keyname").value(hasItems(
                ContServiceTypeKey.CW.getKeyname(),
                ContServiceTypeKey.HEAT.getKeyname(),
                ContServiceTypeKey.HW.getKeyname())))

            .andExpect(jsonPath("$.[*].contServiceTypeKey").value(hasItems(
                ContServiceTypeKey.CW.getKeyname(),
                ContServiceTypeKey.HEAT.getKeyname(),
                ContServiceTypeKey.HW.getKeyname())));
	}


	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjectGet() throws Exception {


        ContObject contObject = EntityAutomation.createContObject(
            "Test Cont Object",
            contObjectService,
            portalUserIdsService.getCurrentIds());

        log.info("Created ContObject: {}", contObject.getId());

        List<DeviceObject> createdDeviceObjects = EntityAutomation.createDeviceObjects(contObject, deviceObjectService, "test-1111111");
        assertThat(createdDeviceObjects).hasSize(1);

        createdDeviceObjects.forEach(i -> log.info("Created DeviceObject: {}", i.getId()));

        restPortalContObjectMockMvc.perform(
            get("/api/subscr/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}",
                contObject.getId(),
                createdDeviceObjects.get(0).getId()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(createdDeviceObjects.get(0).getId().intValue()))
            .andExpect(jsonPath("$.number").value(createdDeviceObjects.get(0).getNumber()));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceModelsGet() throws Exception {


        restPortalContObjectMockMvc.perform(
            get("/api/subscr/deviceObjects/deviceModels"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(1)))
            .andExpect(jsonPath("$.[*].exCode").value(hasItem("NA")));



        restPortalContObjectMockMvc.perform(
            get("/api/subscr/deviceObjects/deviceModels/{deviceModelId}",1))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.exCode").value("NA"));


	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceModelMetadataGet() throws Exception {
      restPortalContObjectMockMvc.perform(
            get("/api/subscr/deviceObjects/deviceModels/{deviceModelId}/metadata",29779958))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjectDataSourceGet() throws Exception {
		//65836845
        restPortalContObjectMockMvc.perform(
            get("/api/subscr/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/subscrDataSource",725, 65836845))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));

	}

	@Ignore
	@Test
    @Transactional
	public void testDeviceObjectDataSourceLoadingSettingsGet() throws Exception {
        restPortalContObjectMockMvc.perform(
            get("/api/subscr/contObjects/{contObjectId}/deviceObjects/{deviceObjectId}/subscrDataSource/loadingSettings",
                725,
                65836845))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
	}

	@Test
    @Transactional
	public void testDeviceModelTypes() throws Exception {
        restPortalContObjectMockMvc.perform(
            get("/api/subscr/deviceObjects/deviceModelTypes"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
	}

	@Test
    @Transactional
	public void testDeviceImpulseCounterTypes() throws Exception {
        restPortalContObjectMockMvc.perform(
            get("/api/subscr/deviceObjects/impulseCounterTypes"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
	}


    @Test
    public void testHeatRadiatorTypes() throws Exception {
        restPortalContObjectMockMvc.perform(
            get("/api/subscr/deviceObjects/heatRadiatorTypes"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)));
    }


    @Test
    @Transactional
    public void deviceObjectHistory() throws Exception {

	    ContObject contObject = EntityAutomation.createContObject(
	        "Test Cont Object",
            contObjectService,
            portalUserIdsService.getCurrentIds());

	    assertThat(contObject).isNotNull();
	    assertThat(contObject.getId()).isNotNull();

        log.info("Created ContObject: {}", contObject.getId());


        List<DeviceObject> createdDeviceObjects = EntityAutomation.createDeviceObjects(contObject, deviceObjectService, "1111111", "2222");
        assertThat(createdDeviceObjects).hasSize(2);

        ContZPoint contZPoint = EntityAutomation.createContZPoint(
            ContServiceTypeKey.CW,
            contObject,
            createdDeviceObjects.get(0),
            contZPointService);

        assertThat(contZPoint).isNotNull();
        assertThat(contZPoint.getId()).isNotNull();

        log.info("Created ContZPoint: {}", contZPoint.getId());

        contZPoint.setDeviceObject(createdDeviceObjects.get(1));
        contZPointService.updateContZPoint(contZPoint);

        DeviceObject checkDeviceObject = deviceObjectRepository.findOne(createdDeviceObjects.get(0).getId());
        assertThat(checkDeviceObject).isNotNull();
        assertThat(checkDeviceObject.getContObject()).isNotNull();
        assertThat(checkDeviceObject.getContObject().getId()).isNotNull();

        contZPointDeviceHistoryRepository.flush();
        List<ContZPointDeviceHistory> historyList = contZPointDeviceHistoryRepository.findAllByContZPoint(contZPoint);
        assertThat(historyList).isNotEmpty();
        assertThat(historyList.get(0).getDeviceObject()).isNotNull();
        assertThat(historyList.get(0).getDeviceObject().getContObject()).isNotNull();

        restPortalContObjectMockMvc.perform(
            get("/api/subscr/device-objects/cont-zpoints/{contZPointId}/history", contZPoint.getId()))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.anyJsonBeatifyResult(i)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].deviceObject.id").value(hasItem(createdDeviceObjects.get(0).getId().intValue())))
            .andExpect(jsonPath("$.[*].deviceObject.number").value(hasItem(createdDeviceObjects.get(0).getNumber())))
            .andExpect(jsonPath("$.[*].contZPointId").value(hasItem(contZPoint.getId().intValue())))
            .andExpect(jsonPath("$.[*].deviceObject.id").value(hasItem(createdDeviceObjects.get(1).getId().intValue())))
            .andExpect(jsonPath("$.[*].deviceObject.number").value(hasItem(createdDeviceObjects.get(1).getNumber())))
            .andExpect(jsonPath("$.[*].revision").value(hasItem(1)))
            .andExpect(jsonPath("$.[*].revision").value(hasItem(2)));

    }

}
