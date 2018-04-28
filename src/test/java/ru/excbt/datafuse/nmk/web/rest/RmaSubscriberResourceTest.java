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
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.SubscriberDTO;
import ru.excbt.datafuse.nmk.data.model.types.TimezoneDefKey;
import ru.excbt.datafuse.nmk.data.service.ObjectAccessService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.OrganizationService;
import ru.excbt.datafuse.nmk.service.SubscriberManageService;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.service.mapper.SubscriberMapper;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class RmaSubscriberResourceTest extends PortalApiTest {

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPortalMockMvc;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private PortalUserIdsService portalUserIdsService;

    private RmaSubscriberResource rmaSubscriberResource;

    @Autowired
    private ObjectAccessService objectAccessService;

    @Autowired
    private SubscriberManageService subscriberManageService;

    @Autowired
    private SubscriberMapper subscriberMapper;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        rmaSubscriberResource = new RmaSubscriberResource(subscriberService, subscriberManageService, organizationService, portalUserIdsService);

        this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(rmaSubscriberResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
    }


	@Autowired
	private SubscriberService subscriberService;

    @Autowired
    private OrganizationService organizationService;

    private Organization createOrganization () {
        Organization org = new Organization();
        org.setOrganizationName("test org");
        return organizationService.saveOrganization(org);
    }

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testGetSubscribers() throws Exception {
        mockMvcRestWrapper.restRequest("/api/rma/subscribers").testGet();
//		_testGetJson(UrlUtils.apiRmaUrl("/subscribers"));
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testGetSubscriber() throws Exception {

        Organization org = createOrganization();

        assertNotNull(org.getId());
	    SubscriberDTO dto = SubscriberDTO.builder().subscriberName("Test Subscriber").organizationId(org.getId()).timezoneDefKeyname("MSK").build();

	    Subscriber subscriber = subscriberManageService.createRmaSubscriber(subscriberMapper.toEntity(dto), portalUserIdsService.getCurrentIds().getSubscriberId());

	    assertNotNull(subscriber.getId());
        mockMvcRestWrapper.restRequest("/api/rma/subscribers/{id}", subscriber.getId()).testGet();
//	    _testGetJson("/api/rma/subscribers/" + subscriber.getId());
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Transactional
	public void testSubcriberCRUD() throws Exception {

        Organization org = createOrganization();


        SubscriberDTO dto = SubscriberDTO.builder()
            .subscriberName("TEST_SUBSCRIBER")
            .organizationId(org.getId())
            .timezoneDefKeyname(TimezoneDefKey.GMT_M3.getKeyname()).build();

		Long subscriberId = mockMvcRestWrapper.restRequest("/api/rma/subscribers").testPost(dto).getLastId();
//            _testCreateJson(UrlUtils.apiRmaUrl("/subscribers"), dto);

		SubscriberDTO createdDTO = subscriberService.findSubscriberDTO(subscriberId).map(s -> {
            s.setComment("Updated By REST");
            s.setCanCreateChild(true);
            return s;
        }).orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(Subscriber.class, subscriberId));

        mockMvcRestWrapper.restRequest("/api/rma/subscribers/{id}", subscriberId).testPut(createdDTO);
//		_testUpdateJson("/api/rma/subscribers/" + subscriberId, createdDTO);

        mockMvcRestWrapper.restRequest("/api/rma/subscribers/{id}", subscriberId).testGet();
//		_testGetJson("/api/rma/subscribers/" + subscriberId);

        mockMvcRestWrapper.restRequest("/api/rma/subscribers/{id}", subscriberId)
            .requestBuilder(b -> b.param("isPermanent", "false"))
            .testDelete();
//		_testDeleteJson("/api/rma/subscribers/" + subscriberId,
//            b -> b.param("isPermanent", "false"));
	}
}
