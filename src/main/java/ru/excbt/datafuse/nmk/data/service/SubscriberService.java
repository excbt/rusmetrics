package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.vo.SubscriberOrganizationVO;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;
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

	private static final Logger logger = LoggerFactory.getLogger(SubscriberService.class);

	private final static String LDAP_DESCRIPTION_SUFFIX_PARAM = "LDAP_CABINETS_DESCRIPTION_SUFFIX";
	private final static String LDAP_DESCRIPTION_SUFFIX_DEFAULT = "Cabinets-";

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

	@Autowired
	private SystemParamService systemParamService;

	@Autowired
	private OrganizationRepository organizationRepository;

	/**
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Subscriber selectSubscriber(Long subscriberId) {
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
	private Subscriber findOne2(Long subscriberId) {
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
		checkNotNull(entity);
		subscriberRepository.save(softDelete(entity));
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Subscriber findOneSubscriber(Long subscriberId) {
		Subscriber result = subscriberRepository.findOne(subscriberId);
		return result;
	}

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

		Query q = em.createNativeQuery("SELECT get_subscriber_current_time(?1);");
		Object dbResult = q.setParameter(1, subscriberId).getSingleResult();
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
		List<Subscriber> result = subscriberRepository.selectChildSubscribers(parentSubscriberId);
		return ObjectFilters.deletedFilter(result);
	}

	/**
	 * 
	 * @param subscriber
	 * @return
	 */
	public String[] buildSubscriberLdapOu(Subscriber subscriber) {
		checkNotNull(subscriber);

		String rmaOu = null;
		String childLdapOu = null;
		String[] orgUnits = null;

		if (Boolean.TRUE.equals(subscriber.getIsChild())) {
			rmaOu = getRmaLdapOu(subscriber.getParentSubscriberId());
			Subscriber parentSubscriber = selectSubscriber(subscriber.getParentSubscriberId());
			checkNotNull(parentSubscriber);

			childLdapOu = parentSubscriber.getChildLdapOu();

			orgUnits = new String[] { rmaOu, childLdapOu };

		} else {
			rmaOu = getRmaLdapOu(subscriber.getId());
			orgUnits = new String[] { rmaOu };
		}

		checkNotNull(orgUnits);

		return orgUnits;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	public String buildCabinetsOuName(Long subscriberId) {
		checkNotNull(subscriberId);
		return "Cabinets-" + subscriberId;
	}

	/**
	 * 
	 * @param subscriber
	 * @return
	 */
	public String buildChildDescription(Subscriber subscriber) {
		checkNotNull(subscriber);
		checkNotNull(subscriber.getSubscriberName());
		String suffix = null;
		try {
			suffix = systemParamService.getParamValueAsString(LDAP_DESCRIPTION_SUFFIX_PARAM);
		} catch (Exception e) {
			logger.warn("System param {} not found", LDAP_DESCRIPTION_SUFFIX_PARAM);
		}

		if (suffix == null || suffix.isEmpty()) {
			logger.warn("System param {} is empty use default: {}", LDAP_DESCRIPTION_SUFFIX_PARAM,
					LDAP_DESCRIPTION_SUFFIX_DEFAULT);
			suffix = LDAP_DESCRIPTION_SUFFIX_DEFAULT;
		}

		return suffix + subscriber.getSubscriberName();
	}

	/**
	 * 
	 * @param subscribers
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscriberOrganizationVO> enhanceSubscriber(final List<Subscriber> subscribers) {
		checkNotNull(subscribers);
		long[] organizationIds = subscribers.stream().filter(i -> i.getOrganizationId() != null)
				.mapToLong(i -> i.getOrganizationId()).toArray();

		final List<Organization> organizations = organizationRepository
				.selectByIds(Arrays.asList(ArrayUtils.toObject(organizationIds)));

		final Map<Long, Organization> organizationsMap = organizations.stream()
				.collect(Collectors.toMap(Organization::getId, Function.identity()));

		return subscribers.stream()
				.map(i -> new SubscriberOrganizationVO(i, organizationsMap.get(i.getOrganizationId())))
				.collect(Collectors.toList());
	}

	/**
	 * 
	 * @param subscriber
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscriberOrganizationVO enhanceSubscriber(Subscriber subscriber) {
		return checkNotNull(enhanceSubscriber(Arrays.asList(subscriber))).get(0);
	}
}
