package ru.excbt.datafuse.nmk.data.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.service.SubscriberService;

@Service
public class CurrentSubscriberService {

	private static final long SUBSCR_ORG_ID = 728;
	
	@Autowired
	private SubscriberService subscriberService;
	
	public long getSubscrOrgId() {
		return SUBSCR_ORG_ID;
	}
	
	public Subscriber getSubscriber() {
		return subscriberService.findOne(SUBSCR_ORG_ID);
	}
}
