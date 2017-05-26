package ru.excbt.datafuse.nmk.data.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
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
