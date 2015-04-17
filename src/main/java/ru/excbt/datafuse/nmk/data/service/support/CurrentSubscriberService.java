package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.AuditUser;
import ru.excbt.datafuse.nmk.data.model.FullUserInfo;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.FullUserInfoRepository;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;

@Service
public class CurrentSubscriberService {

	private static final long SUBSCR_ORG_ID = 728;
	private static final long NOT_LOGGED_ORG_ID = 0;

	private static final Logger logger = LoggerFactory
			.getLogger(CurrentSubscriberService.class);

	private final ThreadLocal<Map<String, FullUserInfo>> currentFullUserMap = new ThreadLocal<Map<String, FullUserInfo>>();
	private final ThreadLocal<Map<String, Subscriber>> subscriberMap = new ThreadLocal<Map<String, Subscriber>>();

	@PersistenceContext(unitName = "nmk-p")
	private EntityManager em;

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private FullUserInfoRepository fullUserInfoRepository;

	/**
	 * 
	 */
	private Map<String, FullUserInfo> getFullUserMap() {
		Map<String, FullUserInfo> result = currentFullUserMap.get();
		if (result == null) {
			result = new HashMap<>();
			currentFullUserMap.set(result);
		}
		return result;
	}

	/**
	 * 
	 */
	private Map<String, Subscriber> getSubscriberMap() {
		Map<String, Subscriber> result = subscriberMap.get();
		if (result == null) {
			result = new HashMap<>();
			subscriberMap.set(result);
		}
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public long getSubscriberId() {

		AuditUser auditUser = currentUserService.getCurrentAuditUser();
		checkNotNull(auditUser, "AuditUser from currentUserService is NULL");

		Long userId = auditUser.getId();

		if (userId == null) {
			return NOT_LOGGED_ORG_ID;
		}

		checkNotNull(userId);

		FullUserInfo userInfo = getFullUserMap().get(userId.toString());

		if (userInfo == null) {
			userInfo = fullUserInfoRepository.findOne(currentUserService
					.getCurrentAuditUser().getId());
			if (userInfo != null) {
				em.detach(userInfo);
				getFullUserMap().put(userId.toString(), userInfo);
			} else {
				return NOT_LOGGED_ORG_ID;
			}

		}

		long result = userInfo.getSubscriberId() != null ? userInfo
				.getSubscriberId() : SUBSCR_ORG_ID;
		logger.trace("Current Subscriber ID: {}", result);

		return result;
	}

	/**
	 * 
	 * @return
	 */
	public Subscriber getSubscriber() {
		long subscriberId = getSubscriberId();

		Subscriber subscriber = getSubscriberMap().get(subscriberId);
		if (subscriber == null) {
			subscriber = subscriberService.findOne(subscriberId);
			if (subscriber != null) {
				getSubscriberMap()
						.put(String.valueOf(subscriberId), subscriber);
			}
		}

		return subscriber;
	}
}
