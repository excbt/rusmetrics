package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.SubscrContObject;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscrContObjectRepository;

@Service
public class SubscrContObjectService {

	private static final Logger logger = LoggerFactory.getLogger(SubscrContObjectService.class);

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
	public List<SubscrContObject> selectByContObjectId(Long contObjectId) {
		return subscrContObjectRepository.findByContObjectId(contObjectId);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Subscriber selectRmaByContObjectId(Long contObjectId) {
		List<SubscrContObject> subscrContObjects = subscrContObjectRepository.findByContObjectId(contObjectId);
		List<SubscrContObject> rmaList = subscrContObjects.stream()
				.filter(i -> Boolean.TRUE.equals(i.getSubscriber().getIsRma())).collect(Collectors.toList());
		if (rmaList.size() > 1) {
			logger.error("ContObject (id={}) has more than one RMA", contObjectId);
		}
		return rmaList.isEmpty() ? null : rmaList.get(0).getSubscriber();
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
		checkNotNull(subscriberId);
		LocalDate currentDate = subscriberService.getSubscriberCurrentDateJoda(subscriberId);
		return subscrContObjectRepository.selectRmaSubscrContObjectIds(currentDate.toDate());
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectSubscriberContObjects(Long subscriberId) {
		checkNotNull(subscriberId);
		List<ContObject> result = subscrContObjectRepository.selectContObjects(subscriberId);
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectRmaSubscriberContObjects(Long subscriberId) {
		checkNotNull(subscriberId);
		List<ContObject> result = subscrContObjectRepository.selectContObjects(subscriberId);
		List<Long> subscrContObjectIds = selectRmaSubscrContObjectIds(subscriberId);
		Set<Long> subscrContObjectIdMap = new HashSet<>(subscrContObjectIds);
		result.forEach(i -> {
			boolean haveSubscr = subscrContObjectIdMap.contains(i.getId());
			i.set_haveSubscr(haveSubscr);
		});
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectSubscriberContObjectIds(Long subscriberId) {
		checkNotNull(subscriberId);
		List<Long> result = subscrContObjectRepository.selectContObjectIds(subscriberId);
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public int selectSubscriberContObjectCount(Long subscriberId) {
		checkNotNull(subscriberId);
		List<Long> result = subscrContObjectRepository.selectContObjectIds(subscriberId);
		return result.size();
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean checkContObjectSubscription(Long subscriberId, Long contObjectId) {
		checkNotNull(subscriberId);
		checkNotNull(contObjectId);
		List<Long> resultIds = subscrContObjectRepository.selectContObjectId(subscriberId, contObjectId);
		return resultIds.size() > 0;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPoint> selectSubscriberContZPoints(Long subscriberId) {
		checkNotNull(subscriberId);
		List<ContZPoint> result = subscrContObjectRepository.selectContZPoints(subscriberId);
		result.forEach(i -> {
			i.getDeviceObjects().forEach(j -> {
				j.loadLazyProps();
			});
		});
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<DeviceObject> selectDeviceObjects(Long subscriberId) {
		checkNotNull(subscriberId);
		return subscrContObjectRepository.selectDeviceObjects(subscriberId);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectAvailableContObjects(Long subscriberId, Long rmaSubscriberId) {
		checkNotNull(subscriberId);
		checkNotNull(rmaSubscriberId);
		return subscrContObjectRepository.selectAvailableContObjects(subscriberId, rmaSubscriberId);
	}

}
