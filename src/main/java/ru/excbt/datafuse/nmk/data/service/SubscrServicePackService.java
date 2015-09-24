package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
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
		return subscrServicePackRepository.findAll();
	}

}
