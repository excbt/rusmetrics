package ru.excbt.datafuse.nmk.data.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.JpaSupportTest;

public class SubscrActionServiceTest extends JpaSupportTest {

	@Autowired
	private SubscrActionUserGroupService subscrActionService;
	
	@Test
	public void testInit() {
		assertNotNull(subscrActionService);
	}
}
