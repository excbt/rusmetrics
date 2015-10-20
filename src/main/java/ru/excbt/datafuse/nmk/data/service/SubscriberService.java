package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscriberService extends AbstractService implements SecuredRoles {

	@Autowired
	private SubscriberRepository subscriberRepository;

	@Autowired
	private SubscrUserRepository subscrUserRepository;

	@Autowired
	private ContZPointRepository contZPointRepository;

	@PersistenceContext(unitName = "nmk-p")
	private EntityManager em;

	@Autowired
	private SubscrContObjectService subscrContObjectService;

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
	// @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	// public List<ContObject> selectSubscriberContObjects(long subscriberId) {
	// List<ContObject> result =
	// subscriberRepository.selectContObjects(subscriberId);
	// return result;
	// }

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	// @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	// public List<ContObject> selectRmaSubscriberContObjects(long subscriberId)
	// {
	// List<ContObject> result =
	// subscriberRepository.selectContObjects(subscriberId);
	// List<Long> subscrContObjectIds =
	// subscrContObjectService.selectRmaSubscrContObjectIds(subscriberId);
	// Set<Long> subscrContObjectIdMap = new HashSet<>(subscrContObjectIds);
	// result.forEach(i -> {
	// boolean haveSubscr = subscrContObjectIdMap.contains(i.getId());
	// i.set_haveSubscr(haveSubscr);
	// });
	// return result;
	// }

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	// @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	// public List<Long> selectSubscriberContObjectIds(long subscriberId) {
	// List<Long> result =
	// subscriberRepository.selectContObjectIds(subscriberId);
	// return result;
	// }

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
		List<SubscrUser> userList = subscrUserRepository.findByUserNameIgnoreCase(userName);
		List<SubscrUser> result = userList.stream().filter(i -> i.getId() > 0).collect(Collectors.toList());
		result.forEach(i -> {
			i.getSubscriber();
		});

		return result;
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
	@Transactional(value = TxConst.TX_DEFAULT)
	public LocalDate getSubscriberCurrentDateJoda(Long subscriberId) {
		Date currentDate = getSubscriberCurrentTime(subscriberId);
		return new LocalDate(currentDate);
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Subscriber> selectRmaSubscribers(Long rmaSubscriberId) {
		return subscriberRepository.findByRmaSubscriberId(rmaSubscriberId);
	}

	/**
	 * 
	 * @param subscriber
	 * @param rmaSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public Subscriber createRmaSubscriber(Subscriber subscriber, Long rmaSubscriberId) {
		checkNotNull(subscriber);
		checkNotNull(rmaSubscriberId);
		checkArgument(subscriber.isNew());
		subscriber.setRmaSubscriberId(rmaSubscriberId);
		checkArgument(!Boolean.TRUE.equals(subscriber.getIsRma()));

		subscriber.setOrganization(findOrganization(subscriber.getOrganizationId()));

		return subscriberRepository.save(subscriber);
	}

	/**
	 * 
	 * @param subscriber
	 * @param rmaSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public Subscriber updateRmaSubscriber(Subscriber subscriber, Long rmaSubscriberId) {
		checkNotNull(subscriber);
		checkNotNull(rmaSubscriberId);
		checkArgument(!subscriber.isNew());
		subscriber.setRmaSubscriberId(rmaSubscriberId);
		checkArgument(!Boolean.TRUE.equals(subscriber.getIsRma()));

		return subscriberRepository.save(subscriber);
	}

	/**
	 * 
	 * @param subscriber
	 * @param rmaSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public void deleteRmaSubscriber(Long subscriberId, Long rmaSubscriberId) {
		checkNotNull(subscriberId);
		checkNotNull(rmaSubscriberId);

		Subscriber subscriber = findOne(subscriberId);
		if (!rmaSubscriberId.equals(subscriber.getRmaSubscriberId())) {
			throw new PersistenceException(String.format("Can't delete Subscriber (id=%d). Invalid RMA", subscriberId));
		}
		subscriberRepository.save(softDelete(subscriber));
	}

	/**
	 * 
	 * @param subscriberId
	 * @param rmaSubscriberId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public void deleteRmaSubscriberPermanent(Long subscriberId, Long rmaSubscriberId) {
		checkNotNull(subscriberId);
		checkNotNull(rmaSubscriberId);

		Subscriber subscriber = findOne(subscriberId);
		if (!rmaSubscriberId.equals(subscriber.getRmaSubscriberId())) {
			throw new PersistenceException(String.format("Can't delete Subscriber (id=%d). Invalid RMA", subscriberId));
		}
		subscriberRepository.delete(subscriber);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public String getRmaLdapOu(Long subscriberId) {
		Subscriber subscriber = subscriberRepository.findOne(subscriberId);
		if (subscriber == null) {
			return null;
		}
		if (Boolean.TRUE.equals(subscriber.getIsRma())) {
			return subscriber.getRmaLdapOu();
		}

		if (subscriber.getRmaLdapOu() != null) {
			return subscriber.getRmaLdapOu();
		}

		if (subscriber.getRmaSubscriberId() == null) {
			return null;
		}

		Subscriber rmaSubscriber = subscriberRepository.findOne(subscriber.getRmaSubscriberId());
		return rmaSubscriber == null ? null : rmaSubscriber.getRmaLdapOu();
	}

}
