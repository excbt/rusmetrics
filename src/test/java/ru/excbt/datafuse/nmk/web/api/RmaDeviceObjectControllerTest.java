package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
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

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectUpdate() throws Exception {
		DeviceObject deviceObject = deviceObjectService.findOne(DEV_RMA_DEVICE_OBJECT_ID);
		deviceObject.setContObject(null);
		deviceObject.setNumber("Nr:" + System.currentTimeMillis());
		String url = apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, DEV_RMA_DEVICE_OBJECT_ID));
		RequestExtraInitializer paramInit = (builder) -> {
			builder.param("subscrDataSourceId", String.valueOf(65523603));
			builder.param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis());
			builder.param("dataSourceTable", "Table:" + System.currentTimeMillis());
			builder.param("dataSourceTable1h", "Table 1H:" + System.currentTimeMillis());
			builder.param("dataSourceTable24h", "Table 24H:" + System.currentTimeMillis());
		};
		_testJsonUpdate(url, deviceObject, paramInit);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectCreateDelete() throws Exception {

		DeviceObject deviceObject = deviceObjectService.findOne(DEV_RMA_DEVICE_OBJECT_ID);
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
		Long deviceObjectId = _testJsonCreate(url, deviceObject, paramInit);

		logger.info("TESTING GET");

		String getUrl = apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, deviceObjectId));

		_testJsonGet(getUrl);

		logger.info("TESTING DELETE");

		String deleteUrl = apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, deviceObjectId));
		RequestExtraInitializer paramDel = (builder) -> {
			builder.param("isPermanent", "true");
		};
		_testJsonDelete(deleteUrl, paramDel);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectAllCreateDelete() throws Exception {

		DeviceObject deviceObject = deviceObjectService.findOne(DEV_RMA_DEVICE_OBJECT_ID);
		deviceObject.setId(null);
		deviceObject.setContObject(null);
		deviceObject.setNumber("Nr:" + System.currentTimeMillis());
		String url = apiRmaUrl(String.format("/contObjects/deviceObjects"));
		RequestExtraInitializer paramInit = (builder) -> {
			builder.param("contObjectId", String.valueOf(DEV_RMA_CONT_OBJECT_ID));
			builder.param("subscrDataSourceId", String.valueOf(65523603));
			builder.param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis());
		};
		Long deviceObjectId = _testJsonCreate(url, deviceObject, paramInit);

		String deleteUrl = apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, deviceObjectId));
		RequestExtraInitializer paramDel = (builder) -> {
			builder.param("isPermanent", "true");
		};
		_testJsonDelete(deleteUrl, paramDel);

	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAllDeviceObjectsGet() throws Exception {
		_testJsonGet(apiRmaUrl("/contObjects/deviceObjects"));
	}

}
