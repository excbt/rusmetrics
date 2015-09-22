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
public class ManualJpaConfigTest extends AbstractJpaConfigTest {

	private final static long SUBSCR_USER_ID = 64166469; // manual-ex1
	public static final long SUBSCR_ORG_ID = 64166467; // РМА-EXCBT

	@Test
	public void entityManagerOK() {
		assertNotNull(entityManager);
	}

	@Before
	public void wireUpAuditor() {
		setupAuditor(SUBSCR_USER_ID, SUBSCR_ORG_ID);
	}

}
