package ru.excbt.datafuse.nmk.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.repository.SubscrActionGroupRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrActionUserRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;


@Service
@Transactional
public class SubscrActionService implements SecuredRoles {

	@Autowired
	private SubscrActionGroupRepository subscrActionGroupRepository;
	
	@Autowired
	private SubscrActionUserRepository subscrActionUserRepository;


	


}
