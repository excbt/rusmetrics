package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSms;
import ru.excbt.datafuse.nmk.data.model.SubscrContEventTypeSmsAddr;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ContEventTypeRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrContEventTypeSmsAddrRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrContEventTypeSmsRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;

@Service
public class SubscrContEventTypeSmsService extends AbstractService {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContEventTypeSmsService.class);

	@Autowired
	private ContEventTypeRepository contEventTypeRepository;

	@Autowired
	private SubscrContEventTypeSmsRepository subscrContEventTypeSmsRepository;

	@Autowired
	private SubscrContEventTypeSmsAddrRepository subscrContEventTypeSmsAddrRepository;

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContEventType> selectAvailableContEventTypes() {
		return contEventTypeRepository.selectBySmsNotification();
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrContEventTypeSms> selectSubscrContEventTypeSms(Long subscriberId) {
		logger.trace("selectSubscrContEventTypeSms. subscriberId={}", subscriberId);
		List<SubscrContEventTypeSms> preResult = subscrContEventTypeSmsRepository.findBySubscriberId(subscriberId);
		return ObjectFilters.deletedFilter(preResult);
	}

	/**
	 * 
	 * @param subscrContEventTypeSmsId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrContEventTypeSmsAddr> selectSubscrContEventTypeSmsAddr(Long subscrContEventTypeSmsId) {
		List<SubscrContEventTypeSmsAddr> preResult = subscrContEventTypeSmsAddrRepository
				.findBySubscrContEventTypeSmsId(subscrContEventTypeSmsId);
		return ObjectFilters.deletedFilter(preResult);
	}

	/**
	 * 
	 * @param subscriber
	 * @param contEventType
	 * @param smsAddrList
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrContEventTypeSms createSubscrContEventTypeSms(Subscriber subscriber, ContEventType contEventType,
			List<SubscrContEventTypeSmsAddr> smsAddrList) {

		checkNotNull(subscriber);
		checkNotNull(contEventType);
		checkNotNull(smsAddrList);

		SubscrContEventTypeSms result = new SubscrContEventTypeSms();
		result.setContEventType(contEventType);
		result.setSubscriber(subscriber);
		result.setIsActive(true);

		subscrContEventTypeSmsRepository.save(result);

		smsAddrList.forEach(i -> {
			checkState(i.isNew());
			i.setSubscrContEventTypeSms(result);
		});

		subscrContEventTypeSmsAddrRepository.save(smsAddrList);

		return result;
	}

	/**
	 * 
	 * @param subscrContEventTypeSmsId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteSubscrContEventTypeSms(Long subscrContEventTypeSmsId) {
		SubscrContEventTypeSms sms = subscrContEventTypeSmsRepository.findOne(subscrContEventTypeSmsId);
		if (sms == null) {
			throw new PersistenceException(
					String.format("SubscrContEventTypeSms (id=%d) is not found", subscrContEventTypeSmsId));
		}

		List<SubscrContEventTypeSmsAddr> smsAddList = subscrContEventTypeSmsAddrRepository
				.findBySubscrContEventTypeSmsId(subscrContEventTypeSmsId);

		subscrContEventTypeSmsAddrRepository.save(softDelete(smsAddList));
		subscrContEventTypeSmsRepository.save(softDelete(sms));
	}

}
