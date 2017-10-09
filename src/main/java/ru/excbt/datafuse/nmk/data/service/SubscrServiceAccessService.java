package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrServiceAccess;
import ru.excbt.datafuse.nmk.data.model.SubscrServicePack;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;
import ru.excbt.datafuse.nmk.data.repository.*;
import ru.excbt.datafuse.nmk.service.utils.DBExceptionUtil;
import ru.excbt.datafuse.nmk.data.model.ids.SubscriberParam;
import ru.excbt.datafuse.nmk.security.SecuredRoles;
import ru.excbt.datafuse.nmk.utils.LocalDateUtils;

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

	@Autowired
	private SubscrServiceAccessRepository subscrServiceAccessRepository;

	@Autowired
	private SubscrServiceItemRepository subscrServiceItemRepository;

	@Autowired
	private SubscrServicePackRepository subscrServicePackRepository;

	@Autowired
	private SubscrServicePermissionRepository subscrServicePermissionRepository;

	@Autowired
	private SubscriberRepository subscriberRepository;

	/**
	 *
	 * @param subscriberId
	 * @param accessDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServiceAccess> selectSubscriberServiceAccessFull(long subscriberId, LocalDate accessDate) {
		checkNotNull(accessDate);
		return subscrServiceAccessRepository.selectBySubscriberId(subscriberId, accessDate.toDate());
	}

	/**
	 *
	 * @param subscriberId
	 * @param accessDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServiceAccess> selectSubscriberServiceAccess(long subscriberId, LocalDate accessDate) {
		checkNotNull(accessDate);

		List<SubscrServiceAccess> accessList = subscrServiceAccessRepository.selectBySubscriberId(subscriberId,
				accessDate.toDate());

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
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_ADMIN })
	public boolean processAccess(Long subscriberId, LocalDate accessDate, SubscrServiceAccess entity) {
		checkNotNull(entity);
		checkArgument(entity.isNew());
		checkArgument(entity.getPackId() != null, "packId is not set");
		checkNotNull(accessDate, "accessDate is not set");

		Subscriber subscriber = subscriberRepository.findOne(subscriberId);
		if (subscriber == null) {
            DBExceptionUtil.entityNotFoundException(Subscriber.class, subscriberId);
        }
		entity.setSubscriber(subscriber);

		List<SubscrServiceAccess> currentAccessList = subscrServiceAccessRepository
				.selectBySubscriberIdAndPackId(subscriberId, entity.getPackId(), accessDate.toDate());

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
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_SUBSCR_ADMIN })
	public List<SubscrServiceAccess> processAccessList(Long subscriberId, LocalDate accessDate,
			final List<SubscrServiceAccess> accessList) {
		checkNotNull(accessDate, "accessDate is not set");
		List<SubscrServiceAccess> currentAccessList = subscrServiceAccessRepository.selectBySubscriberId(subscriberId,
				accessDate.toDate());

		final List<SubscrServiceAccess> extraAccessList = new ArrayList<>(accessList);

		List<SubscrServicePack> persistentServicePack = subscrServicePackRepository.findByIsPersistentService(true);

		extraAccessList.addAll(newSubscrServicePackAccessList(persistentServicePack));

		List<SubscrServiceAccess> removeGrants = new ArrayList<>();
		List<SubscrServiceAccess> addGrants = new ArrayList<>();

        Subscriber subscriber = subscriberRepository.findOne(subscriberId);
        if (subscriber == null) {
            DBExceptionUtil.entityNotFoundException(Subscriber.class, subscriberId);
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
			i.setAccessStartDate(accessDate.toDate());
		});

		removeGrants.forEach((c) -> {
			checkState(!c.isNew());
			c.setAccessEndDate(accessDate.toDate());
		});

		subscrServiceAccessRepository.save(addGrants);
		subscrServiceAccessRepository.save(removeGrants);

		List<SubscrServiceAccess> newAccessList = subscrServiceAccessRepository.selectBySubscriberId(subscriberId,
				accessDate.toDate());

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
			newEntity.setAccessStartDate(accessStartDate.toDate());
			return subscrServiceAccessRepository.save(newEntity);
		}

		if (workAccessList.size() > 1) {
			throw new IllegalStateException("More than 1 granted access");
		}

		SubscrServiceAccess currentAccess = workAccessList.get(0);
		if (currentAccess.getAccessStartDate().compareTo(accessStartDate.toDate()) <= 0) {
			return currentAccess;
		} else {
			throw new IllegalArgumentException("accessStartDate is before actual accessStartDate");
		}

	}

	/**
	 *
	 * @param entityId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public void deleteOne(long entityId) {
		subscrServiceAccessRepository.delete(entityId);
	}

	/**
	 *
	 * @param subscriberId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
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
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServicePermission> selectSubscriberPermissions(Long subscriberId, LocalDate accessDate) {
		return selectSubscriberPermissions(subscriberId, accessDate.toDate());
	}

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServicePermission> selectSubscriberPermissions(Long subscriberId, java.time.LocalDate accessDate) {
		return selectSubscriberPermissions(subscriberId, LocalDateUtils.asDate(accessDate));
	}

	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
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
     * @param subscriberParam
     * @param accessDate
     * @param <T>
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public <T> List<T> filterObjectAccess(List<T> objectList, SubscriberParam subscriberParam, LocalDate accessDate) {
		List<SubscrServicePermission> permissions = selectSubscriberPermissions(subscriberParam.getSubscriberId(),
				accessDate);
		SubscrServicePermissionFilter filter = new SubscrServicePermissionFilter(permissions, subscriberParam);
		return filter.filterObjects(objectList);
	}

    /**
     *
     * @param objectList
     * @param subscriberParam
     * @param accessDate
     * @param <T>
     * @return
     */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public <T> List<T> filterObjectAccess(List<T> objectList, SubscriberParam subscriberParam, java.time.LocalDate accessDate) {
		List<SubscrServicePermission> permissions = selectSubscriberPermissions(subscriberParam.getSubscriberId(),
				accessDate);
		SubscrServicePermissionFilter filter = new SubscrServicePermissionFilter(permissions, subscriberParam);
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
	@Transactional(value = TxConst.TX_DEFAULT)
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

}
