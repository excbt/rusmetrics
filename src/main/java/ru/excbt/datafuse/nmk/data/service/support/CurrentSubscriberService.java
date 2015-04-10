package ru.excbt.datafuse.nmk.data.service.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.FullUserInfo;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.FullUserInfoRepository;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;

@Service
public class CurrentSubscriberService {

	private static final long SUBSCR_ORG_ID = 728;
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(CurrentSubscriberService.class);
	

	@Autowired
	private SubscriberService subscriberService;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private FullUserInfoRepository fullUserInfoRepository;

	/**
	 * 
	 * @return
	 */
	public long getSubscriberId() {

		FullUserInfo info = fullUserInfoRepository.findOne(currentUserService
				.getCurrentAuditUser().getId());

		long result = info.getSubscriberId() != null ? info.getSubscriberId()
				: SUBSCR_ORG_ID;
		logger.trace("Current Subscriber ID: {}", result);
		
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public Subscriber getSubscriber() {
		return subscriberService.findOne(this.getSubscriberId());
	}
}
