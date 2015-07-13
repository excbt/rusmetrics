package ru.excbt.datafuse.nmk.data.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ContEventRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;

@Service
@Transactional
public class SubscriberService {

	@Autowired
	private SubscriberRepository subscriberRepository;

	@Autowired
	private SubscrUserRepository subscrUserRepository;

	@Autowired
	private ContZPointRepository contZPointRepository;

	@Autowired
	private ContEventRepository contEventRepository;

	@PersistenceContext
	private EntityManager em;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Subscriber findOne(long id) {
		return subscriberRepository.findOne(id);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<ContObject> selectSubscriberContObjects(long subscriberId) {
		List<ContObject> result = subscriberRepository
				.selectContObjects(subscriberId);
		return result;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Long> selectSubscriberContObjectIds(long subscriberId) {
		List<Long> result = subscriberRepository
				.selectContObjectIds(subscriberId);
		return result;
	}

	/**
	 * 
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	@Deprecated
	public List<ContZPoint> findContZPoints(long contObjectId) {
		List<ContZPoint> result = contZPointRepository
				.findByContObjectId(contObjectId);
		return result;
	}

	/**
	 * 
	 * @param subscrUserId
	 * @return
	 */
	@Transactional(readOnly = true)
	public SubscrUser findSubscrUser(long subscrUserId) {
		return subscrUserRepository.findOne(subscrUserId);
	}

	/**
	 * 
	 * @param userName
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SubscrUser> findUserByUsername(String userName) {
		return subscrUserRepository.findByUserNameIgnoreCase(userName);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param contObjectId
	 * @return
	 */
	@Transactional(readOnly = true)
	public boolean checkContObjectSubscription(long subscriberId,
			long contObjectId) {
		List<Long> resultIds = subscriberRepository.selectContObjectId(
				subscriberId, contObjectId);
		return resultIds.size() > 0;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public Date getSubscriberCurrentTime(Long subscriberId) {
		Object dbResult = em
				.createNativeQuery("SELECT get_subscriber_current_time(?1);")
				.setParameter(1, subscriberId).getSingleResult();
		if (dbResult == null) {
			return null;
		}
		return (Date) dbResult;
	}

}
