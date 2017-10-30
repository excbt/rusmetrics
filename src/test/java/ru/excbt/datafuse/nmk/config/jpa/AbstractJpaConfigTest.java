package ru.excbt.datafuse.nmk.config.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;

import ru.excbt.datafuse.nmk.data.auditor.MockAuditorAware;
import ru.excbt.datafuse.nmk.data.model.V_AuditUser;
import ru.excbt.datafuse.nmk.data.service.MockSubscriberService;
import ru.excbt.datafuse.nmk.data.service.MockUserService;

public abstract class AbstractJpaConfigTest {

	@PersistenceContext(unitName = "nmk-p")
	protected EntityManager entityManager;

	@Autowired
	protected MockAuditorAware auditorAware;

	@Autowired
	protected MockUserService mockUserService;

	@Autowired
	protected MockSubscriberService mockSubscriberService;

	/**
	 *
	 * @param userId
	 * @param subscriberId
	 */
	protected void setupAuditor(long userId, long subscriberId) {
		auditorAware.setAuditUser(entityManager.getReference(V_AuditUser.class,
				userId));
		mockUserService.setMockUserId(userId);
		mockSubscriberService.setMockSubscriberId(subscriberId);
	}

}
