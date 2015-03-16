package ru.excbt.datafuse.nmk.data.service;

import org.springframework.stereotype.Service;

@Service
public class CurrentSubscrOrgService {

	private static final long SUBSCR_ORG_ID = 728; 
	
	public long getSubscrOrgId() {
		return SUBSCR_ORG_ID;
	}
}
