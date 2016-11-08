package ru.excbt.datafuse.nmk.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePackSpecial;
import ru.excbt.datafuse.nmk.data.repository.SubscrServicePackRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrServicePackSpecialRepository;
import ru.excbt.datafuse.nmk.data.service.support.SubscriberParam;

/**
 * Сервис для работы с пакетом услуг абонента
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.09.2015
 *
 */
@Service
public class SubscrServicePackService {

	@Autowired
	private SubscrServicePackRepository subscrServicePackRepository;

	@Autowired
	private SubscrServicePackSpecialRepository subscrServicePackSpecialRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServicePack> selectServicePackList(SubscriberParam subscriberParam) {
		Sort sort = new Sort(Sort.Direction.ASC, "packNr");
		List<SubscrServicePack> result = subscrServicePackRepository.findAll(sort);

		final List<SubscrServicePackSpecial> subscrServicePackSpecials = subscrServicePackSpecialRepository
				.findBySubscriberId(subscriberParam.getSubscriberId());

		List<Long> servicePackIds = subscrServicePackSpecials.stream().map(i -> i.getSubscrServicePackId())
				.collect(Collectors.toList());

		return result.stream().filter(i -> !Boolean.TRUE.equals(i.getIsSpecial()) || servicePackIds.contains(i.getId()))
				.collect(Collectors.toList());
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
