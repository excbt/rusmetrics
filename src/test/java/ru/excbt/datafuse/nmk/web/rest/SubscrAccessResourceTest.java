package ru.excbt.datafuse.nmk.web.rest;

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
import ru.excbt.datafuse.nmk.data.repository.ContObjectAccessRepository;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.ContObjectAccessService;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectAccessMapper;
import ru.excbt.datafuse.nmk.service.mapper.ContObjectMapper;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

@RunWith(SpringRunner.class)
public class SubscrAccessResourceTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private SubscrAccessResource subscrAccessResource;
    @Autowired
    private ObjectAccessService objectAccessService;
    @Autowired
    private ContObjectMapper contObjectMapper;
    @Autowired
    private ContObjectAccessRepository contObjectAccessRepository;
    @Autowired
    private ContObjectAccessMapper contObjectAccessMapper;
    @Autowired
    private ContObjectAccessService contObjectAccessService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrAccessResource = new SubscrAccessResource(objectAccessService, contObjectMapper, contObjectAccessRepository, contObjectAccessService, contObjectAccessMapper, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrAccessResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }

    @Test
    public void testGetContObjects() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr-access/cont-objects").testGet();
    }

    @Test
    public void testGetContObjectsPage() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr-access/cont-objects/page").testGet();
    }

    @Test
    public void testGetZPointAccess() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr-access/cont-zpoints")
            .requestBuilder(b -> b.param("contObjectId", "18811505")).testGet();
    }

}
