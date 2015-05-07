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

@Service
@Transactional
public class SubscrActionUserService implements SecuredRoles{

	@Autowired
	private SubscrActionUserRepository subscrActionUserRepository;
	
	
	
	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<SubscrActionUser> findAll(long subscriberId) {
		return subscrActionUserRepository.findBySubscriberId(subscriberId);
	}	

	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public SubscrActionUser findOne(long id) {
		return subscrActionUserRepository.findOne(id);
	}
	

	/**
	 * 
	 * @param entity
	 * @return
	 */
	@Secured({ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })
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
	@Secured({ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })	
	public SubscrActionUser createOne(SubscrActionUser entity) {
		checkNotNull(entity);
		checkArgument(entity.isNew());
		checkNotNull(entity.getSubscriber());
		return subscrActionUserRepository.save(entity);
	}


	/**
	 * 
	 * @param id
	 */
	@Secured({ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })	
	public void deleteOne(long id) {
		if (subscrActionUserRepository.exists(id)) {
			subscrActionUserRepository.delete(id);
		} else {
			throw new PersistenceException(String.format(
					"Object %s(id=%d) is not found",
					SubscrActionUser.class.getName(), id));
			
		}
	}


	/**
	 * 
	 * @param entity
	 */
	@Secured({ROLE_SUBSCR_USER, ROLE_SUBSCR_ADMIN })	
	public void deleteOne(SubscrActionUser entity) {
		checkNotNull(entity);
		subscrActionUserRepository.delete(entity);
	}	
	
}
