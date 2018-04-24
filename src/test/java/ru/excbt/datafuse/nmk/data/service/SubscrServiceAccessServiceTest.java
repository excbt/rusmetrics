package ru.excbt.datafuse.nmk.data.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceAccess;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;
import ru.excbt.datafuse.nmk.web.rest.util.PortalUserIdsMock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class SubscrServiceAccessServiceTest extends PortalDataTest {

	private static final Logger logger = LoggerFactory.getLogger(SubscrServiceAccessServiceTest.class);

	private final static long MANUAL_SUBSCRIBER_ID = 64166467;

	@Mock
	private PortalUserIdsService portalUserIdsService;

	@Before
	public void setUp() throws Exception {
	    MockitoAnnotations.initMocks(this);
	    PortalUserIdsMock.initMockService(portalUserIdsService, TestExcbtRmaIds.ExcbtRmaPortalUserIds);
	}


	@Autowired
	private SubscrServiceItemService subscrServiceItemService;

	@Autowired
	private SubscrServicePackService subscrServicePackService;

	@Autowired
	private SubscrServiceAccessService subscrServiceAccessService;

	@Autowired
	private ReportTypeService reportTypeService;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private RmaSubscriberService rmaSubscriberService;

	@Test
	public void testServiceItems() throws Exception {
		List<SubscrServiceItem> result = subscrServiceItemService.selectServiceItemList();
		assertNotNull(result);
	}

	@Test
	public void testServicePacks() throws Exception {
		List<SubscrServicePack> result = subscrServicePackService.selectServicePackList(portalUserIdsService.getCurrentIds());
		assertNotNull(result);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testSelectServiceAccess() throws Exception {
		List<SubscrServiceAccess> subscrServiceAccessList = subscrServiceAccessService
				.selectSubscriberServiceAccessFull(MANUAL_SUBSCRIBER_ID, LocalDate.now());
		assertNotNull(subscrServiceAccessList);
		assertTrue(subscrServiceAccessList.size() > 0);
	}

	/**
	 *
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testUpdateAccess() throws Exception {

		List<SubscrServiceAccess> subscrServiceAccessList = subscrServiceAccessService
				.selectSubscriberServiceAccessFull(MANUAL_SUBSCRIBER_ID, LocalDate.now());

		List<SubscrServiceAccess> newAccess = new ArrayList<>();

		subscrServiceAccessService.processAccessList(MANUAL_SUBSCRIBER_ID, LocalDate.now(), newAccess);

		List<SubscrServiceAccess> newAccess2 = subscrServiceAccessList;

		// newAccess2.add(SubscrServiceAccess.newInstance(64471798L, null));

		subscrServiceAccessService.processAccessList(MANUAL_SUBSCRIBER_ID, LocalDate.now(), newAccess2);
	}

	// @Test
	// public void testAllPack() throws Exception {
	// List<SubscrServicePack> packList =
	// subscrServicePackService.selectServicePackList(true);
	// assertTrue(packList.size() > 0);
	// List<SubscrServiceAccess> newAccess =
	// packList.get(0).getServiceItems().stream()
	// .map((i) -> SubscrServiceAccess.newInstance(packList.get(0).getId(),
	// i.getId()))
	// .collect(Collectors.toList());
	// newAccess.add(SubscrServiceAccess.newInstance(packList.get(0).getId(),
	// null));
	// subscrServiceAccessService.processAccessList(MANUAL_SUBSCRIBER_ID,
	// LocalDate.now(), newAccess);
	// }

	@Test
	public void testSubscriberPermissions() throws Exception {
		List<SubscrServicePermission> permissions = subscrServiceAccessService
				.selectSubscriberPermissions(MANUAL_SUBSCRIBER_ID, LocalDate.now());
		assertTrue(permissions.size() > 0);
	}

	@Test
	public void testSubscriberPermissionFilter() throws Exception {
		List<SubscrServicePermission> permissions = subscrServiceAccessService
				.selectSubscriberPermissions(MANUAL_SUBSCRIBER_ID, LocalDate.now());
		assertTrue(permissions.size() > 0);
		SubscrServicePermissionFilter filter = new SubscrServicePermissionFilter(permissions, portalUserIdsService.getCurrentIds());
		List<ReportType> reportTypes = reportTypeService.findAllReportTypes();
		assertTrue(reportTypes.size() > 0);
		List<ReportType> filteredReports = filter.filterObjects(reportTypes);
		logger.info("Size of filtered reports:{}", filteredReports.size());
		assertNotNull(filteredReports);
		assertTrue(filteredReports.size() > 0);
	}

	@Test
	@Ignore
	public void testUpdateAllRmaSubscriberAccess() throws Exception {
		// Long rmaSubscriberId = 37176875L;
		Long rmaSubscriberId = TestExcbtRmaIds.EXCBT_RMA_SUBSCRIBER_ID;

		List<SubscrServicePack> servicePackList = subscrServicePackService.selectServicePackList(portalUserIdsService.getCurrentIds());

		servicePackList.forEach(i -> {
			logger.info("Service Pack {}: {}", i.getId(), i.getPackName());
		});

		List<Subscriber> subscribers = rmaSubscriberService.selectRmaSubscribers(rmaSubscriberId);
		subscribers.forEach(i -> {
			logger.info("Processing {}", i.getSubscriberName());

			List<SubscrServiceAccess> fullAccessList = new ArrayList<>();

			servicePackList.forEach(p -> {
				List<SubscrServiceAccess> accessList = subscrServiceAccessService.getPackSubscrServiceAccess(p.getId());
				fullAccessList.addAll(accessList);
			});
			logger.info("Total items: {}", fullAccessList.size());
			subscrServiceAccessService.processAccessList(i.getId(), LocalDate.now(), fullAccessList);
		});
	}

}
