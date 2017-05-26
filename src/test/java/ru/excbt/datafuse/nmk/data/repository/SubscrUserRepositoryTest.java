package ru.excbt.datafuse.nmk.data.repository;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.excbt.datafuse.nmk.config.jpa.JpaSupportTest;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
    SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, WebMvcAutoConfiguration.class})
@Transactional
public class SubscrUserRepositoryTest extends JpaSupportTest {


	private static final Logger logger = LoggerFactory
			.getLogger(SubscrUserRepositoryTest.class);

	@Autowired
	private SubscrUserRepository subscrUserRepository;


	@Test
	public void testFindUserName() {
		List<SubscrUser> suList = subscrUserRepository.findByUserNameIgnoreCase("s_admin");
		assertNotNull(suList);

		SubscrUser su = suList.get(0);

		logger.debug("Find subscrUser {}", su.getUserName());
		logger.debug("subscrUser roles {}", su.getSubscrRoles().size());

	}



}
