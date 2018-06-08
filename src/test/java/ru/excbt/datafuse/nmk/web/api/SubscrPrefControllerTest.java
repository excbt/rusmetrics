package ru.excbt.datafuse.nmk.web.api;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.excbt.datafuse.nmk.data.model.SubscrPrefValue;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.SubscrObjectTreeService;
import ru.excbt.datafuse.nmk.data.service.SubscrPrefService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
public class SubscrPrefControllerTest extends PortalApiTest {


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SubscrPrefController subscrPrefController;
    @Autowired
    private SubscrPrefService subscrPrefService;
    @Autowired
    private SubscrObjectTreeService subscrObjectTreeService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrPrefController = new SubscrPrefController(subscrPrefService, subscrObjectTreeService, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrPrefController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrPrefValuesGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/subscrPrefValues").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrPrefValueGet() throws Exception {
		Consumer<MockHttpServletRequestBuilder> params = (builder) -> {
			builder.param("subscrPrefKeyname", "SUBSCR_OBJECT_TREE_CONT_OBJECTS");
		};
        mockMvcRestWrapper.restRequest("/api/subscr/subscrPrefValue")
            .requestBuilder(params)
            .testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrPrefValuesSave() throws Exception {
		String prefValuesContent = mockMvcRestWrapper.restRequest("/api/subscr/subscrPrefValues")
            .testGet().getStringContent();


		List<SubscrPrefValue> prefValues = TestUtils.fromJSON(new TypeReference<List<SubscrPrefValue>>() {
		}, prefValuesContent);

		assertNotNull(prefValues);

		for (SubscrPrefValue v : prefValues) {
			v.setValue("value_" + System.currentTimeMillis());
			v.setSubscrPref(null);
			v.setDevComment("Deleted By Rest");
		}

        mockMvcRestWrapper.restRequest("/api/subscr/subscrPrefValues").testPut(prefValues);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSubscrPrefObjectTreeType() throws Exception {
		Consumer<MockHttpServletRequestBuilder> params = (builder) -> {
			builder.param("subscrPrefKeyname", "SUBSCR_OBJECT_TREE_CONT_OBJECTS");
		};
        mockMvcRestWrapper.restRequest("/api/subscr/subscrPrefValues/objectTreeTypes")
            .requestBuilder(params)
            .testGet();
	}

}
