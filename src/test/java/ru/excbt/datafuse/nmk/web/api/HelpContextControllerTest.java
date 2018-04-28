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
import ru.excbt.datafuse.nmk.data.service.HelpContextService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class HelpContextControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private HelpContextController helpContextController;

    @Autowired
    private HelpContextService helpContextService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        helpContextController = new HelpContextController(helpContextService);

        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(helpContextController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalContObjectMockMvc);
    }

	@Test
    @Transactional
	public void testRedirect() throws Exception {
		mockMvcRestWrapper.restRequest("/api/help/jmp/home01").testGetAndReturn()
            .andExpect(status().is3xxRedirection());
	}

	@Test
    @Transactional
	public void testSetup() throws Exception {
        helpContextService.createByAnchorId("home01");
        mockMvcRestWrapper.restRequest("/api/help/setup/home01").testGet();
	}

	@Test
    @Transactional
	public void testHelpInfo() throws Exception {
        helpContextService.createByAnchorId("home01");
        mockMvcRestWrapper.restRequest("/api/help/info/home01").testGet();
	}
}
