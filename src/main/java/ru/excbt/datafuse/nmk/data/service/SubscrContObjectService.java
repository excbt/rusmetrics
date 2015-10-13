package ru.excbt.datafuse.nmk.data.service;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrContObject;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;

@Service
public class SubscrContObjectService {

	@Autowired
	private SubscrContObjectRepository subscrContObjectRepository;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @param objects
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteOne(List<SubscrContObject> objects) {

		objects.forEach(i -> {
			i.setDeleted(1);
			if (i.getSubscrEndDate() == null && i.getSubscriberId() != null) {
				Date subscrDate = subscriberService.getSubscriberCurrentTime(i.getSubscriberId());
				i.setSubscrEndDate(subscrDate);
			}
		});

		subscrContObjectRepository.save(objects);
	}

	/**
	 * 
	 * @param subscrContObject
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteOne(SubscrContObject subscrContObject) {
		subscrContObject.setDeleted(1);
		subscrContObjectRepository.save(subscrContObject);
	}

	/**
	 * 
	 * @param objects
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteOnePermanent(List<SubscrContObject> objects) {
		subscrContObjectRepository.delete(objects);
	}

	/**
	 * 
	 * @param subscrContObject
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteOnePermanent(SubscrContObject subscrContObject) {
		subscrContObjectRepository.delete(subscrContObject);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrContObject> findByContObjectId(Long contObjectId) {
		return subscrContObjectRepository.findByContObjectId(contObjectId);
	}

	/**
	 * 
	 * @param subscrContObject
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrContObject saveOne(SubscrContObject subscrContObject) {
		return subscrContObjectRepository.save(subscrContObject);
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<Long> selectRmaSubscrContObjectIds(Long subscriberId) {
		LocalDate currentDate = subscriberService.getSubscriberCurrentDateJoda(subscriberId);
		return subscrContObjectRepository.selectRmaSubscrContObjectIds(currentDate.toDate());
	}

}
