package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;

public class SubscrDeviceObjectControllerTest extends AnyControllerTest {

	private final static Long DEV_CONT_OBJECT = 733L;
	private final static Long DEV_DEVICE_OBJECT = 54209288L;

	private final static long DEV_RMA_DEVICE_OBJECT_ID = 737;
	private final static long DEV_RMA_CONT_OBJECT_ID = 725;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Test
	public void testDeviceObjectsGet() throws Exception {
		String url = apiSubscrUrl(String.format("/contObjects/%d/deviceObjects", DEV_CONT_OBJECT));
		_testJsonGet(url);
	}

	@Test
	public void testDeviceObjectMetaDataVzletGet() throws Exception {
		String url = apiSubscrUrl(
				String.format("/contObjects/%d/deviceObjects/%d/metaVzlet", DEV_CONT_OBJECT, DEV_DEVICE_OBJECT));
		_testJsonGetNoJsonCheck(url);
	}

	@Test
	public void testDeviceObjectMetaVzletCRUD() throws Exception {
		deviceObjectService.deleteDeviceObjectMetaVzlet(DEV_DEVICE_OBJECT);

		DeviceObjectMetaVzlet metaVzlet = new DeviceObjectMetaVzlet();
		metaVzlet.setVzletTableDay("Day XXX");
		metaVzlet.setVzletTableHour("Hour XXX");

		String url = apiSubscrUrl(
				String.format("/contObjects/%d/deviceObjects/%d/metaVzlet", DEV_CONT_OBJECT, DEV_DEVICE_OBJECT));

		Long metaId = _testJsonCreate(url, metaVzlet);

		metaVzlet.setId(metaId);
		metaVzlet.setVzletTableDay("Day YYY");
		metaVzlet.setVzletTableHour("Hour YYY");

		_testJsonUpdate(url, metaVzlet);

		_testJsonDelete(url);
	}

	@Test
	public void testDeviceObjectsVzletSystemGet() throws Exception {
		String url = apiSubscrUrl("/deviceObjects/metaVzlet/system");
		_testJsonGet(url);
	}

	@Test
	public void testDeviceObjects725Get() throws Exception {
		String url = apiSubscrUrl(String.format("/contObjects/%d/deviceObjects", 725));
		_testJsonGet(url);
	}

	@Test
	public void testDeviceObjects725_737Get() throws Exception {
		String url = apiSubscrUrl(String.format("/contObjects/%d/deviceObjects/%d", 725, 737));
		_testJsonGet(url);
	}

	@Test
	public void testDeviceModelsGet() throws Exception {
		_testJsonGet(apiSubscrUrl("/deviceObjects/deviceModels"));
	}

	@Test
	public void testAllDeviceObjectsGet() throws Exception {
		_testJsonGet(apiSubscrUrl("/contObjects/deviceObjects"));
	}

	@Test
	public void testDeviceObjectUpdate() throws Exception {
		DeviceObject deviceObject = deviceObjectService.findOne(DEV_RMA_DEVICE_OBJECT_ID);
		deviceObject.setContObject(null);
		deviceObject.setNumber("Nr:" + System.currentTimeMillis());
		String url = apiSubscrUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, DEV_RMA_DEVICE_OBJECT_ID));
		RequestExtraInitializer paramInit = (builder) -> {
			builder.param("subscrDataSourceId", String.valueOf(65523603));
			builder.param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis());
		};
		_testJsonUpdate(url, deviceObject, paramInit);
	}

	@Test
	public void testDeviceObjectCreateDelete() throws Exception {

		DeviceObject deviceObject = deviceObjectService.findOne(DEV_RMA_DEVICE_OBJECT_ID);
		deviceObject.setId(null);
		deviceObject.setContObject(null);
		deviceObject.setNumber("Nr:" + System.currentTimeMillis());
		String url = apiSubscrUrl(String.format("/contObjects/%d/deviceObjects", DEV_RMA_CONT_OBJECT_ID));
		RequestExtraInitializer paramInit = (builder) -> {
			builder.param("subscrDataSourceId", String.valueOf(65523603));
			builder.param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis());
		};
		Long deviceObjectId = _testJsonCreate(url, deviceObject, paramInit);

		String deleteUrl = apiSubscrUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, deviceObjectId));
		RequestExtraInitializer paramDel = (builder) -> {
			builder.param("isPermanent", "true");
		};
		_testJsonDelete(deleteUrl, paramDel);

	}

	@Test
	public void testDeviceObjectAllCreateDelete() throws Exception {

		DeviceObject deviceObject = deviceObjectService.findOne(DEV_RMA_DEVICE_OBJECT_ID);
		deviceObject.setId(null);
		deviceObject.setContObject(null);
		deviceObject.setNumber("Nr:" + System.currentTimeMillis());
		String url = apiSubscrUrl(String.format("/contObjects/deviceObjects"));
		RequestExtraInitializer paramInit = (builder) -> {
			builder.param("contObjectId", String.valueOf(DEV_RMA_CONT_OBJECT_ID));
			builder.param("subscrDataSourceId", String.valueOf(65523603));
			builder.param("subscrDataSourceAddr", "Addr:" + System.currentTimeMillis());
		};
		Long deviceObjectId = _testJsonCreate(url, deviceObject, paramInit);

		String deleteUrl = apiSubscrUrl(
				String.format("/contObjects/%d/deviceObjects/%d", DEV_RMA_CONT_OBJECT_ID, deviceObjectId));
		RequestExtraInitializer paramDel = (builder) -> {
			builder.param("isPermanent", "true");
		};
		_testJsonDelete(deleteUrl, paramDel);

	}
}
