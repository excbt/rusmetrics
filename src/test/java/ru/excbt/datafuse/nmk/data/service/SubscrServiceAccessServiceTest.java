package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;

public class SubscrServiceAccessServiceTest extends JpaSupportTest {

	@Autowired
	private SubscrServiceItemService subscrServiceItemService;

	@Autowired
	private SubscrServicePackService subscrServicePackService;

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

}
