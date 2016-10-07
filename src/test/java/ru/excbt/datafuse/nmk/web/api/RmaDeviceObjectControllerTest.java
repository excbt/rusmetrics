package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;

import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectLoadingSettings;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSourceLoadingSettings;
import ru.excbt.datafuse.nmk.data.model.types.DeviceModelType;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectLoadingSettingsService;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscrDataSourceLoadingSettingsService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

public class RmaDeviceObjectControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(RmaDeviceObjectControllerTest.class);

	private final static Long DEV_CONT_OBJECT = 733L;
	private final static Long DEV_DEVICE_OBJECT = 54209288L;

	private final static long DEV_RMA_DEVICE_OBJECT_ID = 737;
	private final static long DEV_RMA_CONT_OBJECT_ID = 725;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Autowired
	private DeviceObjectLoadingSettingsService deviceObjectLoadingSettingsService;

	@Autowired
	private SubscrDataSourceLoadingSettingsService subscrDataSourceLoadingSettingsService;

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDeviceObjectUpdate() throws Exception {
		String url = apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, DEV_RMA_DEVICE_OBJECT_ID));

		String deviceObjectContent = _testGetJson(url);

		DeviceObject deviceObject = fromJSON(new TypeReference<DeviceObject>() {
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

		RequestExtraInitializer paramInit = (builder) -> {
			builder.param("subscrDataSourceId", String.valueOf(65523603));
			builder.param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis());
			builder.param("dataSourceTable", "Table:" + System.currentTimeMillis());
			builder.param("dataSourceTable1h", "Table 1H:" + System.currentTimeMillis());
			builder.param("dataSourceTable24h", "Table 24H:" + System.currentTimeMillis());
		};
		_testUpdateJson(url, deviceObject, paramInit);
	}

	@Test
	//@Ignore
	public void testDeviceObjectLoginInfoUpdate() throws Exception {
		String url = apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, DEV_RMA_DEVICE_OBJECT_ID));

		String deviceObjectContent = _testGetJson(url);

		DeviceObject deviceObject = fromJSON(new TypeReference<DeviceObject>() {
		}, deviceObjectContent);

		assertNotNull(deviceObject);

		assertNotNull(deviceObject.getDeviceLoginInfo());
		//deviceObject.set
		deviceObject.getDeviceLoginInfo().setDeviceLogin("login_12345");
		deviceObject.getDeviceLoginInfo().setDevicePassword("pass_" + System.currentTimeMillis());

		deviceObject.getEditDataSourceInfo().setSubscrDataSourceId(65523603L);

		_testUpdateJson(url, deviceObject);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDeviceObjectCreateDelete() throws Exception {

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(DEV_RMA_DEVICE_OBJECT_ID);
		deviceObject.setId(null);
		deviceObject.setContObject(null);
		deviceObject.setNumber("Nr:" + System.currentTimeMillis());

		deviceObject.setDeviceModel(null);
		deviceObject.getDeviceObjectDataSources().clear();

		String url = apiRmaUrl(String.format("/contObjects/%d/deviceObjects", DEV_RMA_CONT_OBJECT_ID));
		RequestExtraInitializer paramInit = (builder) -> {
			builder.param("subscrDataSourceId", String.valueOf(65523603));
			builder.param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis());
		};
		Long deviceObjectId = _testCreateJson(url, deviceObject, paramInit);

		logger.info("TESTING GET");

		String getUrl = apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, deviceObjectId));

		_testGetJson(getUrl);

		logger.info("TESTING DELETE");

		String deleteUrl = apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, deviceObjectId));
		RequestExtraInitializer paramDel = (builder) -> {
			builder.param("isPermanent", "true");
		};
		_testDeleteJson(deleteUrl, paramDel);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDeviceObjectAllCreateDelete() throws Exception {

		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(DEV_RMA_DEVICE_OBJECT_ID);
		deviceObject.setId(null);
		deviceObject.setContObject(null);
		deviceObject.setNumber("Nr:" + System.currentTimeMillis());
		String url = apiRmaUrl(String.format("/contObjects/deviceObjects"));
		RequestExtraInitializer paramInit = (builder) -> {
			builder.param("contObjectId", String.valueOf(DEV_RMA_CONT_OBJECT_ID));
			builder.param("subscrDataSourceId", String.valueOf(65523603));
			builder.param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis());
		};
		Long deviceObjectId = _testCreateJson(url, deviceObject, paramInit);

		String deleteUrl = apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, deviceObjectId));
		RequestExtraInitializer paramDel = (builder) -> {
			builder.param("isPermanent", "true");
		};
		_testDeleteJson(deleteUrl, paramDel);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllDeviceObjectsGet() throws Exception {
		_testGetJson(apiRmaUrl("/contObjects/deviceObjects"));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectsLoadingSettingsGet() throws Exception {
		_testGetJson(apiRmaUrl("/contObjects/%d/deviceObjects/%d/loadingSettings", 725, 3));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectsLoadingLogGet() throws Exception {
		_testGetJson(apiRmaUrl("/contObjects/%d/deviceObjects/%d/loadingLog", 725, 3));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectsLoadingSettingsPut() throws Exception {
		DeviceObject deviceObject = deviceObjectService.selectDeviceObject(3);
		DeviceObjectLoadingSettings settings = deviceObjectLoadingSettingsService
				.getDeviceObjectLoadingSettings(deviceObject);
		settings.setLoadingAttempts(10);
		settings.setLoadingInterval("12:00");
		settings.setIsLoadingAuto(!Boolean.TRUE.equals(settings.getIsLoadingAuto()));
		_testUpdateJson(apiRmaUrl("/contObjects/%d/deviceObjects/%d/loadingSettings", 725, 3), settings);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
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
		_testUpdateJson(
				apiRmaUrl("/contObjects/%d/deviceObjects/%d/subscrDataSource/loadingSettings", 725, deviceObjectId),
				settings);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectDataSourceGet() throws Exception {
		//65836845
		_testGetJson(apiSubscrUrl("/contObjects/%d/deviceObjects/%d/subscrDataSource", 725, 65836845));
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectList() throws Exception {
		String url = apiRmaUrl(String.format("/contObjects/%d/deviceObjects", DEV_RMA_CONT_OBJECT_ID));

		_testGetJson(url);

	}

	@Test
	public void testDeviceObjectLastInfo() throws Exception {
		String url = apiRmaUrl(String.format("/contObjects/%d/deviceObjects/%d", 512136083, 512136235));

		String deviceObjectContent = _testGetJson(url);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceModelUpdate() throws Exception {

		String response = _testGetJson(apiRmaUrl("/deviceObjects/deviceModels"));

		List<DeviceModel> deviceModels = fromJSON(new TypeReference<List<DeviceModel>>() {
		}, response);

		if (!deviceModels.isEmpty()) {
			DeviceModel deviceModel = deviceModels.get(0);
			deviceModel.getDeviceModelTypes().add(DeviceModelType.WATER.name());

			_testUpdateJson(apiRmaUrl("/deviceObjects/deviceModels/" + deviceModel.getId()), deviceModel);

		}

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testDeviceModelCreate() throws Exception {

		DeviceModel deviceModel = new DeviceModel();
		deviceModel.setModelName("TEST AK");
		deviceModel.getDeviceModelTypes().add(DeviceModelType.WATER.name());

		_testCreateJson(apiRmaUrl("/deviceObjects/deviceModels"), deviceModel);

	}

}
