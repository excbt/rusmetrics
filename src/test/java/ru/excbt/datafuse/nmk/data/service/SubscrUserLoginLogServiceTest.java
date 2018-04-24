package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import ru.excbt.datafuse.nmk.service.conf.PortalDataTest;

@RunWith(SpringRunner.class)
public class SubscrUserLoginLogServiceTest extends PortalDataTest {

	@Autowired
	private SubscrUserLoginLogService subscrUserLoginLogService;

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testLoginLog() throws Exception {
		subscrUserLoginLogService.registerLogin(59270873L, "kovtonyuk");
	}
}
