package ru.excbt.datafuse.nmk.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.QOrganization;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.service.dto.OrganizationDTO;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.support.EntityActions;
import ru.excbt.datafuse.nmk.data.repository.OrganizationRepository;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.mapper.OrganizationMapper;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.service.utils.WhereClauseBuilder;

/**
 * Сервис для работы с организациями
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 19.03.2015
 *
 */
@Service
public class OrganizationService implements SecuredRoles {

    private static final Logger log = LoggerFactory.getLogger(OrganizationService.class);

	private final OrganizationRepository organizationRepository;

	private final OrganizationMapper organizationMapper;

	private final SubscriberService subscriberService;

    private final static QOrganization qOrganization = QOrganization.organization;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository, OrganizationMapper organizationMapper, SubscriberService subscriberService) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
        this.subscriberService = subscriberService;
    }

//    /**
//	 *
//	 * @param id
//	 * @return
//	 */
//	@Transactional( readOnly = true)
//	public Organization selectOrganization(final long id) {
//		return organizationRepository.findOne(id);
//	}

	@Transactional( readOnly = true)
	public Optional<Organization> findOneOrganization(final long id) {
		return Optional.ofNullable(organizationRepository.findOne(id));
	}

//	/**
//	 *
//	 * @param id
//	 * @return
//	 */
//	@Transactional( readOnly = true)
//	public Organization findOrganization(final long id) {
//		return organizationRepository.findOne(id);
//	}

	/**
	 *
	 * @return
	 */
	@Transactional
	public List<OrganizationDTO> selectRsoOrganizations(PortalUserIds portalUserIds) {
	    Long rmaSubscriberId = subscriberService.getRmaSubscriberId(portalUserIds);
		List<Organization> organizations = organizationRepository
				.selectRsoOrganizations(rmaSubscriberId);
		List<OrganizationDTO> result = organizations.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.filter(ObjectFilters.NO_DEV_MODE_OBJECT_PREDICATE).map(organizationMapper::toDTO).collect(Collectors.toList());
		return result;
	}

	/**
	 *
	 * @return
	 */
	@Transactional
	public List<OrganizationDTO> selectCmOrganizations(PortalUserIds portalUserIds) {
		List<Organization> organizations = organizationRepository
				.selectCmOrganizations(portalUserIds.getRmaId());
		List<OrganizationDTO> result = organizations.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
            .filter(ObjectFilters.NO_DEV_MODE_OBJECT_PREDICATE)
            .map(organizationMapper::toDTO)
            .collect(Collectors.toList());
		return result;
	}

	/**
	 *
	 * @return
	 */
	@Transactional
	public List<OrganizationDTO> selectOrganizations(PortalUserIds portalUserIds) {
		List<Organization> organizations = organizationRepository
				.findOrganizationsOfRma(portalUserIds.getRmaId());
		List<OrganizationDTO> result = organizations.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.filter(ObjectFilters.NO_DEV_MODE_OBJECT_PREDICATE)
            .map(organizationMapper::toDTO)
            .collect(Collectors.toList());
		return result;
	}

	@Transactional
	public List<OrganizationDTO> findOrganizationsOfRma(PortalUserIds userids) {
	    Long searchSubscriberId = userids.isRma() ? userids.getSubscriberId() : userids.getRmaId();
		List<OrganizationDTO> organizations = organizationRepository.findOrganizationsOfRma(searchSubscriberId)
            .stream()
                .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
                .filter(ObjectFilters.NO_DEV_MODE_OBJECT_PREDICATE)
                .map(organizationMapper::toDTO).collect(Collectors.toList());
		return organizations;
	}

	@Transactional
	public List<OrganizationDTO> findOrganizationsOfRma(PortalUserIds userids, Sort sort) {
	    Long searchSubscriberId = userids.isRma() ? userids.getSubscriberId() : userids.getRmaId();
		List<OrganizationDTO> organizations = organizationRepository.findOrganizationsOfRma(searchSubscriberId, sort)
            .stream()
                .filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
                .filter(ObjectFilters.NO_DEV_MODE_OBJECT_PREDICATE)
                .map(organizationMapper::toDTO).collect(Collectors.toList());
		return organizations;
	}


    /**
     *
     * @param userids
     * @param searchStringOptional
     * @param pageable
     * @return
     */
	@Transactional
	public Page<OrganizationDTO> findOrganizationsOfRmaPaged(
                                                            PortalUserIds userids,
                                                            Optional<String> searchStringOptional,
                                                            Optional<Boolean> subscriberModeOpt,
                                                            Pageable pageable
                                                            ) {

	    Long searchSubscriberId = userids.isRma() ? userids.getSubscriberId() : userids.getRmaId();

        QOrganization qOrganization = QOrganization.organization;

        BooleanExpression subscriberFilter = subscriberModeOpt.map(val -> val ?
                qOrganization.subscriberId.eq(userids.getSubscriberId()) :
                qOrganization.rmaSubscriberId.eq(searchSubscriberId).or(qOrganization.isCommon.isTrue())
                )
            .orElse(
                qOrganization.rmaSubscriberId.eq(searchSubscriberId)
                    .or(qOrganization.isCommon.isTrue())
                    .or(qOrganization.subscriberId.eq(userids.getSubscriberId()))
            );

        WhereClauseBuilder where = new WhereClauseBuilder()
            .and(subscriberFilter)
            .and(baseConditions());

        searchStringOptional.ifPresent(s -> where.and(searchCondition(s)));

        Page<OrganizationDTO> page = organizationRepository.findAll(where, pageable).map(organizationMapper::toDTO);
		return page;
	}



    private static BooleanExpression baseConditions() {
        return qOrganization.deleted.eq(0)
            .and(qOrganization.isDevMode.isNull().or(qOrganization.isDevMode.isFalse()));
    }

    private static BooleanExpression searchCondition(String s) {
	    if (s.isEmpty()) {
	        return null;
        }
	    return qOrganization.inn.toUpperCase().like(QueryDSLUtil.upperCaseLikeStr.apply(s))
            .or(qOrganization.ogrn.toUpperCase().like(QueryDSLUtil.upperCaseLikeStr.apply(s)))
            .or(qOrganization.organizationName.toUpperCase().like(QueryDSLUtil.upperCaseLikeStr.apply(s)));
    }


	/**
	 *
	 * @param keyname
	 * @return
	 */
	@Transactional
	public Organization selectByKeyname(SubscriberParam subscriberParam, String keyname) {
		List<Organization> organizations = organizationRepository.selectByKeyname(subscriberParam.getRmaSubscriberId(),
				keyname);
		return organizations.size() > 0 ? organizations.get(0) : null;
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public Organization saveOrganization(Organization entity) {
		return organizationRepository.saveAndFlush(entity);
	}


	@Transactional
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public OrganizationDTO saveOrganization(OrganizationDTO dto, PortalUserIds currentIds) {
	    Organization organization = organizationMapper.toEntity(dto);
	    organization.setSubscriber(new Subscriber().id(currentIds.getSubscriberId()));

	    if (organization.getId() != null) {

        }
		return organizationMapper.toDTO(organizationRepository.saveAndFlush(organization));
	}

	/**
	 *
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public Organization deleteOrganization(Organization entity) {
		return organizationRepository.save(EntityActions.softDelete(entity));
	}

	/**
	 *
	 * @param organizations
	 * @param checkOrganizationId
	 */
	@Transactional( readOnly = true)
	public void checkAndEnhanceOrganizations(final List<OrganizationDTO> organizations,
			final Long checkOrganizationId) {

		if (organizations != null && checkOrganizationId != null) {
			boolean orgExists = organizations.stream().anyMatch(i -> checkOrganizationId.equals(i.getId()));
			if (!orgExists) {
                findOneOrganization(checkOrganizationId).ifPresent(o -> organizations.add(0, organizationMapper.toDTO(o)));
			}
		}
		//return organizations;
	}

	@Transactional
    @Secured({ ROLE_ADMIN, ROLE_RMA_CONT_OBJECT_ADMIN, ROLE_RMA_DEVICE_OBJECT_ADMIN })
	public void deleteOrganization(final Long organizationId, final PortalUserIds portalUserIds) {
	    Organization org = organizationRepository.findOne(organizationId);
       if (Boolean.TRUE.equals(org.getIsCommon())) {
            DBExceptionUtil.accessDeniedException(Organization.class, organizationId);
        }

        if (org.getSubscriber() != null && !portalUserIds.getSubscriberId().equals(org.getSubscriber().getId())) {
            DBExceptionUtil.accessDeniedException(Organization.class, organizationId);
        }

        org.setDeleted(1);
	    organizationRepository.save(org);
    }

}
