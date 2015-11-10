package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ServicePriceItem;
import ru.excbt.datafuse.nmk.data.repository.ServicePriceItemRepository;

@Service
public class ServicePriceItemService {

	@Autowired
	private ServicePriceItemRepository servicePriceItemRepository;

	/**
	 * 
	 * @param servicePriceListId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ServicePriceItem> findPriceItems(Long servicePriceListId) {
		return servicePriceItemRepository.findByServicePriceListId(servicePriceListId);
	}
}
