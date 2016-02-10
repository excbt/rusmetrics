package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceItem;
import ru.excbt.datafuse.nmk.data.repository.SubscrServiceItemRepository;

/**
 * Сервис для работы с типами услуг абонента
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.09.2015
 *
 */
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
		return selectServiceItemList(false);
	}

	/**
	 * 
	 * @param includeAll
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServiceItem> selectServiceItemList(boolean includeAll) {
		List<SubscrServiceItem> result = subscrServiceItemRepository.findAll();
		if (!includeAll) {
			result = ObjectFilters.activeFilter(result);
		}
		return result;
	}

	/**
	 * 
	 * @param keyname
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServiceItem> findByKeyname(String keyname) {
		return subscrServiceItemRepository.findByKeyname(keyname);
	}

}
