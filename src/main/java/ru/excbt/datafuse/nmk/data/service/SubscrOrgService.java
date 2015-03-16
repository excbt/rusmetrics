package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.SubscrOrg;
import ru.excbt.datafuse.nmk.data.repository.SubscrOrgRepository;

@Service
@Transactional
public class SubscrOrgService {
	
	@Autowired
	private SubscrOrgRepository subscrOrgRepository;
	
	@Transactional(readOnly = true)
	public SubscrOrg findOne(long id) {
		return subscrOrgRepository.findOne(id);
	}
	
	
}
