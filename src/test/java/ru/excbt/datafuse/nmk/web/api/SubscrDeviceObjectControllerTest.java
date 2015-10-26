package ru.excbt.datafuse.nmk.web.api;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

public class SubscrDeviceObjectControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrDeviceObjectControllerTest.class);

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

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectMetaDataVzletGet() throws Exception {
		String url = apiSubscrUrl(
				String.format("/contObjects/%d/deviceObjects/%d/metaVzlet", DEV_CONT_OBJECT, DEV_DEVICE_OBJECT));
		_testJsonGetNoJsonCheck(url);
	}

	/**
	 * 
	 * @throws Exception
	 */
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

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjectsVzletSystemGet() throws Exception {
		String url = apiSubscrUrl("/deviceObjects/metaVzlet/system");
		_testJsonGet(url);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjects725Get() throws Exception {
		String url = apiSubscrUrl(String.format("/contObjects/%d/deviceObjects", 725));
		_testJsonGet(url);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceObjects725_737Get() throws Exception {
		String url = apiSubscrUrl(String.format("/contObjects/%d/deviceObjects/%d", 725, 737));
		_testJsonGet(url);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDeviceModelsGet() throws Exception {
		_testJsonGet(apiSubscrUrl("/deviceObjects/deviceModels"));
	}

}
