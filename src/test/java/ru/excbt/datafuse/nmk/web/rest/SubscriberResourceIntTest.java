package ru.excbt.datafuse.nmk.web.rest;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDGenerator;
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
import ru.excbt.datafuse.nmk.data.model.types.TimezoneDefKey;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.service.SubscriberManageService;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.mapper.SubscriberMapper;
import ru.excbt.datafuse.nmk.service.vm.SubscriberVM;
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

    @Autowired
    private SubscriberManageService subscriberManageService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscriberResource = new SubscriberResource(portalUserIdsService, subscriberService, subscriberMapper, subscriberManageService);
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
        mockMvcRestWrapper.restRequest("/api/subscribers/rma")
            .testGet();
    }

    @Test
    @Transactional
    public void getSubscribersRMAPageSearch() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscribers/rma")
            .requestBuilder(b -> b.param("searchString", "ижевск"))
            .testGet();
    }

    @Test
    @Transactional
    public void subscriberNormalCreate() throws Exception {
        SubscriberVM vm = SubscriberVM.builder().subscriberName("Test Subscriber").canCreateChild(false).build();
        log.info("Is RMA: {}, Key: {}", portalUserIdsService.getCurrentIds().isRma(), portalUserIdsService.getCurrentIds().getSubscrTypeKey());
        mockMvcRestWrapper.restRequest("/api/subscribers/normal").testPut(vm);
    }

    @Test
    @Transactional
    public void subscriberRmaCreate() throws Exception {
        SubscriberVM vm = SubscriberVM.builder()
            .subscriberName("Test Subscriber")
            .rmaLdapOu("Test_" + Generators.timeBasedGenerator().generate())
            .canCreateChild(true)
            .timezoneDef(TimezoneDefKey.MSK.getKeyname())
            .build();
        log.info("Is RMA: {}, Key: {}", portalUserIdsService.getCurrentIds().isRma(), portalUserIdsService.getCurrentIds().getSubscrTypeKey());
        mockMvcRestWrapper.restRequest("/api/subscribers/rma").testPut(vm);
    }
}
