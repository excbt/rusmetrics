package ru.excbt.datafuse.nmk.web.api;

import org.junit.Before;
import org.junit.Ignore;
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
import ru.excbt.datafuse.nmk.data.model.SubscrServiceAccess;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.service.*;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.SubscriberTimeService;
import ru.excbt.datafuse.nmk.utils.UrlUtils;
import ru.excbt.datafuse.nmk.web.PortalApiTest;
import ru.excbt.datafuse.nmk.web.rest.util.MockMvcRestWrapper;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class SubscServiceManageControllerTest extends PortalApiTest {

	private final static long MANUAL_SUBSCRIBER_ID = 64166467;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restPortalMockMvc;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Mock
	private PortalUserIdsService portalUserIdsService;

    @Autowired
    private SubscrServiceAccessService subscrServiceSubscriberAccess;

    @Autowired
    private SubscrServiceItemService subscrServiceItemService;

    @Autowired
    private SubscrServicePackService subscrServicePackService;

    @Autowired
    private CurrentSubscriberService currentSubscriberService;

    private SubscServiceManageController subscServiceManageController;
    @Autowired
    private SubscrPriceListService subscrPriceListService;
    @Autowired
    private SubscriberTimeService subscriberTimeService;
    @Autowired
    private SubscrServiceAccessService subscrServiceAccessService;

    private MockMvcRestWrapper mockMvcRestWrapper;

    @Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);

	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);

        subscServiceManageController = new SubscServiceManageController(subscrServicePackService, subscrServiceItemService, subscrPriceListService, subscriberTimeService, subscrServiceAccessService, portalUserIdsService);

	    this.restPortalMockMvc = MockMvcBuilders.standaloneSetup(subscServiceManageController)
	        .setCustomArgumentResolvers(pageableArgumentResolver)
	        .setMessageConverters(jacksonMessageConverter).build();

        mockMvcRestWrapper = new MockMvcRestWrapper(restPortalMockMvc);
	}



	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testPackGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/manage/service/servicePackList").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testItemsGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/manage/service/serviceItemList").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testPricesGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/manage/service/servicePriceList").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testManualSubscriberAccessGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/{id}/manage/service/access", MANUAL_SUBSCRIBER_ID).testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testCurrentSubscriberAccessGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr//manage/service/access").testGet();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testManualSubscriberUpdateAccess() throws Exception {

		List<SubscrServicePack> packs = subscrServicePackService.findByKeyname("WEB_TEST_SERVICE");
		assertTrue(packs.size() == 1);

		Long testPackId = packs.get(0).getId();

		List<SubscrServiceAccess> accessList = subscrServiceSubscriberAccess
				.selectSubscriberServiceAccess(MANUAL_SUBSCRIBER_ID, LocalDate.now());

		accessList.forEach((i) -> {
			i.setId(null);
			i.setSubscriber(null);
			i.setSubscriberId(null);
			i.setAccessStartDate(null);
		});

		Optional<SubscrServiceAccess> testServiceCheck = accessList.stream()
				.filter((i) -> testPackId.equals(i.getPackId())).findAny();

		if (testServiceCheck.isPresent()) {
			accessList.remove(testServiceCheck.get());
		} else {
			SubscrServiceAccess access = SubscrServiceAccess.newInstance(testPackId, null);
			accessList.add(access);
		}

		String url = UrlUtils.apiSubscrUrl(String.format("/%d/manage/service/access", MANUAL_SUBSCRIBER_ID));
        mockMvcRestWrapper.restRequest(url).testPut(accessList);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testCurrentSubscriberUpdateAccess() throws Exception {

		List<SubscrServicePack> packs = subscrServicePackService.findByKeyname("WEB_TEST_SERVICE");
		assertTrue(packs.size() == 1);

		Long testPackId = packs.get(0).getId();

		List<SubscrServiceAccess> accessList = subscrServiceSubscriberAccess
				.selectSubscriberServiceAccess(currentSubscriberService.getSubscriberId(), LocalDate.now());

		accessList.forEach((i) -> {
			i.setId(null);
			i.setSubscriber(null);
			i.setSubscriberId(null);
			i.setAccessStartDate(null);
		});

		Optional<SubscrServiceAccess> testServiceCheck = accessList.stream()
				.filter((i) -> testPackId.equals(i.getPackId())).findAny();

		if (testServiceCheck.isPresent()) {
			accessList.remove(testServiceCheck.get());
		} else {
			SubscrServiceAccess access = SubscrServiceAccess.newInstance(testPackId, null);
			accessList.add(access);
		}

		String url = UrlUtils.apiSubscrUrl("/manage/service/access");
        mockMvcRestWrapper.restRequest(url).testPut(accessList);

	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testManualSubscriberPermissionsGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/{id}/manage/service/permissions", MANUAL_SUBSCRIBER_ID).testGet();
	}

	@Test
	public void testCurrentSubscriberPermissionsGet() throws Exception {
        mockMvcRestWrapper.restRequest("/api/subscr/manage/service/permissions").testGet();
	}

}
