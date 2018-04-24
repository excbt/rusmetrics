package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class SubscrActionServiceTest extends PortalDataTest {

	@Autowired
	private SubscrActionUserGroupService subscrActionService;

	@Test
	public void testInit() {
		assertNotNull(subscrActionService);
	}
}
