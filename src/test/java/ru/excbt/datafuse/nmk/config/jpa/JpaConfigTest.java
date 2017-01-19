package ru.excbt.datafuse.nmk.config.jpa;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ru.excbt.datafuse.nmk.config.Constants;
import ru.excbt.datafuse.nmk.data.model.support.SubscriberUserInfo;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;
import ru.excbt.datafuse.nmk.data.support.TestExcbtRmaIds;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { JpaConfigTest.class })
@ActiveProfiles(value = { Constants.SPRING_PROFILE_TEST, Constants.SPRING_PROFILE_DEVELOPMENT })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		SpringApplicationAdminJmxAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class })
@Import(value = { DatabaseConfig.class })
public class JpaConfigTest extends AbstractJpaConfigTest implements SubscriberUserInfo, TestExcbtRmaIds {

	private final static long TEST_AUDIT_USER = 1;
	public static final long DEV_SUBSCR_ORG_ID = 728;

	/**
	 * 
	 */
	@Test
	public void entityManagerOK() {
		assertNotNull(entityManager);
	}

	/**
	 * 
	 */
	@Before
	public void wireUpAuditor() {
		setupAuditor(getSubscrUserId(), getSubscriberId());
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public long getSubscriberId() {
		return DEV_SUBSCR_ORG_ID;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public long getSubscrUserId() {
		return TEST_AUDIT_USER;
	}

	/**
	 * 
	 * @return
	 */
	public SubscriberParam getSubscriberParam() {
		return SubscriberParam.builder().subscriberId(getSubscriberId()).subscrUserId(getSubscrUserId()).build();
	}

}
