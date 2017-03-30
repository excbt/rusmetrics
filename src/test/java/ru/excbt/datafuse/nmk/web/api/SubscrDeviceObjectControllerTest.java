package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;
import ru.excbt.datafuse.nmk.data.model.dmo.DeviceObjectDMO;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectService;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;

import javax.transaction.Transactional;

import static org.junit.Assert.assertTrue;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
public class SubscrDeviceObjectControllerTest extends AnyControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrDeviceObjectControllerTest.class);

	private final static Long DEV_CONT_OBJECT = 733L;
	private final static Long DEV_DEVICE_OBJECT = 54209288L;

	private final static long DEV_RMA_DEVICE_OBJECT_ID = 737;
	private final static long DEV_RMA_CONT_OBJECT_ID = 725;

	@Autowired
	private DeviceObjectService deviceObjectService;

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
        DeviceObjectDMO dmo = deviceObjectService.convert(deviceObjectDTO);
        TestUtils.objectToJson(dmo);
        deviceObjectDTO.createDeviceLoginIngo();
        deviceObjectDTO.getDeviceLoginInfo().setDeviceLogin("user");
        deviceObjectDTO.getDeviceLoginInfo().setDevicePassword("pass");
	    deviceObjectDTO.setIsTimeSyncEnabled(true);
	    assertTrue(deviceObjectDTO.getEditDataSourceInfo() != null);
	    assertTrue(deviceObjectDTO.getEditDataSourceInfo().getSubscrDataSourceId() != null);
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
		String url = UrlUtils.apiSubscrUrl(
				String.format("/contObjects/%d/deviceObjects/%d/metaVzlet", DEV_CONT_OBJECT, DEV_DEVICE_OBJECT));
		_testGetSuccessful(url);
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

}
