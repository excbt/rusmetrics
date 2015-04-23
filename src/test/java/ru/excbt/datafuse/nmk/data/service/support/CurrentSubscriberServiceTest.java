package ru.excbt.datafuse.nmk.data.service.support;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;

public class CurrentSubscriberServiceTest extends JpaSupportTest {

	@Autowired
	private CurrentSubscriberService currentSubscriberService;
	
	@Test
	public void testCurrentUser() {
		assertTrue(currentSubscriberService.getSubscriberId() > 0); 
	}
}
