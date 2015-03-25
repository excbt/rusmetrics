package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.SubscrRole;

@Service
public class CurrentSubscriberService {

	private static final long SUBSCR_ORG_ID = 728;
	
	@Autowired
	private SubscrRoleService subscrOrgService;
	
	public long getSubscrOrgId() {
		return SUBSCR_ORG_ID;
	}
	
	public SubscrRole getSubscrOrg() {
		return subscrOrgService.findOne(SUBSCR_ORG_ID);
	}
}
