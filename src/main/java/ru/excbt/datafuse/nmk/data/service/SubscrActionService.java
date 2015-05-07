package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.repository.SubscrActionGroupRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrActionUserGroupRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrActionUserRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
@Transactional
public class SubscrActionService implements SecuredRoles {

	@Autowired
	private SubscrActionGroupRepository subscrActionGroupRepository;

	@Autowired
	private SubscrActionUserRepository subscrActionUserRepository;

	@Autowired
	private SubscrActionUserGroupRepository subscrActionUserGroupRepository;

	/**
	 * 
	 * @param subscriberId
	 * @param subscrActionUserId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SubscrActionGroup> selectUserGroups(long subscriberId,
			long subscrActionUserId) {
		return subscrActionUserGroupRepository.selectUserGroups(subscriberId,
				subscrActionUserId);
	}

	/**
	 * 
	 * @param subscriberId
	 * @param subscrActionGroupId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SubscrActionUser> selectGroupUsers(long subscriberId,
			long subscrActionGroupId) {
		return subscrActionUserGroupRepository.selectGroupUsers(subscriberId,
				subscrActionGroupId);
	}
}
