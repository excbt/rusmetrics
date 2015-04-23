package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.repository.SubscrActionGroupRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrActionUserRepository;

@Service
@Transactional
public class SubscrActionService {

	@Autowired
	private SubscrActionGroupRepository subscrActionGroupRepository;
	
	@Autowired
	private SubscrActionUserRepository subscrActionUserRepository;

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SubscrActionGroup> findActionGroup(long subscriberId) {
		return subscrActionGroupRepository.findBySubscriberId(subscriberId);
	}
	
	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SubscrActionUser> findActionUser(long subscriberId) {
		return subscrActionUserRepository.findBySubscriberId(subscriberId);
	}
	
}
