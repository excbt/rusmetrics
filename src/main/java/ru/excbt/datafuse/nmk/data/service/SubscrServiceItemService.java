package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;
import ru.excbt.datafuse.nmk.data.repository.SubscrServiceItemRepository;

@Service
public class SubscrServiceItemService {

	@Autowired
	private SubscrServiceItemRepository subscrServiceItemRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServiceItem> selectServiceItemList() {
		return subscrServiceItemRepository.findAll();
	}

}
