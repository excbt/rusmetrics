package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePrice;
import ru.excbt.datafuse.nmk.data.repository.SubscrServicePriceRepository;

@Service
public class SubscrServicePriceService {

	@Autowired
	private SubscrServicePriceRepository subscrServicePriceRepository;

	/**
	 * 
	 * @param packId
	 * @param priceDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServicePrice> selectPackPriceByDate(Long packId, LocalDate priceDate) {
		checkNotNull(priceDate);
		return subscrServicePriceRepository.selectPackPriceByDate(packId, priceDate.toDate());
	}

	/**
	 * 
	 * @param packId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServicePrice> selectPackPrice(Long packId) {
		return subscrServicePriceRepository.findByPackIdOrderByPriceBeginDateDescIdDesc(packId);
	}

	/**
	 * 
	 * @param itemId
	 * @param priceDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServicePrice> selectItemPriceByDate(Long itemId, LocalDate priceDate) {
		checkNotNull(priceDate);
		return subscrServicePriceRepository.selectItemPriceByDate(itemId, priceDate.toDate());
	}

	/**
	 * 
	 * @param itemId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServicePrice> selectItemPrice(Long itemId) {
		return subscrServicePriceRepository.findByItemIdOrderByPriceBeginDateDescIdDesc(itemId);
	}

	/**
	 * 
	 * @param itemId
	 * @param priceDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServicePrice> selectPriceByDate(LocalDate priceDate) {
		checkNotNull(priceDate);
		return subscrServicePriceRepository.selectPriceByDate(priceDate.toDate());
	}

}
