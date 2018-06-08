package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.service.ContEventService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.ApiConst;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class SubscrContEventTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;


    private SubscrContEventController subscrContEventController;

    @Autowired
    private ContEventService contEventService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrContEventController = new SubscrContEventController(contEventService, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrContEventController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	@Test
	@Ignore
	public void testSubscrContObjectEvents() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contObjects/events").testGet();
//		_testGetJson("/api/subscr/contObjects/events");
	}

	@Test
	@Ignore
	public void testSubscrContObjectEventsFilter() throws Exception {

		ResultActions resultActionsAll = restPortalMockMvc.perform(get("/api/subscr/contObjects/eventsFilter")
				.with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk())
				.andExpect(content().contentType(ApiConst.APPLICATION_JSON_UTF8));
	}

	@Test
	public void testSubscrContObjectEventsFilterDate() throws Exception {

		ResultActions resultActionsAll = restPortalMockMvc
				.perform(get("/api/subscr/contObjects/eventsFilter").param("startDate", "2015-04-01")
						.param("endDate", "2015-05-01").with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk())
				.andExpect(content().contentType(ApiConst.APPLICATION_JSON_UTF8));
	}

	@Test
	public void testSubscrContObjectEventsFilterDateContObjects() throws Exception {

		ResultActions resultActionsAll = restPortalMockMvc.perform(get("/api/subscr/contObjects/eventsFilter")
				.param("startDate", "2015-04-01").param("endDate", "2015-05-01").param("contObjectIds", "20118671")
				.with(testSecurityContext()).accept(MediaType.APPLICATION_JSON));

		resultActionsAll.andDo(MockMvcResultHandlers.print());

		resultActionsAll.andExpect(status().isOk())
				.andExpect(content().contentType(ApiConst.APPLICATION_JSON_UTF8));
	}

	@Test
	@Ignore
	public void testSubscrContObjectEventsPaged() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/contObjects/eventsFilterPaged?page=0&size=20").testGet();
//		_testGetJson("/api/subscr/contObjects/eventsFilterPaged?page=0&size=20");
	}

}
