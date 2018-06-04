package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetadata;
import ru.excbt.datafuse.nmk.data.service.DeviceObjectMetadataService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;


@RunWith(SpringRunner.class)
public class RmaDeviceObjectMetadataControllerTest extends PortalApiTest {

	private final static long DEV_RMA_DEVICE_OBJECT_ID = 737;
	private final static long DEV_RMA_CONT_OBJECT_ID = 725;

	//private final static long DEV_DEVICE_OBJECT_ID = 65836845;
	private final static long DEV_DEVICE_OBJECT_ID = 3;
	private final static long DEV_CONT_OBJECT_ID = 725;


	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@MockBean
	private PortalUserIdsService portalUserIdsService;

	@Autowired
	private RmaDeviceObjectMetadataController rmaDeviceObjectMetadataController;

	@Autowired
	private DeviceObjectMetadataService deviceObjectMetadataService;

    private MockMvcRestWrapper mockMvcRestWrapper;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(rmaDeviceObjectMetadataController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testMeasureUnitGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/deviceObjects/metadata/measureUnits").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testMeasureUnitSameGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/deviceObjects/metadata/measureUnits")
            .requestBuilder(b-> b.param("measureUnit", "p_mpa"))
            .testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testContServiceTypesGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/deviceObjects/metadata/contServiceTypes").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDeviceObjectMetadataGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/deviceObjects/{id2}/metadata", DEV_CONT_OBJECT_ID, DEV_DEVICE_OBJECT_ID).testGet();
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

        mockMvcRestWrapper.restRequest("/api/rma/contObjects/{id1}/deviceObjects/{id2}/metadata", DEV_CONT_OBJECT_ID, DEV_DEVICE_OBJECT_ID).testPut(metadata);
	}

	@Test
    @Transactional
	public void testDeviceObjectMetadataByContObject() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/contObjects/725/deviceObjects/byContZPoint/{id1}/metadata", 512084866).testGet();
	}

}
