package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrUserService extends AbstractService implements SecuredRoles {

	@Autowired
	private SubscrUserRepository subscrUserRepository;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @param subscrUserId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrRole> selectSubscrRoles(long subscrUserId) {
		return subscrUserRepository.selectSubscrRoles(subscrUserId);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrUser> findBySubscriberId(Long subscriberId) {
		List<SubscrUser> resultList = subscrUserRepository.findBySubscriberId(subscriberId);
		resultList.forEach(i -> {
			i.getSubscriber().getId();
		});
		return resultList;
	}

	/**
	 * 
	 * @param subscrUserId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUser findOne(Long subscrUserId) {
		return subscrUserRepository.findOne(subscrUserId);
	}

	/**
	 * 
	 * @param subscrUser
	 * @return
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUser createOne(SubscrUser subscrUser) {
		checkNotNull(subscrUser);
		checkArgument(subscrUser.isNew());
		checkNotNull(subscrUser.getUserName());
		checkNotNull(subscrUser.getSubscriberId());
		checkNotNull(subscrUser.getSubscrRoles());

		Subscriber subscriber = subscriberService.findOne(subscrUser.getSubscriberId());
		subscrUser.setSubscriber(subscriber);

		return subscrUserRepository.save(subscrUser);
	}

	/**
	 * 
	 * @param subscrUser
	 * @return
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrUser updateOne(SubscrUser subscrUser) {
		checkNotNull(subscrUser);
		checkArgument(!subscrUser.isNew());
		checkNotNull(subscrUser.getUserName());
		checkNotNull(subscrUser.getSubscriberId());
		checkNotNull(subscrUser.getSubscrRoles());

		Subscriber subscriber = subscriberService.findOne(subscrUser.getSubscriberId());
		subscrUser.setSubscriber(subscriber);

		return subscrUserRepository.save(subscrUser);
	}

	/**
	 * 
	 * @param subscrUserId
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteOne(Long subscrUserId) {
		checkNotNull(subscrUserId);

		SubscrUser subscrUser = subscrUserRepository.findOne(subscrUserId);
		if (subscrUser == null) {
			throw new PersistenceException(String.format("SubscrUser (id=%d) is not found", subscrUserId));
		}
		subscrUserRepository.save(softDelete(subscrUser));
	}

	/**
	 * 
	 * @param subscrUserId
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteOnePermanent(Long subscrUserId) {
		checkNotNull(subscrUserId);

		SubscrUser subscrUser = subscrUserRepository.findOne(subscrUserId);
		if (subscrUser == null) {
			throw new PersistenceException(String.format("SubscrUser (id=%d) is not found", subscrUserId));
		}
		subscrUserRepository.delete(subscrUser);
	}

}
