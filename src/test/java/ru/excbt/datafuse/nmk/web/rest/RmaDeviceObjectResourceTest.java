package ru.excbt.datafuse.nmk.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.types.DeviceModelType;
import ru.excbt.datafuse.nmk.data.repository.HeatRadiatorTypeRepository;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectLoadingSettingsService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrDataSourceLoadingSettingsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
public class RmaDeviceObjectResourceTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaDeviceObjectResourceTest.class);

//	private final static Long DEV_CONT_OBJECT = 733L;
//	private final static Long DEV_DEVICE_OBJECT = 54209288L;

	private final static long DEV_RMA_DEVICE_OBJECT_ID = 737;
	private final static long DEV_RMA_CONT_OBJECT_ID = 725;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private DeviceObjectLoadingSettingsService deviceObjectLoadingSettingsService;

	@Autowired
	private SubscrDataSourceLoadingSettingsService subscrDataSourceLoadingSettingsService;

    @Autowired
	private HeatRadiatorTypeRepository heatRadiatorTypeRepository;


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @MockBean
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private RmaDeviceObjectResource rmaDeviceObjectResource;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

//        rmaDeviceObjectResource = new RmaDeviceObjectResource();

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(rmaDeviceObjectResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
    }

    /*

     */
	@Test
	//@Ignore
    @Transactional
	public void testDeviceObjectUpdate() throws Exception {



		String url = UrlUtils.apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, DEV_RMA_DEVICE_OBJECT_ID));


		String deviceObjectContent = mockMvcRestWrapper.restRequest(url).testGet().getStringContent();
//		String deviceObjectContent = _testGetJson(url);

		DeviceObject deviceObject = TestUtils.fromJSON(new TypeReference<DeviceObject>() {
		}, deviceObjectContent);

		deviceObject.setContObject(null);
		deviceObject.setNumber("Nr:" + System.currentTimeMillis());

		deviceObject.getEditDataSourceInfo().setSubscrDataSourceId(65523603L);
		deviceObject.getEditDataSourceInfo().setSubscrDataSourceAddr("РУС Addr 222:" + System.currentTimeMillis());
		deviceObject.getEditDataSourceInfo().setDataSourceTable("РУС Table 222:" + System.currentTimeMillis());
		deviceObject.getEditDataSourceInfo().setDataSourceTable1h("РУС Table1h 222:" + System.currentTimeMillis());
		deviceObject.getEditDataSourceInfo().setDataSourceTable24h("РУС Table24h 222:" + System.currentTimeMillis());

		assertNotNull(deviceObject.getDeviceLoginInfo());
		deviceObject.getDeviceLoginInfo().setDeviceLogin("login_12345");
		deviceObject.getDeviceLoginInfo().setDevicePassword("pass_12345");


		HeatRadiatorType heatRadiatorType = new HeatRadiatorType();
		heatRadiatorType.setTypeName("Тестовый тип");
        heatRadiatorTypeRepository.saveAndFlush(heatRadiatorType);
        assertNotNull(heatRadiatorType.getId());

        deviceObject.setHeatRadiatorTypeId(heatRadiatorType.getId());

		RequestExtraInitializer paramInit = (builder) -> {
			builder.param("subscrDataSourceId", String.valueOf(65523603));
			builder.param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis());
			builder.param("dataSourceTable", "Table:" + System.currentTimeMillis());
			builder.param("dataSourceTable1h", "Table 1H:" + System.currentTimeMillis());
			builder.param("dataSourceTable24h", "Table 24H:" + System.currentTimeMillis());
		};

        mockMvcRestWrapper.restRequest(url)
            .requestBuilder(b -> b
                    .param("subscrDataSourceId", String.valueOf(65523603))
                    .param("subscrDataSourceAddr","Addr:" + System.currentTimeMillis())
                    .param("dataSourceTable", "Table:" + System.currentTimeMillis())
                    .param("dataSourceTable1h", "Table 1H:" + System.currentTimeMillis())
                    .param("dataSourceTable24h", "Table 24H:" + System.currentTimeMillis())
            )
            .testPut(deviceObject)
            .testGet();

//
//		_testUpdateJson(url, deviceObject, paramInit);
//
//		_testGetJson(url);
	}

    /*

     */
	@Test
    @Transactional
	//@Ignore
	public void testDeviceObjectLoginInfoUpdate() throws Exception {
		String url = UrlUtils.apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, DEV_RMA_DEVICE_OBJECT_ID));


		String deviceObjectContent = mockMvcRestWrapper.restRequest(url).testGet().getStringContent();
