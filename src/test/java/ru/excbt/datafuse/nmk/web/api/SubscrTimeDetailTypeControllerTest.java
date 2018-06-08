package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
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
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.TimeDetailTypeService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
public class SubscrTimeDetailTypeControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SubscrTimeDetailTypeController subscrTimeDetailTypeController;
    @Autowired
    private TimeDetailTypeService timeDetailTypeService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrTimeDetailTypeController = new SubscrTimeDetailTypeController(timeDetailTypeService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrTimeDetailTypeController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testTimeDetailType1h24h() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/timeDetailType/1h24h").testGet();
//		_testGetJson(UrlUtils.apiSubscrUrl("/timeDetailType/1h24h"));
	}
}
