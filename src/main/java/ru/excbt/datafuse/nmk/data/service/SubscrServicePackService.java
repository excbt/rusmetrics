package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.repository.SubscrServicePackRepository;

@Service
public class SubscrServicePackService {

	@Autowired
	private SubscrServicePackRepository subscrServicePackRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServicePack> selectServicePackList() {
		return selectServicePackList(false);
	}

	/**
	 * 
	 * @param includeAll
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServicePack> selectServicePackList(boolean includeAll) {
		List<SubscrServicePack> result = subscrServicePackRepository.findAll();
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
	public List<SubscrServicePack> findByKeyname(String keyname) {
		return subscrServicePackRepository.findByKeyname(keyname);
	}

}
