package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.repository.SubscrActionGroupRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

/**
 * Сервис для работы с привязкой заданий абонентов и пользователей
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.05.2015
 *
 */
@Service
public class SubscrActionGroupService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrActionGroupService.class);

	@Autowired
	private SubscrActionGroupRepository subscrActionGroupRepository;

	@Autowired
	private SubscrActionUserGroupService subscrActionUserGroupService;

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<SubscrActionGroup> findAll(long subscriberId) {
		return subscrActionGroupRepository.findBySubscriberId(subscriberId);
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Transactional( readOnly = true)
	public SubscrActionGroup findOne(long id) {
		return subscrActionGroupRepository.findOne(id);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public SubscrActionGroup updateOne(SubscrActionGroup entity) {
		checkArgument(!entity.isNew());
		checkNotNull(entity.getSubscriber());
		return subscrActionGroupRepository.save(entity);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public SubscrActionGroup updateOne(SubscrActionGroup entity, Long[] userIds) {
		checkArgument(!entity.isNew());
		checkNotNull(entity.getSubscriber());

		SubscrActionGroup resultEntity = subscrActionGroupRepository.save(entity);

		if (userIds != null) {
			subscrActionUserGroupService.updateGroupToUsers(resultEntity.getId(), userIds);
		}

		return resultEntity;

	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public SubscrActionGroup createOne(SubscrActionGroup entity) {
		checkArgument(entity.isNew());
		checkNotNull(entity.getSubscriber());
		return subscrActionGroupRepository.save(entity);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public SubscrActionGroup createOne(SubscrActionGroup entity, Long[] userIds) {
		checkArgument(entity.isNew());
		checkNotNull(entity.getSubscriber());

		SubscrActionGroup resultEntity = subscrActionGroupRepository.save(entity);

		if (userIds != null) {
			subscrActionUserGroupService.updateGroupToUsers(resultEntity.getId(), userIds);
		}

		return resultEntity;
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(SubscrActionGroup entity) {
		checkArgument(!entity.isNew());
		subscrActionGroupRepository.delete(entity);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(long id) {
		if (subscrActionGroupRepository.exists(id)) {
			subscrActionUserGroupService.deleteByGroup(id);
			subscrActionGroupRepository.delete(id);
		} else {
			throw new PersistenceException(
					String.format("Object %s(id=%d) is not found", SubscrActionGroup.class.getName(), id));
		}

	}

}
