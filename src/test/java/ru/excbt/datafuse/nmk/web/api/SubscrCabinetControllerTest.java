package ru.excbt.datafuse.nmk.web.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.support.SubscrUserWrapper;
import ru.excbt.datafuse.nmk.data.repository.CabinetMessageRepository;
import ru.excbt.datafuse.nmk.data.service.CabinetMessageService;
import ru.excbt.datafuse.nmk.data.service.PortalUserIdsService;
import ru.excbt.datafuse.nmk.data.service.SubscrCabinetService;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.SubscriberService;
import ru.excbt.datafuse.nmk.utils.TestUtils;
import ru.excbt.datafuse.nmk.web.AnyControllerTest;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class SubscrCabinetControllerTest extends PortalApiTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrCabinetControllerTest.class);

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	private SubscrCabinetController subscrCabinetController;

	@Autowired
	private SubscrCabinetService subscrCabinetService;

	@Autowired
	private CabinetMessageService cabinetMessageService;

	@Autowired
	private CabinetMessageRepository cabinetMessageRepository;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscrCabinetController = new SubscrCabinetController(subscrCabinetService, cabinetMessageService, cabinetMessageRepository, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscrCabinetController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}


//	/**
//	 *
//	 * @return
//	 */
//	@Override
//	public long getSubscriberId() {
//		return 512156297L;
//	}
//
//	/**
//	 *
//	 * @return
//	 */
//	@Override
//	public long getSubscrUserId() {
//		return 512156325L;
//	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testContObjectInfoList() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/subscrCabinet/contObjectCabinetInfo").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testCreateCabinetSubscribers() throws Exception {
		List<Long> contObjectIds = Arrays.asList(29863789L, 29863938L, 29863933L);

        mockMvcRestWrapper.restRequest("/api/subscr/subscrCabinet/create").testPut(contObjectIds);
//		_testUpdateJson("/api/subscr/subscrCabinet/create", contObjectIds);
		/**
		 * 29863789
		 * 29863938
		 * 29863933
		 * 29863488
		 * 29863487
		 * 29863944
		 * 66948436
		 * 29863950
		 * 29863957
		 * 29863952
		 * 29863953
		 * 29863949
		 * 29863965
		 * 29863964
		 * 29863934
		 * 29863503
		 * 29863502
		 */

	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testDeleteCabinetSubscriber() throws Exception {

		List<Subscriber> subscribers = subscriberService.selectChildSubscribers(portalUserIdsService.getCurrentIds().getSubscriberId());

		List<Long> childSubscriberIds = subscribers.stream().map(i -> i.getId()).collect(Collectors.toList());
        mockMvcRestWrapper.restRequest("/api/subscr/subscrCabinet/delete").testPut(childSubscriberIds);
//		_testUpdateJson("/api/subscr/subscrCabinet/delete", childSubscriberIds);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testUpdateSubscrUser() throws Exception {
        String content = mockMvcRestWrapper.restRequest("/api/subscr/subscrCabinet/subscrUser/512157249").testGet().getStringContent();

//        String content = _testGetJson("/api/subscr/subscrCabinet/subscrUser/512157249");

		SubscrUserWrapper subscrUserWrapper = TestUtils.fromJSON(new TypeReference<SubscrUserWrapper>() {
		}, content);

		assertNotNull(subscrUserWrapper);

		subscrUserWrapper.getSubscrUser().setDevComment("My custom edit at " + System.currentTimeMillis());
		subscrUserWrapper.setPasswordPocket("12345");

        mockMvcRestWrapper.restRequest("/api/subscr/subscrCabinet/subscrUser/512157249").testPut(subscrUserWrapper);
//		_testUpdateJson("/api/subscr/subscrCabinet/subscrUser/512157249", subscrUserWrapper);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testSubscrUserResetPassword() throws Exception {
		List<Long> subscrUserIds = Arrays.asList(512157249L);
        mockMvcRestWrapper.restRequest("/api/subscr/subscrCabinet/subscrUser/resetPassword").testPut(subscrUserIds);
//		_testUpdateJson("/api/subscr/subscrCabinet/subscrUser/resetPassword", subscrUserIds);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testSubscrUserSendPassword() throws Exception {
		List<Long> subscrUserIds = Arrays.asList(512157254L);
        mockMvcRestWrapper.restRequest("/api/subscr/subscrCabinet/subscrUser/sendPassword").testPut(subscrUserIds);
//		_testUpdateJson("/api/subscr/subscrCabinet/subscrUser/sendPassword", subscrUserIds);
	}

}
