package ru.excbt.datafuse.nmk.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.SubscriberMapper;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
public class SubscriberResourceIntTest extends PortalApiTest {

    private static final Logger log = LoggerFactory.getLogger(SubscriberResourceIntTest.class);

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalContObjectMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private SubscriberMapper subscriberMapper;

    private SubscriberResource subscriberResource;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscriberResource = new SubscriberResource(portalUserIdsService, subscriberService, subscriberMapper);
        this.restPortalContObjectMockMvc = MockMvcBuilders.standaloneSetup(subscriberResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        this.mockMvcRestWrapper = new MockMvcRestWrapper(this.restPortalContObjectMockMvc);
    }


//    @Test
//    @Transactional
//    public void getSubscribers() throws Exception {
//        mockMvcRestWrapper.restRequest("/api/subscribers")
//            .testGet();
//    }
//
    @Test
    @Transactional
    public void getSubscribersRMAPage() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscribers/rma/page")
            .testGet();
    }

    @Test
    @Transactional
    public void getSubscribersRMAPageSearch() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscribers/rma/page")
            .requestBuilder(b -> b.param("searchString", "ижевск"))
            .testGet();
    }
}
