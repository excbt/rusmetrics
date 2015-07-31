package ru.excbt.datafuse.nmk.data.service.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.security.AuditUserPrincipal;
import ru.excbt.datafuse.nmk.data.service.ContObjectService;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;
import ru.excbt.datafuse.nmk.security.SubscriberUserDetails;

@Service
public class CurrentSubscriberService {

	private static final Logger logger = LoggerFactory
			.getLogger(CurrentSubscriberService.class);

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private ContObjectService contObjectService;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private MockSubscriberService mockSubscriberService;

	/**
	 * 
	 * @return
	 */
	public long getSubscriberId() {

		SubscriberUserDetails userDetails = currentUserService.getCurrentUserDetails();
		
		
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
	protected long getSubscriberIdOld() {
		
		AuditUserPrincipal userPrincipal = currentUserService
				.getCurrentAuditUserPrincipal();
		
		if (userPrincipal == null) {
			logger.warn("ATTENTION!!! userPrincipal is null. Using mockUserService");
			return mockSubscriberService.getMockSubscriberId();
		}
		
		Long result = null;
		Long subscriberId = userPrincipal.getSubscriberId();
		if (subscriberId != null) {
			result = userPrincipal.getSubscriberId();
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
	public Subscriber getSubscriber() {

		SubscriberUserDetails userDetails = currentUserService.getCurrentUserDetails();
		

		if (userDetails == null) {
			logger.warn("ATTENTION!!! AuditUserPrincipal is null. Using mockUserService");
			return mockSubscriberService.getMockSubscriber();
		}

		Long subscriberId = userDetails.getSubscriberId();
		if (subscriberId == null) {
			logger.warn("ATTENTION!!! Property subscriberId of AuditUserPrincipal is null. Using mockUserService");
			return mockSubscriberService.getMockSubscriber();
		}

		return subscriberService.findOne(subscriberId);
	}

	/**
	 * 
	 * @return
	 */
	protected Subscriber getSubscriberOld() {
		
		AuditUserPrincipal userPrincipal = currentUserService
				.getCurrentAuditUserPrincipal();
		
		if (userPrincipal == null) {
			logger.warn("ATTENTION!!! AuditUserPrincipal is null. Using mockUserService");
			return mockSubscriberService.getMockSubscriber();
		}
		
		Long subscriberId = userPrincipal.getSubscriberId();
		if (subscriberId == null) {
			logger.warn("ATTENTION!!! Property subscriberId of AuditUserPrincipal is null. Using mockUserService");
			return mockSubscriberService.getMockSubscriber();
		}
		
		return subscriberService.findOne(subscriberId);
	}

	/**
	 * 
	 * @return
	 */
	public LocalDateTime getSubscriberCurrentTime_Joda() {
		Date pre = subscriberService
				.getSubscriberCurrentTime(getSubscriberId());
		return pre == null ? null : new LocalDateTime(pre);
	}

	/**
	 * 
	 * @return
	 */
	public Instant getSubscriberCurrentTimeInstant_JDK() {
		Date pre = subscriberService
				.getSubscriberCurrentTime(getSubscriberId());
		return pre == null ? null : pre.toInstant();
	}

	/**
	 * 
	 * @return
	 */
	public List<ContObject> getCurrentSubscriberContObjects() {
		return contObjectService.selectSubscriberContObjects(getSubscriberId());
	}

	/**
	 * 
	 * @return
	 */
	public Long getCurrentUserId() {
		return currentUserService.getCurrentUserId();
	}
}
