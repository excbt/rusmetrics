package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceSubscriberAccess;

public class SubscrServiceSubscriberAccessServiceTest extends JpaSupportTest {

	private final static long MANUAL_SUBSCRIBER_ID = 64166467;

	@Autowired
	private SubscrServiceItemService subscrServiceItemService;

	@Autowired
	private SubscrServicePackService subscrServicePackService;

	@Autowired
	private SubscrServiceSubscriberAccessService subscrServiceAccessService;

	@Test
	public void testServiceItems() throws Exception {
		List<SubscrServiceItem> result = subscrServiceItemService.selectServiceItemList();
		assertNotNull(result);
	}

	@Test
	public void testServicePacks() throws Exception {
		List<SubscrServicePack> result = subscrServicePackService.selectServicePackList();
		assertNotNull(result);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSelectServiceAccess() throws Exception {
		List<SubscrServiceSubscriberAccess> subscrServiceAccessList = subscrServiceAccessService
				.selectSubscriberServiceAccessFull(MANUAL_SUBSCRIBER_ID, LocalDate.now());
		assertNotNull(subscrServiceAccessList);
		assertTrue(subscrServiceAccessList.size() > 0);
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateAccess() throws Exception {

		List<SubscrServiceSubscriberAccess> subscrServiceAccessList = subscrServiceAccessService
				.selectSubscriberServiceAccessFull(MANUAL_SUBSCRIBER_ID, LocalDate.now());

		List<SubscrServiceSubscriberAccess> newAccess = new ArrayList<>();

		subscrServiceAccessService.processAccessList(MANUAL_SUBSCRIBER_ID, LocalDate.now(), newAccess);

		List<SubscrServiceSubscriberAccess> newAccess2 = new ArrayList<>();

		newAccess2.add(SubscrServiceSubscriberAccess.newInstance(64471798L, null));

		subscrServiceAccessService.processAccessList(MANUAL_SUBSCRIBER_ID, LocalDate.now(), newAccess2);
	}

	@Test
	public void testAllPack() throws Exception {
		List<SubscrServicePack> packList = subscrServicePackService.selectServicePackList();
		assertTrue(packList.size() > 0);
		List<SubscrServiceSubscriberAccess> newAccess = packList.get(0).getServiceItems().stream()
				.map((i) -> SubscrServiceSubscriberAccess.newInstance(packList.get(0).getId(), i.getId()))
				.collect(Collectors.toList());
		newAccess.add(SubscrServiceSubscriberAccess.newInstance(packList.get(0).getId(), null));
		subscrServiceAccessService.processAccessList(MANUAL_SUBSCRIBER_ID, LocalDate.now(), newAccess);
	}

}
