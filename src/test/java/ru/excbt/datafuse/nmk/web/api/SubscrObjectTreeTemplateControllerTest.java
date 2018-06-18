package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.core.type.TypeReference;
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
import ru.excbt.datafuse.nmk.data.model.SubscrObjectTreeTemplate;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrObjectTreeTemplateService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.List;

@RunWith(SpringRunner.class)
public class SubscrObjectTreeTemplateControllerTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private SubscrObjectTreeTemplateController subscrObjectTreeTemplateController;
    @Autowired
    private SubscrObjectTreeTemplateService subscrObjectTreeTemplateService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrObjectTreeTemplateController = new SubscrObjectTreeTemplateController(subscrObjectTreeTemplateService, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrObjectTreeTemplateController)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	@Test
	public void testRmaSubscrObjectTreeTemplates() throws Exception {
        String content = mockMvcRestWrapper.restRequest("/api/subscr/subscrObjectTreeTemplates").testGet().getStringContent();

		List<SubscrObjectTreeTemplate> templates = TestUtils.fromJSON(new TypeReference<List<SubscrObjectTreeTemplate>>() {
		}, content);

		for (SubscrObjectTreeTemplate i : templates) {
            mockMvcRestWrapper.restRequest("/api/subscr/subscrObjectTreeTemplates/{id}/items", i.getId()).testGet();
		}

	}
}
