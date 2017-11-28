package ru.excbt.datafuse.nmk.config.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import ru.excbt.datafuse.nmk.data.service.MockSubscriberService;
import ru.excbt.datafuse.nmk.data.service.MockUserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class AbstractJpaConfigTest {

	@PersistenceContext(unitName = "nmk-p")
	protected EntityManager entityManager;

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
		mockUserService.setMockUserId(userId);
		mockSubscriberService.setMockSubscriberId(subscriberId);
	}

}
