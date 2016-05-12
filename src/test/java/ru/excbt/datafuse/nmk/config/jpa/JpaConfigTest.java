package ru.excbt.datafuse.nmk.config.jpa;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.excbt.datafuse.nmk.config.PropertyConfig;
import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;
import ru.excbt.datafuse.nmk.data.model.support.SubscriberUserInfo;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		classes = { PropertyConfig.class, JpaConfigLocal.class, JpaRawConfigLocal.class, LdapConfig.class })
public class JpaConfigTest extends AbstractJpaConfigTest implements SubscriberUserInfo {

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
