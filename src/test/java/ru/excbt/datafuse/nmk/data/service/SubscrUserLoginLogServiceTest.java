package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;

public class SubscrUserLoginLogServiceTest extends JpaSupportTest {

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
