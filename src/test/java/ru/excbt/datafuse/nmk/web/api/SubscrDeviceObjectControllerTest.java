package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrDeviceObjectControllerTest extends AnyControllerTest {

	private final static Long DEV_CONT_OBJECT = 733L;
	private final static Long DEV_DEVICE_OBJECT = 54209288L;

	@Autowired
	private DeviceObjectService deviceObjectService;

	@Test
	public void testGetDeviceObjects() throws Exception {
		String url = apiSubscrUrl(String.format("/contObjects/%d/deviceObjects", DEV_CONT_OBJECT));
		_testJsonGet(url);
	}

	@Test
	public void testGetDeviceObjectMetaDataVzlet() throws Exception {
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
	public void testGetDeviceObjectsVzletSystem() throws Exception {
		String url = apiSubscrUrl("/deviceObjects/metaVzlet/system");
		_testJsonGet(url);
	}

	@Test
	public void testGetDeviceObjects725() throws Exception {
		String url = apiSubscrUrl(String.format("/contObjects/%d/deviceObjects", 725));
		_testJsonGet(url);
	}

	@Test
	public void testGetDeviceObjects725_737() throws Exception {
		String url = apiSubscrUrl(String.format("/contObjects/%d/deviceObjects/%d", 725, 737));
		_testJsonGet(url);
	}

	@Test
	public void testGetDeviceModels() throws Exception {
		_testJsonGet(apiSubscrUrl("/deviceObjects/deviceModels"));
	}

}
