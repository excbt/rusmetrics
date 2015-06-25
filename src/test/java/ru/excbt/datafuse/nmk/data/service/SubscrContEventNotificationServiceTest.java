package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;

public class SubscrContEventNotificationServiceTest extends JpaSupportTest {

	@Autowired
	private SubscrContEventNotifiicationService subscrContEventNotifiicationService;
	
	@Test
	public void testFindAll() {
		Integer i = 728;
		Page<?> result = subscrContEventNotifiicationService.selectAllNotifications(i, true, null);
		assertNotNull(result);
	}
	
}
