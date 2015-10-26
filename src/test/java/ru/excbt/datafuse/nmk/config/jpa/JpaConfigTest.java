package ru.excbt.datafuse.nmk.config.jpa;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.excbt.datafuse.nmk.config.PropertyConfig;
import ru.excbt.datafuse.nmk.config.ldap.LdapConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PropertyConfig.class, JpaConfigLocal.class,
		JpaRawConfigLocal.class, LdapConfig.class })
public class JpaConfigTest extends AbstractJpaConfigTest {

	private final static long TEST_AUDIT_USER = 1;
	public static final long DEV_SUBSCR_ORG_ID = 728;

	@Test
	public void entityManagerOK() {
		assertNotNull(entityManager);
	}

	@Before
	public void wireUpAuditor() {
		setupAuditor(TEST_AUDIT_USER, DEV_SUBSCR_ORG_ID);
	}

}
