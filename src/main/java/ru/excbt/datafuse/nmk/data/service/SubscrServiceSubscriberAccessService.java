package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
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
import ru.excbt.datafuse.nmk.data.model.SubscrServiceSubscriberAccess;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.repository.SubscrServiceItemRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrServicePackRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrServiceSubscriberAccessRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrServiceSubscriberAccessService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrServiceSubscriberAccessService.class);

	@Autowired
	private SubscrServiceSubscriberAccessRepository subscrServiceSubscriberAccessRepository;

	@Autowired
	private SubscrServiceItemRepository subscrServiceItemRepository;

	@Autowired
	private SubscrServicePackRepository subscrServicePackRepository;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @param subscriberId
	 * @param accessDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServiceSubscriberAccess> selectSubscriberServiceAccessFull(long subscriberId,
			LocalDate accessDate) {
		checkNotNull(accessDate);
		return subscrServiceSubscriberAccessRepository.selectBySubscriberId(subscriberId, accessDate.toDate());
	}

	/**
	 * 
	 * @param subscriberId
	 * @param accessDate
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrServiceSubscriberAccess> selectSubscriberServiceAccess(long subscriberId, LocalDate accessDate) {
		checkNotNull(accessDate);

		List<SubscrServiceSubscriberAccess> accessList = subscrServiceSubscriberAccessRepository
				.selectBySubscriberId(subscriberId, accessDate.toDate());

		List<SubscrServiceSubscriberAccess> result = accessList.stream().filter((i) -> i.getAccessEndDate() == null)
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
	public boolean processAccess(long subscriberId, LocalDate accessDate, SubscrServiceSubscriberAccess entity) {
		checkNotNull(entity);
		checkArgument(entity.isNew());
		checkArgument(entity.getPackId() != null, "packId is not set");
		checkNotNull(accessDate, "accessDate is not set");

		Subscriber subscriber = subscriberService.findOne(subscriberId);
		entity.setSubscriber(subscriber);

		List<SubscrServiceSubscriberAccess> currentAccessList = subscrServiceSubscriberAccessRepository
				.selectBySubscriberIdAndPackId(subscriberId, entity.getPackId(), accessDate.toDate());

		SubscrServiceSubscriberAccess grantedAccess = grantPackItemAccess(subscriber, currentAccessList,
				entity.getPackId(), entity.getItemId(), accessDate);

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
	public List<SubscrServiceSubscriberAccess> processAccessList(long subscriberId, LocalDate accessDate,
			final List<SubscrServiceSubscriberAccess> accessList) {
		checkNotNull(accessDate, "accessDate is not set");
		List<SubscrServiceSubscriberAccess> currentAccessList = subscrServiceSubscriberAccessRepository
				.selectBySubscriberId(subscriberId, accessDate.toDate());

		List<SubscrServiceSubscriberAccess> removeGrants = new ArrayList<>();
		List<SubscrServiceSubscriberAccess> addGrants = new ArrayList<>();

		Subscriber subscriber = subscriberService.findOne(subscriberId);
		if (subscriber == null) {
			throw new PersistenceException(String.format("subscriber (id=%d) is not found", subscriberId));
		}

		currentAccessList.stream().filter((i) -> i.getAccessEndDate() == null).forEach((c) -> {
			Optional<SubscrServiceSubscriberAccess> check1 = accessList.stream().filter((i) -> i.equalsPackItem(c))
					.findAny();
			if (!check1.isPresent()) {
				removeGrants.add(c);
			}
		});

		accessList.forEach((n) -> {
			Optional<SubscrServiceSubscriberAccess> check2 = currentAccessList.stream()
					.filter((i) -> i.getAccessEndDate() == null).filter((i) -> i.equalsPackItem(n)).findAny();
			logger.info("check2:{}", check2.isPresent());
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

		subscrServiceSubscriberAccessRepository.save(addGrants);
		subscrServiceSubscriberAccessRepository.save(removeGrants);

		List<SubscrServiceSubscriberAccess> newAccessList = subscrServiceSubscriberAccessRepository
				.selectBySubscriberId(subscriberId, accessDate.toDate());

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
	private SubscrServiceSubscriberAccess grantPackItemAccess(Subscriber subscriber,
			List<SubscrServiceSubscriberAccess> currentAccessList, Long packId, Long itemId,
			LocalDate accessStartDate) {

		checkNotNull(subscriber);
		checkArgument(!subscriber.isNew());
		checkNotNull(accessStartDate);
		checkNotNull(packId);
		checkNotNull(currentAccessList);

		List<SubscrServiceSubscriberAccess> workAccessList = currentAccessList.stream()
				.filter((i) -> i.getItemId() == itemId).collect(Collectors.toList());

		if (workAccessList.size() == 0) {
			SubscrServiceSubscriberAccess newEntity = new SubscrServiceSubscriberAccess();
			newEntity.setSubscriber(subscriber);
			newEntity.setPackId(packId);
			newEntity.setItemId(itemId);
			newEntity.setAccessStartDate(accessStartDate.toDate());
			return subscrServiceSubscriberAccessRepository.save(newEntity);
		}

		if (workAccessList.size() > 1) {
			throw new IllegalStateException("More than 1 granted access");
		}

		SubscrServiceSubscriberAccess currentAccess = workAccessList.get(0);
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
		subscrServiceSubscriberAccessRepository.delete(entityId);
	}

}
