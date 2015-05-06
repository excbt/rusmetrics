package ru.excbt.datafuse.nmk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.repository.SubscrActionGroupRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrActionUserRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


@Service
@Transactional
public class SubscrActionService implements SecuredRoles {

	@Autowired
	private SubscrActionGroupRepository subscrActionGroupRepository;
	
	@Autowired
	private SubscrActionUserRepository subscrActionUserRepository;

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SubscrActionGroup> findActionGroup(long subscriberId) {
		return subscrActionGroupRepository.findBySubscriberId(subscriberId);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public SubscrActionGroup findActionGroupOne(long id) {
		return subscrActionGroupRepository.findOne(id);
	}
	
	
	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SubscrActionUser> findActionUser(long subscriberId) {
		return subscrActionUserRepository.findBySubscriberId(subscriberId);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public SubscrActionUser findActionUserOne(long id) {
		return subscrActionUserRepository.findOne(id);
	}
	
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Secured ({ROLE_ADMIN, SUBSCR_ROLE_ADMIN})
	public SubscrActionGroup updateOneGroup(SubscrActionGroup entity) {
		checkArgument(!entity.isNew());
		checkNotNull(entity.getSubscriber());
		return subscrActionGroupRepository.save(entity);
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Secured ({ROLE_ADMIN, SUBSCR_ROLE_ADMIN})
	public SubscrActionUser updateOneUser(SubscrActionUser entity) {
		checkArgument(!entity.isNew());
		checkNotNull(entity.getSubscriber());
		return subscrActionUserRepository.save(entity);
	}
}
