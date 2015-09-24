package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceAccess;
import ru.excbt.datafuse.nmk.data.repository.SubscrServiceAccessRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrServiceItemRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrServicePackRepository;

@Service
public class SubscrServiceAccessService {

	@Autowired
	private SubscrServiceAccessRepository subscrServiceAccessRepository;

	@Autowired
	private SubscrServiceItemRepository subscrServiceItemRepository;

	@Autowired
	private SubscrServicePackRepository subscrServicePackRepository;

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrServiceAccess> getSubscriberServiceAccess(long subscriberId) {
		return subscrServiceAccessRepository.findBySubscriberId(subscriberId);
	}
}
