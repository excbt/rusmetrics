package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.dto.SubscriberDTO;
import ru.excbt.datafuse.nmk.data.model.support.EntityActions;
import ru.excbt.datafuse.nmk.data.model.types.SubscrTypeKey;
import ru.excbt.datafuse.nmk.data.repository.ContZPointRepository;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrUserRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscriberRepository;
import ru.excbt.datafuse.nmk.ldap.service.LdapService;
import ru.excbt.datafuse.nmk.service.mapper.SubscriberMapper;

/**
 * Сервис для работы с Абонентами РМА
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.11.2015
 *
 */
@Service
public class RmaSubscriberService extends SubscriberService {

	private static final Logger logger = LoggerFactory.getLogger(RmaSubscriberService.class);

	private final ReportParamsetService reportParamsetService;

	private final OrganizationService organizationService;

	private final LdapService ldapService;

	private final SubscrUserService subscrUserService;




    public RmaSubscriberService(SubscriberRepository subscriberRepository, SubscrUserRepository subscrUserRepository, ContZPointRepository contZPointRepository, TimezoneDefService timezoneDefService, SubscrServiceAccessService subscrServiceAccessService, SystemParamService systemParamService, OrganizationRepository organizationRepository, ReportParamsetService reportParamsetService, OrganizationService organizationService, LdapService ldapService, SubscrUserService subscrUserService, SubscriberMapper subscriberMapper) {
        super(subscriberRepository, subscrUserRepository, contZPointRepository, timezoneDefService, subscrServiceAccessService, systemParamService, organizationRepository, subscriberMapper);
        this.reportParamsetService = reportParamsetService;
        this.organizationService = organizationService;
        this.ldapService = ldapService;
        this.subscrUserService = subscrUserService;
    }

