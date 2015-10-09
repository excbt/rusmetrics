package ru.excbt.datafuse.nmk.data.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ContEventRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;

@Service
public class SubscriberService {

	@Autowired
	private SubscriberRepository subscriberRepository;

	@Autowired
	private SubscrUserRepository subscrUserRepository;

	@Autowired
	private ContZPointRepository contZPointRepository;

	@Autowired
	private ContEventRepository contEventRepository;

	@PersistenceContext(unitName = "nmk-p")
	private EntityManager em;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Subscriber selectSubscriber(long subscriberId) {
		Subscriber result = subscriberRepository.findOne(subscriberId);
		if (result == null) {
			throw new PersistenceException(String.format("Subscriber(id=%d) is not found", subscriberId));
		}

		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Subscriber findOne(long subscriberId) {
		Subscriber result = subscriberRepository.findOne(subscriberId);
		if (result == null) {
			throw new PersistenceException(String.format("Subscriber(id=%d) is not found", subscriberId));
		}

		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContObject> selectSubscriberContObjects(long subscriberId) {
		List<ContObject> result = subscriberRepository.selectContObjects(subscriberId);
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectSubscriberContObjectIds(long subscriberId) {
		List<Long> result = subscriberRepository.selectContObjectIds(subscriberId);
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public int selectSubscriberContObjectCount(long subscriberId) {
		List<Long> result = subscriberRepository.selectContObjectIds(subscriberId);
		return result.size();
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	@Deprecated
	public List<ContZPoint> findContZPoints(long contObjectId) {
		List<ContZPoint> result = contZPointRepository.findByContObjectId(contObjectId);
		return result;
	}

	/**
	 * 
	 * @param subscrUserId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrUser findSubscrUser(long subscrUserId) {
		return subscrUserRepository.findOne(subscrUserId);
	}

	/**
	 * 
	 * @param userName
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrUser> findUserByUsername(String userName) {
		return subscrUserRepository.findByUserNameIgnoreCase(userName);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public boolean checkContObjectSubscription(long subscriberId, long contObjectId) {
		List<Long> resultIds = subscriberRepository.selectContObjectId(subscriberId, contObjectId);
		return resultIds.size() > 0;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Date getSubscriberCurrentTime(Long subscriberId) {
		Object dbResult = em.createNativeQuery("SELECT get_subscriber_current_time(?1);").setParameter(1, subscriberId)
				.getSingleResult();
		if (dbResult == null) {
			return null;
		}
		return (Date) dbResult;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<ContZPoint> selectSubscriberContZPoints(long subscriberId) {
		List<ContZPoint> result = subscriberRepository.selectContZPoints(subscriberId);
		result.forEach(i -> {
			i.getDeviceObjects().forEach(j -> {
				j.loadLazyProps();
			});
		});
		return result;
	}

}
