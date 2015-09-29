package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceAccess;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.model.keyname.ReportType;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;
import ru.excbt.datafuse.nmk.data.service.support.SubscrServicePermissionFilter;

public class SubscrServiceAccessServiceTest extends JpaSupportTest {

	private final static long MANUAL_SUBSCRIBER_ID = 64166467;

	@Autowired
	private SubscrServiceItemService subscrServiceItemService;

	@Autowired
	private SubscrServicePackService subscrServicePackService;

	@Autowired
	private SubscrServiceAccessService subscrServiceAccessService;

	@Autowired
	private ReportTypeService reportTypeService;

	@Test
	public void testServiceItems() throws Exception {
		List<SubscrServiceItem> result = subscrServiceItemService.selectServiceItemList();
		assertNotNull(result);
	}

	@Test
	public void testServicePacks() throws Exception {
		List<SubscrServicePack> result = subscrServicePackService.selectServicePackList(true);
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
		SubscrServicePermissionFilter filter = new SubscrServicePermissionFilter(permissions);
		List<ReportType> reportTypes = reportTypeService.findAllReportTypes();
		List<ReportType> filteredReports = filter.filterPermissions(reportTypes);
		assertNotNull(filteredReports);
	}

}
