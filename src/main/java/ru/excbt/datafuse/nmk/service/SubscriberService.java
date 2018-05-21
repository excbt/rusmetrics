package ru.excbt.datafuse.nmk.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.domain.QAbstractPersistableEntity;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.QSubscriber;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.SubscriberDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.support.EntityActions;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.model.vo.SubscriberOrganizationVO;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.data.service.SystemParamService;
import ru.excbt.datafuse.nmk.data.service.TimezoneDefService;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.mapper.SubscriberMapper;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.utils.WhereClauseBuilder;
import ru.excbt.datafuse.nmk.service.vm.SubscriberVM;

/**
 * Сервис для работы с абонентами
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 24.03.2015
 *
 */
@Service
public class SubscriberService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscriberService.class);

	private final static String LDAP_DESCRIPTION_SUFFIX_PARAM = "LDAP_CABINETS_DESCRIPTION_SUFFIX";
	private final static String LDAP_DESCRIPTION_SUFFIX_DEFAULT = "Cabinets-";

    public enum SubscriberMode {
        NORMAL,
        RMA,
        CABINET
    }

    private static QSubscriber qSubscriber = QSubscriber.subscriber;

    @PersistenceContext(unitName = "nmk-p")
    protected EntityManager em;

	protected final SubscriberRepository subscriberRepository;

	protected final SubscrUserRepository subscrUserRepository;

	protected final ContZPointRepository contZPointRepository;

	protected final TimezoneDefService timezoneDefService;


	private final SystemParamService systemParamService;

	private final OrganizationRepository organizationRepository;

    protected final SubscriberMapper subscriberMapper;

    protected final SubscriberTimeService subscriberTimeService;

    @Autowired
    public SubscriberService(SubscriberRepository subscriberRepository, SubscrUserRepository subscrUserRepository, ContZPointRepository contZPointRepository, TimezoneDefService timezoneDefService, SystemParamService systemParamService, OrganizationRepository organizationRepository, SubscriberMapper subscriberMapper, SubscriberTimeService subscriberTimeService) {
        this.subscriberRepository = subscriberRepository;
        this.subscrUserRepository = subscrUserRepository;
        this.contZPointRepository = contZPointRepository;
        this.timezoneDefService = timezoneDefService;
        this.systemParamService = systemParamService;
        this.organizationRepository = organizationRepository;
        this.subscriberMapper = subscriberMapper;
        this.subscriberTimeService = subscriberTimeService;
    }

    /**
     *
     * @param subscriberId
     * @return
     */
    @Deprecated
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Subscriber selectSubscriber(Long subscriberId) {
		Subscriber result = subscriberRepository.findOne(subscriberId);
		if (result == null) {
			throw new PersistenceException(String.format("Subscriber(id=%d) is not found", subscriberId));
		}

		return result;
	}

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Optional<SubscriberDTO> findSubscriberDTO(Long subscriberId) {
		return Optional.ofNullable(subscriberMapper.toDto(subscriberRepository.findOne(subscriberId)));
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_CREATE_CABINET })
	@Transactional(value = TxConst.TX_DEFAULT)
	public Subscriber saveSubscriber(Subscriber entity) {
		return subscriberRepository.saveAndFlush(entity);
	}

    /**
     *
     * @param subscriberDTO
     * @return
     */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_CREATE_CABINET })
	@Transactional(value = TxConst.TX_DEFAULT)
	public Subscriber saveSubscriberDTO(SubscriberDTO subscriberDTO) {
		return subscriberRepository.saveAndFlush(subscriberMapper.toEntity(subscriberDTO));
	}

	/**
	 *
	 * @param entity
	 */
	@Secured({ ROLE_ADMIN, ROLE_SUBSCR_CREATE_CABINET })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteSubscriber(Subscriber entity) {
		checkNotNull(entity);
		subscriberRepository.save(EntityActions.softDelete(entity));
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Optional<Subscriber> findOneSubscriber(Long subscriberId) {
		return Optional.ofNullable(subscriberRepository.findOne(subscriberId));
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

//	/**
//	 *
//	 * @param subscriberId
//	 * @return
//	 */
//	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
//	public String getRmaLdapOu(Long subscriberId) {
//		Subscriber subscriber = subscriberRepository.findOne(subscriberId);
//		if (subscriber == null) {
//			return null;
//		}
//		if (Boolean.TRUE.equals(subscriber.getIsRma())) {
//			return subscriber.getRmaLdapOu();
//		}
//
//		if (subscriber.getRmaLdapOu() != null) {
//			return subscriber.getRmaLdapOu();
//		}
//
//		if (subscriber.getRmaSubscriberId() == null) {
//			return null;
//		}
//
//		Subscriber rmaSubscriber = subscriberRepository.findOne(subscriber.getRmaSubscriberId());
//		return rmaSubscriber == null ? null : rmaSubscriber.getRmaLdapOu();
//	}

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

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscriberDTO> selectSubscribers(PortalUserIds userIds) {


        List<Subscriber> result = subscriberRepository.selectSubscribers(userIds.getSubscriberId());
        return result.stream()
            .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .map(subscriberMapper::toDto).collect(Collectors.toList());
	}

    private static com.querydsl.core.types.dsl.BooleanExpression searchCondition(String s) {
        if (s.isEmpty()) {
            return null;
        }
        return qSubscriber.subscriberName.toUpperCase().like(QueryDSLUtil.upperCaseLikeStr.apply(s));
    }


    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public Page<SubscriberDTO> selectSubscribers(PortalUserIds userIds,
                                                 SubscriberMode subscriberMode,
                                                 Optional<String> searchStringOptional,
                                                 Pageable pageable) {

        QAbstractPersistableEntity qPersistableEntity = new QAbstractPersistableEntity(qSubscriber);

        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(qSubscriber.deleted.eq(0))
            .and(qPersistableEntity.id.ne(userIds.getSubscriberId()));

        if (subscriberMode == SubscriberMode.RMA) {
            where.and(qSubscriber.subscrType.eq(SubscrTypeKey.RMA.getKeyname()));
        } else if (subscriberMode == SubscriberMode.NORMAL) {
            where.and(qSubscriber.subscrType.eq(SubscrTypeKey.NORMAL.getKeyname()));
            where.and(qSubscriber.parentSubscriberId.eq(userIds.getSubscriberId()));
        } else if (subscriberMode == SubscriberMode.CABINET) {
            where.and(qSubscriber.subscrType.eq(SubscrTypeKey.CABINET.getKeyname()));
            where.and(qSubscriber.parentSubscriberId.eq(userIds.getSubscriberId()));
        }

        searchStringOptional.ifPresent(s -> where.and(searchCondition(s)));

        Page<Subscriber> result = subscriberRepository.findAll(where, pageable);
        return result.map(subscriberMapper::toDto);
	}

    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public <T> Page<T> selectSubscribers2(PortalUserIds userIds,
                                                 SubscriberMode subscriberMode,
                                                 Optional<String> searchStringOptional,
                                                 Function<Subscriber, T> mapper,
                                                 Pageable pageable) {

        QAbstractPersistableEntity qPersistableEntity = new QAbstractPersistableEntity(qSubscriber);

        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(qSubscriber.deleted.eq(0))
            .and(qPersistableEntity.id.ne(userIds.getSubscriberId()));

        if (subscriberMode == SubscriberMode.RMA) {
            where.and(qSubscriber.subscrType.eq(SubscrTypeKey.RMA.getKeyname()));
        } else if (subscriberMode == SubscriberMode.NORMAL) {
            where.and(qSubscriber.subscrType.eq(SubscrTypeKey.NORMAL.getKeyname()));
            where.and(qSubscriber.rmaSubscriberId.eq(userIds.getSubscriberId()));
        } else if (subscriberMode == SubscriberMode.CABINET) {
            where.and(qSubscriber.subscrType.eq(SubscrTypeKey.CABINET.getKeyname()));
            where.and(qSubscriber.parentSubscriberId.eq(userIds.getSubscriberId()));
        }

        searchStringOptional.ifPresent(s -> where.and(searchCondition(s)));

        Page<Subscriber> result = subscriberRepository.findAll(where, pageable);
        return result.map(mapper::apply);
	}


    @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
    public List<SubscriberVM> findSubscribersManageList(PortalUserIds userIds) {

        QAbstractPersistableEntity qPersistableEntity = new QAbstractPersistableEntity(qSubscriber);

        BooleanExpression subscriberFilter = qSubscriber.rmaSubscriberId.eq(userIds.getSubscriberId());


        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(qSubscriber.deleted.eq(0))
            .and(qPersistableEntity.id.ne(userIds.getSubscriberId()))
            .and(subscriberFilter);

        Sort sorting = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<SubscriberVM> resultList = new ArrayList<>();
        subscriberRepository.findAll(where,sorting).forEach(i -> resultList.add(subscriberMapper.toVM(i)));

        return resultList;
    }

//	/**
//	 *
//	 * @param subscriber
//	 * @return
//	 */
//	public String[] buildSubscriberLdapOu(Subscriber subscriber) {
//		checkNotNull(subscriber);
//
//		String rmaOu = null;
//		String childLdapOu = null;
//		String[] orgUnits = null;
//
//		if (Boolean.TRUE.equals(subscriber.getIsChild())) {
//			rmaOu = getRmaLdapOu(subscriber.getParentSubscriberId());
//			Subscriber parentSubscriber = selectSubscriber(subscriber.getParentSubscriberId());
//			checkNotNull(parentSubscriber);
//
//			childLdapOu = parentSubscriber.getChildLdapOu();
//
//			orgUnits = new String[] { rmaOu, childLdapOu };
//
//		} else {
//			rmaOu = getRmaLdapOu(subscriber.getId());
//			orgUnits = new String[] { rmaOu };
//		}
//
//		checkNotNull(orgUnits);
//
//		return orgUnits;
//	}

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

		if (subscribers.isEmpty()) {
			return new ArrayList<>();
		}

		long[] organizationIds = subscribers.stream().filter(i -> i.getOrganization() != null)
				.mapToLong(i -> i.getOrganization().getId()).toArray();

		final List<Organization> organizations = organizationIds.length == 0 ? new ArrayList<>()
				: organizationRepository.selectByIds(Arrays.asList(ArrayUtils.toObject(organizationIds)));

		final Map<Long, Organization> organizationsMap = organizations.stream()
				.collect(Collectors.toMap(Organization::getId, Function.identity()));

		return subscribers.stream()
				.filter(i -> i.getOrganization() != null)
                .map(i -> new SubscriberOrganizationVO(i, organizationsMap.get(i.getOrganization().getId())))
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


    /**
     *
     * @param portalUserIds
     * @return
     */
	public Long getRmaSubscriberId(PortalUserIds portalUserIds) {
	    Subscriber s = Optional.ofNullable(subscriberRepository.findOne(portalUserIds.getSubscriberId()))
            .orElseThrow(() -> DBExceptionUtil.newEntityNotFoundException(Subscriber.class, portalUserIds.getSubscriberId()));
	    return s.getIsRma() ? s.getId() : s.getRmaSubscriberId();
    }


	@Transactional(readOnly = true)
	public List<SubscriberDTO> findByRmaSubscriberId(Long rmaSubscriberId) {
		return subscriberRepository.findByRmaSubscriberId(rmaSubscriberId).stream()
            .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .map(s -> subscriberMapper.toDto(s)).collect(Collectors.toList());
	}

    @Transactional(readOnly = true)
    public List<SubscriberDTO> findByRmaSubscriber(PortalUserIds portalUserIds) {
	    if (!portalUserIds.isRma()) {
	        return Collections.emptyList();
        }
        return subscriberRepository.findByRmaSubscriberId(portalUserIds.getSubscriberId()).stream()
            .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .map(s -> subscriberMapper.toDto(s)).collect(Collectors.toList());
    }


    /**
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<SubscriberDTO> findAllRma() {
        return subscriberRepository.finaAllRma().stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .map(s -> subscriberMapper.toDto(s)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean checkParentSubscriber(Long subcriberId, PortalUserIds portalUserIds) {
        Subscriber subscriber = subscriberRepository.findOne(subcriberId);
        if (subscriber == null) {
            return false;
        }
        return portalUserIds.getSubscriberId().equals(subscriber.getRmaSubscriberId());
    }
    //public void
}
