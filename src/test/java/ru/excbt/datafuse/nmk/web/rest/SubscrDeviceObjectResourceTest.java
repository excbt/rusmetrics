package ru.excbt.datafuse.nmk.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
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
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.rest.util.JsonResultViewer;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class SubscrDeviceObjectResourceTest extends AnyControllerTest {

	private static final Logger log = LoggerFactory.getLogger(SubscrDeviceObjectResourceTest.class);

	private final static Long DEV_CONT_OBJECT = 733L;
	private final static Long DEV_DEVICE_OBJECT = 54209288L;

	private final static long DEV_RMA_DEVICE_OBJECT_ID = 737;
	private final static long DEV_RMA_CONT_OBJECT_ID = 725;


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



	@Test
    @Transactional
	public void testDeviceObjectsGet() throws Exception {
		String url = UrlUtils.apiSubscrUrl(String.format("/contObjects/%d/deviceObjects", DEV_CONT_OBJECT));
		_testGetJson(url);
	}

    @Test
    @Transactional
    public void testDeviceObjectUpdate() throws Exception {
        final long id = 128729223L;
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
        String url = UrlUtils.apiSubscrUrl(String.format("/contObjects/%d/deviceObjects/%d", DEV_CONT_OBJECT,id));
        _testPutJson(url,deviceObjectDTO);
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
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.arrayBeatifyResult(i)));
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
				String.format("/contObjects/%d/deviceObjects/%d/metaVzlet", DEV_CONT_OBJECT, DEV_DEVICE_OBJECT));

		Long metaId = _testCreateJson(url, metaVzlet);

		metaVzlet.setId(metaId);
		metaVzlet.setVzletTableDay("Day YYY");
		metaVzlet.setVzletTableHour("Hour YYY");

		_testUpdateJson(url, metaVzlet);

		_testDeleteJson(url);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjectsVzletSystemGet() throws Exception {
		String url = UrlUtils.apiSubscrUrl("/deviceObjects/metaVzlet/system");
		_testGetJson(url);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjects733Get() throws Exception {
		String url = UrlUtils.apiSubscrUrl(String.format("/contObjects/%d/deviceObjects", 733));
		_testGetJson(url);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjects733_128729223Get() throws Exception {
		String url = UrlUtils.apiSubscrUrl(String.format("/contObjects/%d/deviceObjects/%d", 733, 128729223));
		_testGetJson(url);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceModelsGet() throws Exception {
		String response = _testGetJson(UrlUtils.apiSubscrUrl("/deviceObjects/deviceModels"));

		List<DeviceModel> deviceModels = TestUtils.fromJSON(new TypeReference<List<DeviceModel>>() {
		}, response);

		if (!deviceModels.isEmpty()) {
			_testGetJson(UrlUtils.apiSubscrUrl("/deviceObjects/deviceModels/" + deviceModels.get(0).getId()));
		}


        _testGetJson(UrlUtils.apiSubscrUrl("/deviceObjects/deviceModels/" + 129265814));

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceModelMetadataGet() throws Exception {
		_testGetJson(UrlUtils.apiSubscrUrl("/deviceObjects/deviceModels/29779958/metadata"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjectDataSourceGet() throws Exception {
		//65836845
		_testGetJson(UrlUtils.apiSubscrUrl("/contObjects/%d/deviceObjects/%d/subscrDataSource", 725, 65836845));
	}

	@Ignore
	@Test
    @Transactional
	public void testDeviceObjectDataSourceLoadingSettingsGet() throws Exception {
		//65836845
		_testGetJson(UrlUtils.apiSubscrUrl("/contObjects/%d/deviceObjects/%d/subscrDataSource/loadingSettings", 725, 65836845));
	}

	@Test
    @Transactional
	public void testDeviceModelTypes() throws Exception {
		_testGetJson(UrlUtils.apiSubscrUrl("/deviceObjects/deviceModelTypes"));
	}

	@Test
    @Transactional
	public void testDeviceImpulseCounterTypes() throws Exception {
		_testGetJson(UrlUtils.apiSubscrUrl("/deviceObjects/impulseCounterTypes"));
	}


    @Test
    public void testHeatRadiatorTypes() throws Exception {
	    _testGetJson(UrlUtils.apiSubscrUrl("/deviceObjects/heatRadiatorTypes"));
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

	    DeviceObject deviceObject = EntityAutomation.createDeviceObject(
	        "1111111",
            contObject,
            deviceObjectService);

	    assertThat(deviceObject).isNotNull();
	    assertThat(deviceObject.getId()).isNotNull();

	    log.info("Created DeviceObject: {}", deviceObject.getId());


        DeviceObject deviceObject2nd = EntityAutomation.createDeviceObject(
            "2222",
            contObject,
            deviceObjectService);

        assertThat(deviceObject2nd).isNotNull();
        assertThat(deviceObject2nd.getId()).isNotNull();

        log.info("Created DeviceObject2nd: {}", deviceObject.getId());

        ContZPoint contZPoint = EntityAutomation.createContZPoint(
            ContServiceTypeKey.CW,
            contObject,
            deviceObject,
            contZPointService);

        assertThat(contZPoint).isNotNull();
        assertThat(contZPoint.getId()).isNotNull();

        log.info("Created ContZPoint: {}", contZPoint.getId());

        contZPoint.setDeviceObject(deviceObject2nd);
        contZPointService.updateContZPoint(contZPoint);

        DeviceObject checkDeviceObject = deviceObjectRepository.findOne(deviceObject.getId());
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
            .andDo((i) -> log.info("Result Json:\n {}", JsonResultViewer.arrayBeatifyResult(i)))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].deviceObject.id").value(hasItem(deviceObject.getId().intValue())))
            .andExpect(jsonPath("$.[*].deviceObject.number").value(hasItem(deviceObject.getNumber())))
            .andExpect(jsonPath("$.[*].contZPointId").value(hasItem(contZPoint.getId().intValue())))
            .andExpect(jsonPath("$.[*].deviceObject.id").value(hasItem(deviceObject2nd.getId().intValue())))
            .andExpect(jsonPath("$.[*].deviceObject.number").value(hasItem(deviceObject2nd.getNumber())))
            .andExpect(jsonPath("$.[*].revision").value(hasItem(1)))
            .andExpect(jsonPath("$.[*].revision").value(hasItem(2)));


    }


}
