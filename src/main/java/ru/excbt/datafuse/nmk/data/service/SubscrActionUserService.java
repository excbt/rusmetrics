package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.repository.SubscrActionUserRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.web.rest.errors.EntityNotFoundException;

/**
 * Сервис для работы с пользователями заданий абонентов
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 07.05.2015
 *
 */
@Service
public class SubscrActionUserService implements SecuredRoles {

	@Autowired
	private SubscrActionUserRepository subscrActionUserRepository;

	@Autowired
	private SubscrActionUserGroupService subscrActionUserGroupService;

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<SubscrActionUser> findAll(long subscriberId) {
		return subscrActionUserRepository.findBySubscriberId(subscriberId);
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@Transactional( readOnly = true)
	public SubscrActionUser findOne(long id) {
		return subscrActionUserRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(SubscrActionUser.class, id));
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public SubscrActionUser updateOne(SubscrActionUser entity) {
		checkNotNull(entity);
		checkArgument(!entity.isNew());
		checkNotNull(entity.getSubscriber());
		return subscrActionUserRepository.save(entity);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public SubscrActionUser updateOne(SubscrActionUser entity, Long[] groupIds) {
		checkNotNull(entity);
		checkArgument(!entity.isNew());
		checkNotNull(entity.getSubscriber());

		SubscrActionUser result = subscrActionUserRepository.save(entity);

		if (groupIds != null) {
			subscrActionUserGroupService.updateUserToGroups(entity.getId(), groupIds);
		}

		return result;

	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public SubscrActionUser createOne(SubscrActionUser entity) {
		checkNotNull(entity);
		checkArgument(entity.isNew());
		checkNotNull(entity.getSubscriber());
		return subscrActionUserRepository.save(entity);
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public SubscrActionUser createOne(SubscrActionUser entity, Long[] groupIds) {
		checkNotNull(entity);
		checkArgument(entity.isNew());
		checkNotNull(entity.getSubscriber());
		SubscrActionUser user = subscrActionUserRepository.save(entity);

		if (groupIds != null) {
			subscrActionUserGroupService.updateUserToGroups(entity.getId(), groupIds);
		}

		return user;
	}

	/**
	 *
	 * @param id
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(long id) {
		if (subscrActionUserRepository.existsById(id)) {
			subscrActionUserGroupService.deleteByUser(id);
			subscrActionUserRepository.deleteById(id);
		} else {
			throw new EntityNotFoundException(SubscrActionUser.class, id);

		}
	}

	/**
	 *
	 * @param entity
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
	public void deleteOne(SubscrActionUser entity) {
		checkNotNull(entity);
		subscrActionUserRepository.delete(entity);
	}

}