    /**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Subscriber> selectRmaSubscribers(Long rmaSubscriberId) {
		return subscriberRepository.selectByRmaSubscriberId(rmaSubscriberId);
	}

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscriberDTO> selectRmaSubscribersDTO(Long rmaSubscriberId) {
		return subscriberRepository.selectByRmaSubscriberId(rmaSubscriberId).stream()
            .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .map(s -> subscriberMapper.toDto(s)).collect(Collectors.toList());
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
		checkArgument(subscriber.getDeleted() == 0);

		subscriber.setSubscrType(SubscrTypeKey.NORMAL.getKeyname());

		Subscriber resultSubscriber = subscriberRepository.save(subscriber);

		// Can Create Child LDAP ou set
		if (BooleanUtils.isTrue(subscriber.getCanCreateChild())) {
			Subscriber s = selectSubscriber(subscriber.getId());
			if (s.getChildLdapOu() == null || s.getChildLdapOu().isEmpty()) {
				subscriber.setChildLdapOu(buildCabinetsOuName(subscriber.getId()));
			} else {
				subscriber.setChildLdapOu(s.getChildLdapOu());
			}
		}
		// End of can Create Child LDAP

		// Can Create Child LDAP action
		if (BooleanUtils.isTrue(subscriber.getCanCreateChild())) {
			String[] ldapOu = buildSubscriberLdapOu(subscriber);
			String childDescription = buildChildDescription(subscriber);
			ldapService.createOuIfNotExists(ldapOu, subscriber.getChildLdapOu(), childDescription);
		}
		// End of can Create Child LDAP action

		LocalDate accessDate = getSubscriberCurrentDateJoda(resultSubscriber.getId());
		subscrServiceAccessService.processAccessList(resultSubscriber.getId(), accessDate, new ArrayList<>());

		// Make default Report Paramset
		reportParamsetService.createDefaultReportParamsets(resultSubscriber);

		return resultSubscriber;
	}

	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public Subscriber createRmaSubscriber(SubscriberDTO subscriberDTO, Long rmaSubscriberId) {
		checkNotNull(subscriberDTO);
		checkNotNull(rmaSubscriberId);
		checkArgument(subscriberDTO.getId() == null);

		Subscriber subscriber = subscriberMapper.toEntity(subscriberDTO);

		subscriber.setRmaSubscriberId(rmaSubscriberId);
		checkArgument(!Boolean.TRUE.equals(subscriber.getIsRma()));
		checkArgument(subscriber.getDeleted() == 0);

		subscriber.setSubscrType(SubscrTypeKey.NORMAL.getKeyname());

		Subscriber resultSubscriber = subscriberRepository.saveAndFlush(subscriber);

		// Can Create Child LDAP ou set
		if (BooleanUtils.isTrue(subscriber.getCanCreateChild())) {
			Subscriber s = selectSubscriber(subscriber.getId());
			if (s.getChildLdapOu() == null || s.getChildLdapOu().isEmpty()) {
				subscriber.setChildLdapOu(buildCabinetsOuName(subscriber.getId()));
			} else {
				subscriber.setChildLdapOu(s.getChildLdapOu());
			}
		}
		// End of can Create Child LDAP

		// Can Create Child LDAP action
		if (BooleanUtils.isTrue(subscriber.getCanCreateChild())) {
			String[] ldapOu = buildSubscriberLdapOu(subscriber);
			String childDescription = buildChildDescription(subscriber);
			ldapService.createOuIfNotExists(ldapOu, subscriber.getChildLdapOu(), childDescription);
		}
		// End of can Create Child LDAP action

		LocalDate accessDate = getSubscriberCurrentDateJoda(resultSubscriber.getId());
		subscrServiceAccessService.processAccessList(resultSubscriber.getId(), accessDate, new ArrayList<>());

		// Make default Report Paramset
		reportParamsetService.createDefaultReportParamsets(resultSubscriber);

		return resultSubscriber;
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

		Subscriber checkSubscriber = subscriberRepository.findOne(subscriber.getId());
		if (checkSubscriber == null || checkSubscriber.getDeleted() == 1) {
			throw new PersistenceException(
					String.format("Subscriber (id=%d) is not found or deleted", subscriber.getId()));
		}

		// Can Create Child LDAP ou set
		if (BooleanUtils.isTrue(subscriber.getCanCreateChild())) {
			Subscriber s = selectSubscriber(subscriber.getId());
			if (s.getChildLdapOu() == null || s.getChildLdapOu().isEmpty()) {
				subscriber.setChildLdapOu(buildCabinetsOuName(subscriber.getId()));
			} else {
				subscriber.setChildLdapOu(s.getChildLdapOu());
			}
		}
		// End of can Create Child LDAP

		Subscriber resultSubscriber = subscriberRepository.save(subscriber);

		// Can Create Child LDAP action
		if (BooleanUtils.isTrue(subscriber.getCanCreateChild())) {
			String[] ldapOu = buildSubscriberLdapOu(subscriber);
			String childDescription = buildChildDescription(subscriber);
			ldapService.createOuIfNotExists(ldapOu, subscriber.getChildLdapOu(), childDescription);
		}
		// End of can Create Child LDAP action

		// Make default Report Paramset
		reportParamsetService.createDefaultReportParamsets(resultSubscriber);

		setupSubscriberAdminUserRoles(resultSubscriber);

		return resultSubscriber;
	}

    /**
     *
     * @param subscriberId
     * @param rmaSubscriberId
     */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	public void deleteRmaSubscriber(Long subscriberId, Long rmaSubscriberId) {
		checkNotNull(subscriberId);
		checkNotNull(rmaSubscriberId);

		Subscriber subscriber = selectSubscriber(subscriberId);
		if (!rmaSubscriberId.equals(subscriber.getRmaSubscriberId())) {
			throw new PersistenceException(String.format("Can't delete Subscriber (id=%d). Invalid RMA", subscriberId));
		}
		subscriberRepository.save(EntityActions.softDelete(subscriber));
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

		Subscriber subscriber = selectSubscriber(subscriberId);
		if (!rmaSubscriberId.equals(subscriber.getRmaSubscriberId())) {
			throw new PersistenceException(String.format("Can't delete Subscriber (id=%d). Invalid RMA", subscriberId));
		}
		subscrServiceAccessService.deleteSubscriberAccess(subscriberId);
		subscriberRepository.delete(subscriber);
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Long> selectRmaSubscriberIds(Long subscriberId) {
		return subscriberRepository.selectByRmaSubscriberIds(subscriberId);
	}

	/**
	 *
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<Subscriber> selectRmaList() {
		return subscriberRepository.selectRmaList();
	}

	/**
	 *
	 * @param subscriber
	 */
	@Secured({ ROLE_RMA_SUBSCRIBER_ADMIN, ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public void setupSubscriberAdminUserRoles(Subscriber subscriber) {

		checkNotNull(subscriber);
		checkArgument(!subscriber.isNew());

		List<SubscrUser> subscrUsers = subscrUserService.selectBySubscriberId(subscriber.getId());
		List<SubscrUser> adminUsers = ObjectFilters.filterToList(subscrUsers, i -> Boolean.TRUE.equals(i.getIsAdmin())
				&& !Boolean.TRUE.equals(i.getIsReadonly()) && !Boolean.TRUE.equals(i.getIsBlocked()));

		adminUsers.forEach(i -> {
			logger.debug("Update roles for {}. isAdmin: {}, isReadonly: {}", i.getUserName(), i.getIsAdmin(),
					i.getIsReadonly());

			logger.debug("Exisiting roles:");
			for (SubscrRole subscrRole : i.getSubscrRoles()) {
				logger.debug("Role: {}", subscrRole.getRoleName());
			}

			i.getSubscrRoles().clear();
			List<SubscrRole> subscrRoles = subscrUserService.processSubscrRoles(subscriber,
					Boolean.TRUE.equals(i.getIsAdmin()), Boolean.TRUE.equals(i.getIsReadonly()));
			i.getSubscrRoles().addAll(subscrRoles);

			logger.debug("New roles:");
			for (SubscrRole subscrRole : i.getSubscrRoles()) {
				logger.debug("Role: {}", subscrRole.getRoleName());
			}

		});

	}

}
