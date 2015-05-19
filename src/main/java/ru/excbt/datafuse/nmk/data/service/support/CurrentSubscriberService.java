package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.security.AuditUserPrincipal;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;

@Service
public class CurrentSubscriberService {

	private static final Logger logger = LoggerFactory
			.getLogger(CurrentSubscriberService.class);

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private MockSubscriberService mockSubscriberService;

	/**
	 * 
	 * @return
	 */
	public long getSubscriberId() {

		AuditUserPrincipal userPrincipal = currentUserService
				.getCurrentAuditUserPrincipal();

		if (userPrincipal == null) {
			return mockSubscriberService.getMockSubscriberId();
		}

		Long result = userPrincipal.getSubscriberId() != null ? userPrincipal
				.getSubscriberId() : mockSubscriberService
				.getMockSubscriberId();

		checkNotNull(result, "getSubscriberId() is NULL");

		return result;

	}

	/**
	 * 
	 * @return
	 */
	public Subscriber getSubscriber() {
		AuditUserPrincipal userPrincipal = currentUserService
				.getCurrentAuditUserPrincipal();

		if (userPrincipal == null) {
			return mockSubscriberService.getMockSubscriber();
		}

		Long subscriberId = userPrincipal.getSubscriberId();
		if (subscriberId == null) {
			return mockSubscriberService.getMockSubscriber();
		}

		return subscriberService.findOne(subscriberId);
	}

}
