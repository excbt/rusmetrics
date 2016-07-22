package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Instant;
import java.util.Date;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.V_AuditUser;
import ru.excbt.datafuse.nmk.data.model.V_FullUserInfo;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.V_FullUserInfoRepository;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.security.SubscriberUserDetails;

/**
 * Класс для работы с текущим абонентом
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 16.03.2015
 *
 */
@Service
public class CurrentSubscriberService {

	private static final Logger logger = LoggerFactory.getLogger(CurrentSubscriberService.class);

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private MockSubscriberService mockSubscriberService;

	//	@Autowired
	//	protected UserSession userSession;

	@Autowired
	private MockUserService mockUserService;

	@Autowired
	private V_FullUserInfoRepository fullUserInfoRepository;

	@Autowired
	private CurrentSubscriberUserDetailsService subscriberUserDetailsService;

	/**
	 * 
	 * @return
	 */
	public long getSubscriberId() {

		SubscriberUserDetails userDetails = subscriberUserDetailsService.getCurrentUserDetails();

		if (userDetails == null) {
			logger.warn("ATTENTION!!! userPrincipal is null. Using mockUserService");
			return mockSubscriberService.getMockSubscriberId();
		}

		Long result = null;
		Long subscriberId = userDetails.getSubscriberId();
		if (subscriberId != null) {
			result = subscriberId;
		} else {
			logger.warn("ATTENTION!!! Property subscriberId of AuditUserPrincipal is null. Using mockUserService");
			result = mockSubscriberService.getMockSubscriberId();
		}

		checkNotNull(result, "getSubscriberId() is NULL");

		return result;
	}

	/**
	 * 
	 * @return
	 */
	public SubscriberParam getSubscriberParam() {
		//		if (userSession.getSubscriberParam() == null) {
		//
		//			Subscriber subscriber = getSubscriber();
		//			checkNotNull(subscriber.getId());
		//
		//			userSession.setSubscriberParam(SubscriberParam.builder().subscriberId(subscriber.getId())
		//					.subscrUserId(getCurrentUserId()).isRma(isRma()).rmaSubscriber(subscriber.getRmaSubscriberId())
		//					.parentSubscriber(subscriber.getParentSubscriberId()).subscrTypeKey(subscriber.getSubscrType())
		//					.build());
		//		}
		//		return userSession.getSubscriberParam();

		Subscriber subscriber = getSubscriber();
		checkNotNull(subscriber.getId());

		return SubscriberParam.builder().subscriberId(subscriber.getId())
				.subscrUserId(getCurrentUserId()).isRma(isRma()).rmaSubscriber(subscriber.getRmaSubscriberId())
				.parentSubscriber(subscriber.getParentSubscriberId()).subscrTypeKey(subscriber.getSubscrType())
				.build();

	}

	/**
	 * 
	 * @return
	 */
	public Subscriber getSubscriber() {

		SubscriberUserDetails userDetails = subscriberUserDetailsService.getCurrentUserDetails();

		if (userDetails == null) {
			logger.warn("ATTENTION!!! AuditUserPrincipal is null. Using mockUserService");
			return mockSubscriberService.getMockSubscriber();
		}

		Long subscriberId = userDetails.getSubscriberId();
		if (subscriberId == null) {
			logger.warn("ATTENTION!!! Property subscriberId of AuditUserPrincipal is null. Using mockUserService");
			return mockSubscriberService.getMockSubscriber();
		}

		return userDetails.getSubscriber();
		//return subscriberService.selectSubscriber(subscriberId);
	}

	/**
	 * 
	 * @return
	 */
	public LocalDateTime getSubscriberCurrentTime_Joda() {
		Date pre = subscriberService.getSubscriberCurrentTime(getSubscriberId());
		return pre == null ? null : new LocalDateTime(pre);
	}

	/**
	 * 
	 * @return
	 */
	public Instant getSubscriberCurrentTimeInstant_JDK() {
		Date pre = subscriberService.getSubscriberCurrentTime(getSubscriberId());
		return pre == null ? null : pre.toInstant();
	}

	/**
	 * 
	 * @return
	 */
	public Long getCurrentUserId() {
		return getCurrentAuditor().getId();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isRma() {
		Subscriber subscriber = getSubscriber();
		if (subscriber == null) {
			return false;
		}
		return Boolean.TRUE.equals(subscriber.getIsRma());
	}

	/**
	 * 
	 * @return
	 */
	public Long getRmaSubscriberId() {
		Subscriber subscriber = getSubscriber();
		if (subscriber == null) {
			return null;
		}
		if (Boolean.TRUE.equals(subscriber.getIsRma())) {
			return subscriber.getId();
		}
		return subscriber.getRmaSubscriberId();
	}

	/**
	 * 
	 * @return
	 */
	public Long getGhostSubscriberId() {
		Subscriber subscriber = getSubscriber();
		return subscriber != null ? subscriber.getGhostSubscriberId() : null;
	}

	/**
	 * 
	 * @return
	 */
	public V_AuditUser getCurrentAuditor() {
		SubscriberUserDetails subscriberUserDetails = subscriberUserDetailsService.getCurrentUserDetails();
		if (subscriberUserDetails == null) {
			return mockUserService.getMockAuditUser();
		}
		return new V_AuditUser(subscriberUserDetails);

	}

	/**
	 * 
	 * @return
	 */
	public V_FullUserInfo getFullUserInfo() {
		V_AuditUser user = getCurrentAuditor();
		if (user == null) {
			return null;
		}

		V_FullUserInfo result = fullUserInfoRepository.findOne(user.getId());
		return result == null ? null : new V_FullUserInfo(result);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSystemUser() {
		return subscriberUserDetailsService.isSystem();
	}

	public SubscriberUserDetails getCurrentUserDetails() {
		return subscriberUserDetailsService.getCurrentUserDetails();
	}

}
