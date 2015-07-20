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
		String url = apiSubscrUrl(String.format(
				"/contObjects/%d/deviceObjects", DEV_CONT_OBJECT));
		testJsonGet(url);
	}

	@Test
	public void testGetDeviceObjectMetaDataVzlet() throws Exception {
		String url = apiSubscrUrl(String.format(
				"/contObjects/%d/deviceObjects/%d/metaVzlet", DEV_CONT_OBJECT,
				DEV_DEVICE_OBJECT));
		testJsonGetNoContentCheck(url);
	}

	@Test
	public void testDeviceObjectMetaVzletCRUD() throws Exception {
		deviceObjectService.deleteDeviceObjectMetaVzlet(DEV_DEVICE_OBJECT);

		DeviceObjectMetaVzlet metaVzlet = new DeviceObjectMetaVzlet();
		metaVzlet.setVzletTableDay("Day XXX");
		metaVzlet.setVzletTableHour("Hour XXX");

		String url = apiSubscrUrl(String.format(
				"/contObjects/%d/deviceObjects/%d/metaVzlet", DEV_CONT_OBJECT,
				DEV_DEVICE_OBJECT));

		Long metaId = testJsonCreate(url, metaVzlet);

		metaVzlet.setId(metaId);
		metaVzlet.setVzletTableDay("Day YYY");
		metaVzlet.setVzletTableHour("Hour YYY");

		testJsonUpdate(url, metaVzlet);

		testJsonDelete(url);
	}

}
