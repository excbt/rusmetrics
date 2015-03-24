package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.repository.ContEventRepository;
import ru.excbt.datafuse.nmk.data.repository.ContObjectRepository;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrRoleRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;

@Service
@Transactional
public class SubscriberService {

	@Autowired
	private SubscrUserRepository subscrUserRepository;

	@Autowired
	private SubscrRoleRepository subscrRoleRepository;

	@Autowired
	private ContZPointRepository contZPointRepository;

	@Autowired
	private ContEventRepository contEventRepository;

	@Autowired
	private ContObjectRepository contObjectRepository;

	@Transactional(readOnly = true)
	@Deprecated
	public List<ContObject> selectSubscrContObjects(long userId) {
		List<ContObject> result = contObjectRepository
				.selectSubscrContObjects(userId);
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
	public List<ContEvent> findContEvents(long contObjectId) {
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
	public SubscrUser findOne(long subscrUserId) {
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
	public List<SubscrRole> selectSubscrRoles(long subscrUserId) {
		checkArgument(subscrUserId > 0);
		return subscrRoleRepository.selectByUserId(subscrUserId);
	}
	
}