//		String deviceObjectContent = _testGetJson(url);

		DeviceObject deviceObject = TestUtils.fromJSON(new TypeReference<DeviceObject>() {
		}, deviceObjectContent);

		assertNotNull(deviceObject);

		assertNotNull(deviceObject.getDeviceLoginInfo());
		//deviceObject.set
		deviceObject.getDeviceLoginInfo().setDeviceLogin("login_12345");
		deviceObject.getDeviceLoginInfo().setDevicePassword("pass_" + System.currentTimeMillis());

		deviceObject.getEditDataSourceInfo().setSubscrDataSourceId(65523603L);

        mockMvcRestWrapper.restRequest(url).testPut(deviceObject);
//		_testUpdateJson(url, deviceObject);
	}

    /*

     */
	@Test
	@Ignore
    @Transactional
	public void testDeviceObjectCreateDelete() throws Exception {

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(DEV_RMA_DEVICE_OBJECT_ID);
		deviceObject.setId(null);
		deviceObject.setContObject(null);
		deviceObject.setNumber("Nr:" + System.currentTimeMillis());

		deviceObject.setDeviceModel(null);

		String url = UrlUtils.apiRmaUrl(String.format("/contObjects/%d/deviceObjects", DEV_RMA_CONT_OBJECT_ID));
		RequestExtraInitializer paramInit = (builder) -> {
			builder.param("subscrDataSourceId", String.valueOf(65523603));
			builder.param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis());
		};
        Long deviceObjectId = mockMvcRestWrapper.restRequest(url)
            .requestBuilder(b -> b
                .param("subscrDataSourceId", String.valueOf(65523603))
                .param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis())
            ).testPost(deviceObject).getLastId();
//		Long deviceObjectId = _testCreateJson(url, deviceObject, paramInit);

		logger.info("TESTING GET");

		String getUrl = UrlUtils.apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, deviceObjectId));

        mockMvcRestWrapper.restRequest(getUrl).testGet();
//		_testGetJson(getUrl);

		logger.info("TESTING DELETE");

		String deleteUrl = UrlUtils.apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, deviceObjectId));
		RequestExtraInitializer paramDel = (builder) -> {
			builder.param("isPermanent", "true");
		};
        mockMvcRestWrapper.restRequest(deleteUrl).testDelete();
//		_testDeleteJson(deleteUrl, paramDel);

	}

    /*

     */
	@Test
	@Ignore
    @Transactional
	public void testDeviceObjectAllCreateDelete() throws Exception {

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(DEV_RMA_DEVICE_OBJECT_ID);
		deviceObject.setId(null);
		deviceObject.setContObject(null);
		deviceObject.setNumber("Nr:" + System.currentTimeMillis());
		String url = UrlUtils.apiRmaUrl(String.format("/contObjects/deviceObjects"));
		RequestExtraInitializer paramInit = (builder) -> {
			builder.param("contObjectId", String.valueOf(DEV_RMA_CONT_OBJECT_ID));
			builder.param("subscrDataSourceId", String.valueOf(65523603));
			builder.param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis());
		};
		Long deviceObjectId = mockMvcRestWrapper.restRequest(url)
            .requestBuilder(b -> b
                .param("contObjectId", String.valueOf(DEV_RMA_CONT_OBJECT_ID))
                .param("subscrDataSourceId", String.valueOf(65523603))
                .param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis())
            ).testPost(deviceObject).getLastId();
//            _testCreateJson(url, deviceObject, paramInit);

		String deleteUrl = UrlUtils.apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, deviceObjectId));
		RequestExtraInitializer paramDel = (builder) -> {
			builder.param("isPermanent", "true");
		};
        mockMvcRestWrapper.restRequest(deleteUrl).requestBuilder(b -> b.param("isPermanent", "true")).testDelete();
//		_testDeleteJson(deleteUrl, paramDel);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testAllDeviceObjectsGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/deviceObjects").testGet();
//		_testGetJson(UrlUtils.apiRmaUrl("/contObjects/deviceObjects"));
	}

    /*

     */
	@Test
    @Transactional
	public void testDeviceObjectsLoadingSettingsGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/deviceObjects/{id2}/loadingSettings", 725, 3)
            .testGet();
//		_testGetJson(UrlUtils.apiRmaUrl("/contObjects/%d/deviceObjects/%d/loadingSettings", 725, 3));
	}

    /*

     */
	@Test
    @Transactional
	public void testDeviceObjectsLoadingLogGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/deviceObjects/{id2}/loadingLog", 725, 3).testGet();
