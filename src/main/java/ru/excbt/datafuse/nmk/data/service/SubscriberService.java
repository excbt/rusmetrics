package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContEvent;
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

	// @Autowired
	// private ContObjectRepository contObjectRepository;

	@Transactional(readOnly = true)
	public Subscriber findOne(long id) {
		return subscriberRepository.findOne(id);
	}

	@Transactional(readOnly = true)
	public List<ContObject> selectSubscriberContObjects(long subscriberId) {
		List<ContObject> result = subscriberRepository
				.selectContObjects(subscriberId);
		return result;
	}

	@Transactional(readOnly = true)
	@Deprecated
	public List<ContZPoint> findContZPoints(long contObjectId) {
		List<ContZPoint> result = contZPointRepository
				.findByContObjectId(contObjectId);
		return result;
	}

	@Transactional(readOnly = true)
	@Deprecated
	public List<ContEvent> findContObjectEvents(long contObjectId) {
		List<ContEvent> result = contEventRepository
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
	 * @param subscrUserId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Subscriber> selectSubscrRoles(long subscrUserId) {
		checkArgument(subscrUserId > 0);
		return subscriberRepository.selectByUserId(subscrUserId);
	}

	@Transactional(readOnly = true)
	public boolean checkContObjectSubscription(long subscriberId,
			long contObjectId) {
		List<Long> resultIds = subscriberRepository.selectContObjectId(
				subscriberId, contObjectId);
		return resultIds.size() > 0;
	}

}
