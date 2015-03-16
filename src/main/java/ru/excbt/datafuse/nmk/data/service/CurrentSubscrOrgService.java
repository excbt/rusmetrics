package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.excbt.datafuse.nmk.data.model.SubscrOrg;

@Service
public class CurrentSubscrOrgService {

	private static final long SUBSCR_ORG_ID = 728;
	
	@Autowired
	private SubscrOrgService subscrOrgService;
	
	public long getSubscrOrgId() {
		return SUBSCR_ORG_ID;
	}
	
	public SubscrOrg getSubscrOrg() {
		return subscrOrgService.findOne(SUBSCR_ORG_ID);
	}
}
