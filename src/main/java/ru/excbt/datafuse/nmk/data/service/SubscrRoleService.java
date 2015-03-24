package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.repository.SubscrRoleRepository;

@Service
@Transactional
public class SubscrRoleService {
	
	@Autowired
	private SubscrRoleRepository subscrRoleRepository;
	
	@Transactional(readOnly = true)
	public SubscrRole findOne(long id) {
		return subscrRoleRepository.findOne(id);
	}
	
	
}
