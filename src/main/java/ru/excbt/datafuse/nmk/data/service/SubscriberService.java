package ru.excbt.datafuse.nmk.data.service;

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

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.service.support.AbstractService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

/**
 * Сервис для работы с абонентами
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.03.2015
 *
 */
@Service
public class SubscriberService extends AbstractService implements SecuredRoles {

	@Autowired
	protected SubscriberRepository subscriberRepository;

	@Autowired
	protected SubscrUserRepository subscrUserRepository;

	@Autowired
	protected ContZPointRepository contZPointRepository;

	@PersistenceContext(unitName = "nmk-p")
	protected EntityManager em;

	@Autowired
	protected TimezoneDefService timezoneDefService;

	@Autowired
	protected SubscrServiceAccessService subscrServiceAccessService;

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
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_CREATE_CABINET })
	@Transactional(value = TxConst.TX_DEFAULT)
	public Subscriber saveSubscriber(Subscriber entity) {
		return subscriberRepository.save(entity);
	}

	/**
	 * 
	 * @param entity
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_CREATE_CABINET })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteSubscriber(Subscriber entity) {
		subscriberRepository.delete(entity);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Subscriber findOneSubscriber(long subscriberId) {
		Subscriber result = subscriberRepository.findOne(subscriberId);
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
		checkNotNull(subscriberId);
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
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Organization> selectRsoOrganizations2(Long subscriberId) {
		return subscriberRepository.selectRsoOrganizations(subscriberId);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public boolean checkSubscriberId(Long subscriberId) {
		List<Long> ids = subscriberRepository.checkSubscriberId(subscriberId);
		return ids.size() == 1;
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

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public Subscriber getLdapSubscriber(Long subscriberId) {
		Subscriber subscriber = subscriberRepository.findOne(subscriberId);
		if (subscriber == null) {
			return null;
		}
		if (Boolean.TRUE.equals(subscriber.getIsRma())) {
			return subscriber;
		}

		if (subscriber.getRmaLdapOu() != null) {
			return subscriber;
		}

		if (subscriber.getRmaSubscriberId() == null) {
			return null;
		}

		Subscriber rmaSubscriber = subscriberRepository.findOne(subscriber.getRmaSubscriberId());
		return rmaSubscriber == null ? null : subscriber;
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Subscriber> findAllSubscribers() {
		return Lists.newArrayList(subscriberRepository.findAll());
	}

	/**
	 * 
	 * @param parentSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Subscriber> selectChildSubscribers(Long parentSubscriberId) {
		return subscriberRepository.selectChildSubscribers(parentSubscriberId);
	}

}
