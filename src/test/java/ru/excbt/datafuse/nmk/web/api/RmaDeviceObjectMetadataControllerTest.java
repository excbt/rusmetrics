package ru.excbt.datafuse.nmk.web.api;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetadata;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectMetadataService;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.RequestExtraInitializer;
import ru.excbt.datafuse.nmk.web.RmaControllerTest;

import javax.transaction.Transactional;

public class RmaDeviceObjectMetadataControllerTest extends RmaControllerTest {

	private final static long DEV_RMA_DEVICE_OBJECT_ID = 737;
	private final static long DEV_RMA_CONT_OBJECT_ID = 725;

	//private final static long DEV_DEVICE_OBJECT_ID = 65836845;
	private final static long DEV_DEVICE_OBJECT_ID = 3;
	private final static long DEV_CONT_OBJECT_ID = 725;

	@Autowired
	private DeviceObjectMetadataService deviceObjectMetadataService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testMeasureUnitGet() throws Exception {
		_testGetJson(UrlUtils.apiRmaUrl("/contObjects/deviceObjects/metadata/measureUnits"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testMeasureUnitSameGet() throws Exception {
		RequestExtraInitializer param = (builder) -> {
			builder.param("measureUnit", "p_mpa");
		};

		_testGet(UrlUtils.apiRmaUrl("/contObjects/deviceObjects/metadata/measureUnits"), param);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testContServiceTypesGet() throws Exception {
        _testGetJson(UrlUtils.apiRmaUrl("/contObjects/deviceObjects/metadata/contServiceTypes"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjectMetadataGet() throws Exception {
		String url = UrlUtils.apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d/metadata", DEV_CONT_OBJECT_ID, DEV_DEVICE_OBJECT_ID));
		_testGetJson(url);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjectMetadataUpdate() throws Exception {
		List<DeviceObjectMetadata> metadata = deviceObjectMetadataService
				.selectDeviceObjectMetadata(DEV_DEVICE_OBJECT_ID);

		metadata.forEach(i -> {
			i.setMetaComment("Comment by REST :" + System.currentTimeMillis());
		});

		String url = UrlUtils.apiRmaUrl(
				String.format("/contObjects/%d/deviceObjects/%d/metadata", DEV_CONT_OBJECT_ID, DEV_DEVICE_OBJECT_ID));
		_testUpdateJson(url, metadata);
	}

	@Test
    @Transactional
	public void testDeviceObjectMetadataByContObject() throws Exception {
		_testGetJson("/api/rma/contObjects/725/deviceObjects/byContZPoint/512084866/metadata");
		///contObjects/{contObjectId}/deviceObjects/byContZPoint/{contZPointId}/metadata
	}

}
