package ru.excbt.datafuse.nmk.config.jpa;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ru.excbt.datafuse.nmk.config.PropertyConfig;
import ru.excbt.datafuse.nmk.data.auditor.MockAuditorAware;
import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.service.support.MockSubscriberService;
import ru.excbt.datafuse.nmk.data.service.support.MockUserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PropertyConfig.class, JpaConfigCli.class })
public class JpaConfigTest {

	private final static long TEST_AUDIT_USER = 1;
	public static final long DEV_SUBSCR_ORG_ID = 728;

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	protected MockAuditorAware auditorAware;

	@Autowired
	protected MockUserService mockUserService;

	@Autowired
	protected MockSubscriberService mockSubscriberService;

	@Test
	public void entityManagerOK() {
		assertNotNull(entityManager);
	}

	@Before
	public void wireUpAuditor() {
		auditorAware.setAuditUser(entityManager.getReference(AuditUser.class,
				TEST_AUDIT_USER));
		mockUserService.setMockUserId(TEST_AUDIT_USER);
		mockSubscriberService.setMockSubscriberId(DEV_SUBSCR_ORG_ID);
	}

}