//		_testGetJson(UrlUtils.apiRmaUrl("/contObjects/%d/deviceObjects/%d/loadingLog", 725, 3));
	}

    /*

     */
	@Test
    @Transactional
	public void testDeviceObjectsLoadingSettingsPut() throws Exception {
		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(3);
		DeviceObjectLoadingSettings settings = deviceObjectLoadingSettingsService
				.getDeviceObjectLoadingSettings(deviceObject);
		settings.setLoadingAttempts(10);
		settings.setLoadingInterval("12:00");
		settings.setIsLoadingAuto(!Boolean.TRUE.equals(settings.getIsLoadingAuto()));
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id2}/deviceObjects/{id2}/loadingSettings", 725, 3).testPut(settings);
//		_testUpdateJson(UrlUtils.apiRmaUrl("/contObjects/%d/deviceObjects/%d/loadingSettings", 725, 3), settings);
	}

    /*

     */
	@Test
    @Transactional
    @Ignore
	public void testDeviceObjectsSubscrDataSourceLoadingSettingsPut() throws Exception {
		Long deviceObjectId = 65836845L;

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(deviceObjectId);
		assertNotNull(deviceObject.getActiveDataSource());
		SubscrDataSource subscrDataSource = deviceObject.getActiveDataSource().getSubscrDataSource();

		assertNotNull(subscrDataSource);
		SubscrDataSourceLoadingSettings settings = subscrDataSourceLoadingSettingsService
				.getSubscrDataSourceLoadingSettings(subscrDataSource);

		settings.setLoadingAttempts(10);
		settings.setLoadingInterval("12:00");
		settings.setIsLoadingAuto(!Boolean.TRUE.equals(settings.getIsLoadingAuto()));
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/deviceObjects/{id2}/subscrDataSource/loadingSettings", 725, deviceObjectId)
            .testPut(settings);
//		_testUpdateJson(
//            UrlUtils.apiRmaUrl("/contObjects/%d/deviceObjects/%d/subscrDataSource/loadingSettings", 725, deviceObjectId),
//				settings);
	}

	/*

	 */
	@Test
    @Transactional
	public void testDeviceObjectDataSourceGet() throws Exception {
		//65836845
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/deviceObjects/{id2}/subscrDataSource", 725, 65836845).testGet();
//		_testGetJson(UrlUtils.apiSubscrUrl("/contObjects/%d/deviceObjects/%d/subscrDataSource", 725, 65836845));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjectList() throws Exception {
//		String url = UrlUtils.apiRmaUrl(String.format("/contObjects/%d/deviceObjects", DEV_RMA_CONT_OBJECT_ID));
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/deviceObjects", DEV_RMA_CONT_OBJECT_ID).testGet();
//		_testGetJson(url);

	}

	/*
	    TODO access denied
	 */
	@Ignore
	@Test
    @Transactional
	public void testDeviceObjectLastInfo() throws Exception {
//		String url = UrlUtils.apiRmaUrl(String.format("/contObjects/%d/deviceObjects/%d", 512136083, 512136235));
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/deviceObjects/{id1}", 512136083, 512136235).testGet();
//		String deviceObjectContent = _testGetJson(url);

	}

	/*
	    TODO access denied
	 */
	@Ignore
	@Test
    @Transactional
	public void testDeviceModelUpdate() throws Exception {


		String response = mockMvcRestWrapper.restRequest("/api/rma/deviceObjects/deviceModels").testGet().getStringContent();
//		String response = _testGetJson(UrlUtils.apiRmaUrl("/deviceObjects/deviceModels"));

		List<DeviceModel> deviceModels = TestUtils.fromJSON(new TypeReference<List<DeviceModel>>() {
		}, response);

		if (!deviceModels.isEmpty()) {
			DeviceModel deviceModel = deviceModels.get(0);
			deviceModel.getDeviceDataTypes().add(DeviceModelType.WATER.name());
            mockMvcRestWrapper.restRequest("/deviceObjects/deviceModels/{id1}", deviceModel.getId()).testPut(deviceModel);
//			_testUpdateJson(UrlUtils.apiRmaUrl("/deviceObjects/deviceModels/" + deviceModel.getId()), deviceModel);

		}

	}

    /*

     */
	@Ignore
	@Test
    @Transactional
	public void testDeviceModelCreate() throws Exception {

		DeviceModel deviceModel = new DeviceModel();
		deviceModel.setModelName("TEST AK");
		deviceModel.getDeviceDataTypes().add(DeviceModelType.WATER.name());
        mockMvcRestWrapper.restRequest("api/rma/deviceObjects/deviceModels").testPost(deviceModel);
//		_testCreateJson(UrlUtils.apiRmaUrl("/deviceObjects/deviceModels"), deviceModel);

	}

}
