package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.RawModemModel;
import ru.excbt.datafuse.nmk.data.model.SubscrDataSource;
import ru.excbt.datafuse.nmk.data.model.types.ExSystemKey;
import ru.excbt.datafuse.nmk.data.repository.keyname.DataSourceTypeRepository;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.RawModemService;
import ru.excbt.datafuse.nmk.data.service.SubscrDataSourceService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.SubscrDataSourceResource;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
public class RmaDataSourceControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SubscrDataSourceResource subscrDataSourceResource;
    @Autowired
    private SubscrDataSourceService subscrDataSourceService;
    @Autowired
    private DataSourceTypeRepository dataSourceRepository;
    @Autowired
    private RawModemService rawModemService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrDataSourceResource = new SubscrDataSourceResource(subscrDataSourceService, dataSourceRepository, rawModemService, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrDataSourceResource)
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
	public void testDataSourcesGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/dataSources").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testDataSourceTypesGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/dataSourceTypes").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testDataSourceCreateUpdateDelete() throws Exception {
		SubscrDataSource dataSource = new SubscrDataSource();
		dataSource.setDataSourceTypeKey(ExSystemKey.DEVICE.getKeyname());

        Long dataSourceId = mockMvcRestWrapper.restRequest("/api/rma/dataSources").testPost(dataSource).getLastId();

		assertNotNull(dataSourceId);

        String dataSourceContent = mockMvcRestWrapper.restRequest("/api/rma/dataSources/", dataSourceId).testGet().getStringContent();

		dataSource = TestUtils.fromJSON(new TypeReference<SubscrDataSource>() {
		}, dataSourceContent);

		dataSource.setRawTimeout(10);

		dataSource.setDataSourceComment("DataSource CRUD test at " + System.currentTimeMillis());

        mockMvcRestWrapper.restRequest("/api/rma/dataSources/{id}", dataSource.getId())
            .testPut(dataSource)
            .testDelete();

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testRawModelModels() throws Exception {

        String content = mockMvcRestWrapper.restRequest("/api/rma/dataSources/rawModemModels").testGet().getStringContent();

		List<RawModemModel> result = TestUtils.fromJSON(new TypeReference<List<RawModemModel>>() {
		}, content);

		assertNotNull(result);

	}

	// TODO access denied
	@Ignore
	@Test
    @Transactional
	public void testCreateModel() throws Exception {
		RawModemModel newModel = new RawModemModel();
		newModel.setRawModemType("GPRS-MODEM");
		newModel.setRawModemModelName("Модель для теста + ");
		newModel.setIsDialup(true);
		newModel.setDevComment("Created by REST");

        Long id = mockMvcRestWrapper.restRequest("/api/rma/dataSources/rawModemModels").testPost(newModel).getLastId();

        String content = mockMvcRestWrapper.restRequest("/api/rma/dataSources/rawModemModels/{id}", id).testGet().getStringContent();

		RawModemModel result = TestUtils.fromJSON(new TypeReference<RawModemModel>() {
		}, content);

		assertNotNull(result);

		result.setDevComment("Edited By REST");
        mockMvcRestWrapper.restRequest("/api/rma/dataSources/rawModemModels/{id}", id)
            .testPut(result)
            .testDelete();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testModemModelIdentity() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/dataSources/rawModemModels/rawModemModelIdentity").testGet();

	}

}
