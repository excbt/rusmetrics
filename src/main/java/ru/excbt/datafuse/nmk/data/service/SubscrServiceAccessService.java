package ru.excbt.datafuse.nmk.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.data.model.SubscrServiceAccess;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.ids.PortalUserIds;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;
import ru.excbt.datafuse.nmk.data.repository.*;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.service.SubscriberTimeService;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;

/**
 * Сервис для работы с доступом к услугам абонента
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.09.2015
 *
 */
@Service
public class SubscrServiceAccessService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrServiceAccessService.class);


	private final SubscrServiceAccessRepository subscrServiceAccessRepository;

	private final SubscrServiceItemRepository subscrServiceItemRepository;

	private final SubscrServicePackRepository subscrServicePackRepository;

	private final SubscrServicePermissionRepository subscrServicePermissionRepository;

	private final SubscriberRepository subscriberRepository;

	private final SubscriberTimeService subscriberTimeService;

    public SubscrServiceAccessService(SubscrServiceAccessRepository subscrServiceAccessRepository, SubscrServiceItemRepository subscrServiceItemRepository, SubscrServicePackRepository subscrServicePackRepository, SubscrServicePermissionRepository subscrServicePermissionRepository, SubscriberRepository subscriberRepository, SubscriberTimeService subscriberTimeService) {
        this.subscrServiceAccessRepository = subscrServiceAccessRepository;
        this.subscrServiceItemRepository = subscrServiceItemRepository;
        this.subscrServicePackRepository = subscrServicePackRepository;
        this.subscrServicePermissionRepository = subscrServicePermissionRepository;
        this.subscriberRepository = subscriberRepository;
        this.subscriberTimeService = subscriberTimeService;
    }

    /**
	 *
	 * @param subscriberId
	 * @param accessDate
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<SubscrServiceAccess> selectSubscriberServiceAccessFull(long subscriberId, LocalDate accessDate) {
		checkNotNull(accessDate);
		return subscrServiceAccessRepository.selectBySubscriberId(subscriberId, LocalDateUtils.asDate(accessDate));
	}

	/**
	 *
	 * @param subscriberId
	 * @param accessDate
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<SubscrServiceAccess> selectSubscriberServiceAccess(long subscriberId, LocalDate accessDate) {
		checkNotNull(accessDate);

		List<SubscrServiceAccess> accessList = subscrServiceAccessRepository.selectBySubscriberId(subscriberId,
            LocalDateUtils.asDate(accessDate));

		List<SubscrServiceAccess> result = accessList.stream().filter((i) -> i.getAccessEndDate() == null)
				.collect(Collectors.toList());

		return result;
	}

	/**
	 *
	 * @param subscriberId
	 * @param entity
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_ADMIN })
	public boolean processAccess(Long subscriberId, LocalDate accessDate, SubscrServiceAccess entity) {
		checkNotNull(entity);
		checkArgument(entity.isNew());
		checkArgument(entity.getPackId() != null, "packId is not set");
		checkNotNull(accessDate, "accessDate is not set");

		Subscriber subscriber = subscriberRepository.findOne(subscriberId);
		if (subscriber == null) {
            throw DBExceptionUtil.newEntityNotFoundException(Subscriber.class, subscriberId);
        }
		entity.setSubscriber(subscriber);

		List<SubscrServiceAccess> currentAccessList = subscrServiceAccessRepository
				.selectBySubscriberIdAndPackId(subscriberId, entity.getPackId(), LocalDateUtils.asDate(accessDate));

		SubscrServiceAccess grantedAccess = grantPackItemAccess(subscriber, currentAccessList, entity.getPackId(),
				entity.getItemId(), accessDate);

		return grantedAccess != null;
	}

	/**
	 *
	 * @param subscriberId
	 * @param accessDate
	 * @param accessList
	 * @return
	 */
	@Transactional
	@Secured({ ROLE_SUBSCR_ADMIN })
	public List<SubscrServiceAccess> processAccessList(Long subscriberId, LocalDate accessDate,
			final List<SubscrServiceAccess> accessList) {
		checkNotNull(accessDate, "accessDate is not set");
		List<SubscrServiceAccess> currentAccessList = subscrServiceAccessRepository.selectBySubscriberId(subscriberId,
				LocalDateUtils.asDate(accessDate));

		final List<SubscrServiceAccess> extraAccessList = new ArrayList<>(accessList);

		List<SubscrServicePack> persistentServicePack = subscrServicePackRepository.findByIsPersistentService(true);

		extraAccessList.addAll(newSubscrServicePackAccessList(persistentServicePack));

		List<SubscrServiceAccess> removeGrants = new ArrayList<>();
		List<SubscrServiceAccess> addGrants = new ArrayList<>();

        Subscriber subscriber = subscriberRepository.findOne(subscriberId);
        if (subscriber == null) {
            throw DBExceptionUtil.newEntityNotFoundException(Subscriber.class, subscriberId);
        }

		currentAccessList.stream().filter((i) -> i.getAccessEndDate() == null).forEach((c) -> {
			Optional<SubscrServiceAccess> check1 = extraAccessList.stream().filter((i) -> i.equalsPackItem(c))
					.findAny();
			if (!check1.isPresent()) {
				removeGrants.add(c);
			}
		});

		extraAccessList.forEach((n) -> {
			Optional<SubscrServiceAccess> check2 = currentAccessList.stream()
					.filter((i) -> i.getAccessEndDate() == null).filter((i) -> i.equalsPackItem(n)).findAny();
			logger.debug("check2:{}", check2.isPresent());
			if (!check2.isPresent() && n.getPackId() != null) {
				addGrants.add(n);
			}
		});

		addGrants.forEach((i) -> {
			checkState(i.isNew());
			i.setSubscriber(subscriber);
			i.setAccessStartDate(LocalDateUtils.asDate(accessDate));
		});

		removeGrants.forEach((c) -> {
			checkState(!c.isNew());
			c.setAccessEndDate(LocalDateUtils.asDate(accessDate));
		});

		subscrServiceAccessRepository.save(addGrants);
		subscrServiceAccessRepository.save(removeGrants);

		List<SubscrServiceAccess> newAccessList = subscrServiceAccessRepository.selectBySubscriberId(subscriberId,
            LocalDateUtils.asDate(accessDate));

		return newAccessList;
	}

	/**
	 *
	 * @param subscriber
	 * @param packId
	 * @param itemId
	 * @param accessStartDate
	 * @return
	 */
	private SubscrServiceAccess grantPackItemAccess(Subscriber subscriber, List<SubscrServiceAccess> currentAccessList,
			Long packId, Long itemId, LocalDate accessStartDate) {

		checkNotNull(subscriber);
		checkArgument(!subscriber.isNew());
		checkNotNull(accessStartDate);
		checkNotNull(packId);
		checkNotNull(currentAccessList);

		List<SubscrServiceAccess> workAccessList = currentAccessList.stream().filter((i) -> i.getItemId() == itemId)
				.collect(Collectors.toList());

		if (workAccessList.size() == 0) {
			SubscrServiceAccess newEntity = new SubscrServiceAccess();
			newEntity.setSubscriber(subscriber);
			newEntity.setPackId(packId);
			newEntity.setItemId(itemId);
			newEntity.setAccessStartDate(LocalDateUtils.asDate(accessStartDate));
			return subscrServiceAccessRepository.save(newEntity);
		}

		if (workAccessList.size() > 1) {
			throw new IllegalStateException("More than 1 granted access");
		}

		SubscrServiceAccess currentAccess = workAccessList.get(0);
		if (currentAccess.getAccessStartDate().compareTo(LocalDateUtils.asDate(accessStartDate)) <= 0) {
			return currentAccess;
		} else {
			throw new IllegalArgumentException("accessStartDate is before actual accessStartDate");
		}

	}

	/**
	 *
	 * @param entityId
	 */
	@Transactional
	public void deleteOne(long entityId) {
		subscrServiceAccessRepository.delete(entityId);
	}

	/**
	 *
	 * @param subscriberId
	 */
	@Transactional
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public void deleteSubscriberAccess(Long subscriberId) {
		List<SubscrServiceAccess> accessList = subscrServiceAccessRepository.selectBySubscriberId(subscriberId);
		subscrServiceAccessRepository.delete(accessList);
	}

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Transactional( readOnly = true)
	public List<SubscrServicePermission> selectSubscriberPermissions(Long subscriberId, LocalDate accessDate) {
		return selectSubscriberPermissions(subscriberId, LocalDateUtils.asDate(accessDate));
	}


	@Transactional( readOnly = true)
	public List<SubscrServicePermission> selectSubscriberPermissions(Long subscriberId, Date accessDate) {
		checkNotNull(accessDate);
		List<SubscrServicePermission> result = subscrServicePermissionRepository.selectCommonPermissions();
		List<SubscrServicePermission> subscriberPermissions = subscrServiceAccessRepository
				.selectSubscriberPermissions(subscriberId, accessDate);
		result.addAll(subscriberPermissions);
		return result;
	}

    /**
     *
     * @param objectList
     * @param portalUserIds
     * @param accessDate
     * @param <T>
     * @return
     */
	@Transactional( readOnly = true)
	public <T> List<T> filterObjectAccess(List<T> objectList, PortalUserIds portalUserIds, LocalDate accessDate) {
		List<SubscrServicePermission> permissions = selectSubscriberPermissions(portalUserIds.getSubscriberId(),
				accessDate);
		SubscrServicePermissionFilter filter = new SubscrServicePermissionFilter(permissions, portalUserIds);
		return filter.filterObjects(objectList);
	}


	/**
	 *
	 * @param subscrServicePackList
	 * @return
	 */
	private List<SubscrServiceAccess> newSubscrServicePackAccessList(List<SubscrServicePack> subscrServicePackList) {
		final List<SubscrServiceAccess> persistentAccessList = new ArrayList<>();

		subscrServicePackList.forEach((p) -> {

			{
				SubscrServiceAccess packAccess = new SubscrServiceAccess();
				packAccess.setPackId(p.getId());
				persistentAccessList.add(packAccess);
			}
			p.getServiceItems().forEach((i) -> {
				SubscrServiceAccess itemAccess = new SubscrServiceAccess();
				itemAccess.setPackId(p.getId());
				itemAccess.setItemId(i.getId());
				persistentAccessList.add(itemAccess);

			});

		});
		return persistentAccessList;
	}

	/**
	 *
	 * @param packId
	 * @return
	 */
	@Transactional
	public List<SubscrServiceAccess> getPackSubscrServiceAccess(Long packId) {
		SubscrServicePack subscrServicePack = subscrServicePackRepository.findOne(packId);
		if (subscrServicePack == null) {
			throw new PersistenceException(String.format("Service Pack (id=%d) is not found", packId));
		}

		List<SubscrServicePack> subscrServicePackList = new ArrayList<>();
		subscrServicePackList.add(subscrServicePack);
		List<SubscrServiceAccess> serviceAccess = newSubscrServicePackAccessList(subscrServicePackList);
		return serviceAccess;
	}

	@Transactional
    public  <T> List<T> filterObjectAccess(List<T> objectList, PortalUserIds portalUserIds) {

        List<T> resultObjects = filterObjectAccess(objectList, portalUserIds,
            LocalDateUtils.asLocalDate(subscriberTimeService.getSubscriberCurrentTime(portalUserIds.getSubscriberId())));

        return resultObjects;
    }



}
