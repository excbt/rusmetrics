package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.repository.SubscrPriceListRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrPriceListService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrPriceListService.class);

	private final static String PRICE_LIST_PREFIX = "Черновик ";

	private final static int PRICE_LEVEL_NMC = 0;
	private final static int PRICE_LEVEL_RMA = 1;
	private final static int PRICE_LEVEL_SUBSCRIBER = 2;

	public static final Predicate<? super SubscrPriceList> PRICE_DRAFT_PREDICATE = (i) -> Boolean.TRUE
			.equals(i.getIsDraft());

	@Autowired
	private SubscrPriceListRepository subscrPriceListRepository;

	@Autowired
	private RmaSubscriberService rmaSubscriberService;

	/**
	 * 
	 * @param level
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPriceList> findRootPriceLists() {
		return subscrPriceListRepository.findByLevel(0);
	}

	/**
	 * 
	 * @param priceListOption
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrPriceList findRootPriceLists(String priceListOption) {
		List<SubscrPriceList> list = subscrPriceListRepository.findByLevel(0);

		list.forEach(i -> {
			logger.info("Price List Name :{}. Active: {}", i.getPriceListName(), i.getIsActive());
		});

		Optional<SubscrPriceList> preResult = list.stream().filter(
				i -> priceListOption.equalsIgnoreCase(i.getPriceListOption()) && Boolean.TRUE.equals(i.getIsActive()))
				.findFirst();

		return preResult.isPresent() ? preResult.get() : null;
	}

	/**
	 * 
	 * @param srcServicePriceListId
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public SubscrPriceList makeRmaPriceList(Long srcServicePriceListId, boolean isActive, Long... subscriberIds) {
		checkNotNull(srcServicePriceListId);
		checkNotNull(subscriberIds);
		checkArgument(subscriberIds.length > 0 && subscriberIds.length <= 3);

		archiveRmaActivePriceList(subscriberIds);

		Long rmaSubscriberId = subscriberIds[0];
		Subscriber rmaSubscriber = rmaSubscriberService.findOne(rmaSubscriberId);
		if (rmaSubscriber == null || !Boolean.TRUE.equals(rmaSubscriber.getIsRma())) {
			throw new PersistenceException(String.format("Invalid Rma Subscriber Id (%d)", rmaSubscriberId));
		}

		SubscrPriceList srcPriceList = subscrPriceListRepository.findOne(srcServicePriceListId);
		if (srcPriceList == null) {
			throw new PersistenceException(
					String.format("ServicePriceList (id=%d) is not found", srcServicePriceListId));
		}

		if (srcPriceList.getPriceListLevel() != PRICE_LEVEL_NMC) {
			throw new UnsupportedOperationException();
		}

		SubscrPriceList newPriceList = copyPriceList_L1(srcPriceList);
		newPriceList.setSrcPriceListId(srcServicePriceListId);
		newPriceList.setPriceListLevel(PRICE_LEVEL_RMA);
		newPriceList.setSubscriber1(rmaSubscriber);

		if (subscriberIds.length > 1) {
			newPriceList.setSubscriber2(rmaSubscriberService.findOne(subscriberIds[1]));
		}
		if (subscriberIds.length > 2) {
			newPriceList.setSubscriber3(rmaSubscriberService.findOne(subscriberIds[2]));
		}
		newPriceList.setPriceListType(subscriberIds.length);
		newPriceList.setIsMaster(true);
		newPriceList.setIsDraft(false);
		newPriceList.setIsActive(isActive);

		if (isActive) {
			Date rmaCurrentDate = rmaSubscriberService.getSubscriberCurrentTime(rmaSubscriberId);
			/**
			 * TODO check setting factBeginDate
			 */
			newPriceList.setFactBeginDate(rmaCurrentDate);
		}

		subscrPriceListRepository.save(newPriceList);

		return newPriceList;
	}

	/**
	 * 
	 * @param srcServicePriceList
	 * @return
	 */

	private SubscrPriceList copyPriceList_L1(SubscrPriceList srcServicePriceList) {
		checkNotNull(srcServicePriceList);
		checkArgument(!srcServicePriceList.isNew());
		SubscrPriceList newPriceList = new SubscrPriceList();

		newPriceList.setPriceListName(srcServicePriceList.getPriceListName());
		newPriceList.setPriceListOption(srcServicePriceList.getPriceListOption());
		newPriceList.setPlanBeginDate(srcServicePriceList.getPlanBeginDate());
		newPriceList.setPlanEndDate(srcServicePriceList.getPlanEndDate());
		newPriceList.setIsActive(false);
		newPriceList.setSubscriber1(srcServicePriceList.getSubscriber1());
		newPriceList.setSubscriber2(srcServicePriceList.getSubscriber2());
		newPriceList.setSubscriber3(srcServicePriceList.getSubscriber3());
		newPriceList.setMasterPriceListId(srcServicePriceList.getMasterPriceListId());

		return newPriceList;
	}

	/**
	 * 
	 * @param subscrPriceList
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public void deleteSubscrPriceList(SubscrPriceList subscrPriceList) {
		checkNotNull(subscrPriceList);
		checkArgument(!subscrPriceList.isNew());
		checkArgument(subscrPriceList.getPriceListLevel() != PRICE_LEVEL_NMC);
		subscrPriceListRepository.delete(subscrPriceList);
	}

	// /**
	// *
	// * @param subscriberId
	// * @return
	// */
	// @Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	// public List<SubscrPriceList> findRmaPriceLists(Long subscriberId) {
	// List<SubscrPriceList> preResult =
	// subscrPriceListRepository.findByRma(subscriberId);
	//
	// List<SubscrPriceList> result = ObjectFilters.deletedFilter(preResult);
	// result.forEach(i -> {
	// if (i.getSubscriber2() != null) {
	// i.getSubscriber2().getId();
	// }
	// });
	//
	// return result;
	// }

	/**
	 * 
	 * @param subscriberIds
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public boolean archiveRmaActivePriceList(Long... subscriberIds) {
		SubscrPriceList activePriceList = findActiveRmaPriceList(subscriberIds);
		if (activePriceList == null) {
			return false;
		}
		activePriceList.setFactEndDate(rmaSubscriberService.getSubscriberCurrentTime(subscriberIds[0]));
		activePriceList.setIsActive(false);
		activePriceList.setIsArchive(true);
		subscrPriceListRepository.save(activePriceList);
		return true;
	}

	/**
	 * 
	 * @param subscriberIds
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public void deleteActivePriceList(Long... subscriberIds) {
		SubscrPriceList activePriceList = findActiveRmaPriceList(subscriberIds);
		if (activePriceList != null) {
			deleteSubscrPriceList(activePriceList);
		}
	}

	private void checkSubscriberIds(Long[] subscriberIds) {
		checkNotNull(subscriberIds);
		checkArgument(subscriberIds.length > 0 && subscriberIds.length <= 3);

		for (Long l : subscriberIds) {
			checkNotNull(l, "subscriberIds elements is null");
		}
	}

	/**
	 * 
	 * @param subscriberIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrPriceList findActiveRmaPriceList(Long... subscriberIds) {
		checkSubscriberIds(subscriberIds);

		List<SubscrPriceList> activePriceLists = findRmaPriceLists(subscriberIds).stream()
				.filter(ObjectFilters.ACTIVE_OBJECT_PREDICATE).collect(Collectors.toList());

		if (activePriceLists.size() == 0) {
			return null;
		}

		checkState(activePriceLists.size() <= 1);
		SubscrPriceList result = activePriceLists.get(0);
		return result;
	}

	/**
	 * 
	 * @param subscriberIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPriceList> findDraftRmaPriceLists(Long... subscriberIds) {
		checkSubscriberIds(subscriberIds);

		List<SubscrPriceList> rmaPriceLists = findRmaPriceLists(subscriberIds);
		List<SubscrPriceList> resultPriceLists = rmaPriceLists.stream().filter(PRICE_DRAFT_PREDICATE)
				.collect(Collectors.toList());

		return resultPriceLists;
	}

	/**
	 * 
	 * @param subscriberIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPriceList> findRmaPriceLists(Long... subscriberIds) {
		checkSubscriberIds(subscriberIds);

		Long rmaSubscriberId = subscriberIds[0];

		List<SubscrPriceList> rmaPriceLists = subscrPriceListRepository.findByRma(rmaSubscriberId);

		final int priceListType = subscriberIds.length;

		List<SubscrPriceList> resultPriceLists = rmaPriceLists.stream()
				.filter(i -> i.getPriceListLevel() == PRICE_LEVEL_RMA).filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.filter(i -> i.getPriceListType() != null && i.getPriceListType().equals(priceListType))
				.filter(i -> (priceListType == 1 && i.getSubscriberId2() == null)
						|| (i.getSubscriberId2() != null && i.getSubscriberId2().equals(subscriberIds[1])))
				.collect(Collectors.toList());

		return resultPriceLists;
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPriceList> findSubscriberPriceLists(Long rmaSubscriberId, Long subscriberId) {
		checkNotNull(rmaSubscriberId);
		checkNotNull(subscriberId);

		List<SubscrPriceList> rmaPriceLists = subscrPriceListRepository.findByRma(rmaSubscriberId);
		List<SubscrPriceList> resultPriceLists = rmaPriceLists.stream()
				.filter(i -> i.getPriceListLevel() == PRICE_LEVEL_SUBSCRIBER)
				.filter(i -> i.getSubscriberId2() != null && i.getSubscriberId2().equals(subscriberId))
				.collect(Collectors.toList());

		return resultPriceLists;
	}

	/**
	 * 
	 * @param srcPriceListId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public SubscrPriceList makeDraftRmaPriceList(Long srcPriceListId) {
		checkNotNull(srcPriceListId);
		SubscrPriceList srcPriceList = subscrPriceListRepository.findOne(srcPriceListId);
		if (srcPriceList == null) {
			throw new PersistenceException(String.format("SubscrPriceList (id=%d) is not found", srcPriceListId));
		}
		SubscrPriceList newPriceList = copyPriceList_L1(srcPriceList);
		newPriceList.setPriceListLevel(PRICE_LEVEL_RMA);
		newPriceList.setIsActive(false);
		newPriceList.setIsArchive(false);
		newPriceList.setIsDraft(true);
		newPriceList.setPriceListType(srcPriceList.getPriceListType());
		newPriceList.setSrcPriceListId(srcPriceListId);
		newPriceList.setPriceListName(PRICE_LIST_PREFIX + srcPriceList.getPriceListName());

		if (Boolean.TRUE.equals(srcPriceList.getIsMaster())) {
			newPriceList.setMasterPriceListId(srcPriceList.getId());
		}

		subscrPriceListRepository.save(newPriceList);
		return newPriceList;
	}

	/**
	 * 
	 * @param srcPriceListId
	 * @param subscriberId
	 * @param isActive
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public SubscrPriceList makeSubscrPriceList(Long srcPriceListId, Long subscriberId, boolean isActive) {
		checkNotNull(srcPriceListId);
		checkNotNull(subscriberId);
		SubscrPriceList srcPriceList = subscrPriceListRepository.findOne(srcPriceListId);
		if (srcPriceList == null) {
			throw new PersistenceException(String.format("SubscrPriceList (id=%d) is not found", srcPriceListId));
		}

		Long rmaSubscriberId = srcPriceList.getSubscriberId1();

		List<Long> rmaSubscribersIds = rmaSubscriberService.selectRmaSubscriberIds(srcPriceList.getSubscriberId1());
		if (!rmaSubscribersIds.contains(subscriberId)) {
			throw new PersistenceException(
					String.format("Subscriber (id=%d) is not set to rma (id=%d)", subscriberId, rmaSubscribersIds));
		}

		if (srcPriceList.getSubscriberId2() != null && !srcPriceList.getSubscriberId2().equals(subscriberId)) {
			throw new PersistenceException(
					String.format("SubscrPriceList is not compatabile with Subscriber (id=%d)", subscriberId));
		}

		if (isActive) {
			setInctiveSubscrPriceList(rmaSubscriberId, subscriberId);
		}

		SubscrPriceList newPriceList = copyPriceList_L1(srcPriceList);
		newPriceList.setSubscriber2(initSubscriber(subscriberId));
		newPriceList.setPriceListLevel(PRICE_LEVEL_SUBSCRIBER);
		if (isActive) {
			Date rmaCurrentDate = rmaSubscriberService.getSubscriberCurrentTime(rmaSubscriberId);
			newPriceList.setFactBeginDate(rmaCurrentDate);
			newPriceList.setIsActive(isActive);
		}
		newPriceList.setIsArchive(false);
		newPriceList.setIsDraft(!isActive);
		newPriceList.setPriceListType(2);
		newPriceList.setSrcPriceListId(srcPriceListId);
		newPriceList.setPriceListName(PRICE_LIST_PREFIX + srcPriceList.getPriceListName());

		if (Boolean.TRUE.equals(srcPriceList.getIsMaster())) {
			newPriceList.setMasterPriceListId(srcPriceList.getId());
		}

		subscrPriceListRepository.save(newPriceList);
		return newPriceList;
	}

	/**
	 * 
	 * @param srcPriceListId
	 * @param subscriberIds
	 * @param activeIds
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public void makeSubscrPriceLists(Long srcPriceListId, List<Long> subscriberIds, List<Long> activeIds) {
		checkNotNull(srcPriceListId);
		checkNotNull(subscriberIds);

		List<Long> activeIdList = activeIds != null ? new ArrayList<>(activeIds) : new ArrayList<>();
		for (Long id : subscriberIds) {
			boolean isActive = activeIdList.contains(id);
			makeSubscrPriceList(srcPriceListId, id, isActive);
		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	private Subscriber initSubscriber(Long id) {
		return id == null ? null : rmaSubscriberService.findOne(id);
	}

	/**
	 * 
	 * @param subscrPriceList
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public SubscrPriceList updateOne(SubscrPriceList subscrPriceList) {
		checkNotNull(subscrPriceList);
		checkArgument(!subscrPriceList.isNew());
		checkArgument(subscrPriceList.getPriceListLevel() != PRICE_LEVEL_NMC);
		checkArgument(Boolean.FALSE.equals(subscrPriceList.getIsActive()));
		checkArgument(Boolean.FALSE.equals(subscrPriceList.getIsArchive()));
		checkArgument(Boolean.TRUE.equals(subscrPriceList.getIsDraft()));

		Subscriber s1 = initSubscriber(subscrPriceList.getSubscriberId1());
		Subscriber s2 = initSubscriber(subscrPriceList.getSubscriberId2());
		Subscriber s3 = initSubscriber(subscrPriceList.getSubscriberId3());
		subscrPriceList.setSubscriber1(s1);
		subscrPriceList.setSubscriber2(s2);
		subscrPriceList.setSubscriber3(s3);

		return subscrPriceListRepository.save(subscrPriceList);
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param subscriberId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public int setInctiveSubscrPriceList(Long rmaSubscriberId, Long subscriberId) {
		List<SubscrPriceList> subscrPriceLists = findSubscriberPriceLists(rmaSubscriberId, subscriberId);
		List<SubscrPriceList> activePriceLists = subscrPriceLists.stream().filter(ObjectFilters.ACTIVE_OBJECT_PREDICATE)
				.filter(i -> i.getPriceListLevel() != null && i.getPriceListLevel() == PRICE_LEVEL_SUBSCRIBER)
				.collect(Collectors.toList());

		Date rmaCurrentDate = rmaSubscriberService.getSubscriberCurrentTime(rmaSubscriberId);

		activePriceLists.forEach(i -> {
			i.setFactEndDate(rmaCurrentDate);
			i.setIsActive(false);
			i.setIsArchive(true);
		});

		if (!activePriceLists.isEmpty()) {
			subscrPriceListRepository.save(activePriceLists);
		}

		return activePriceLists.size();
	}

	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public SubscrPriceList setActiveSubscrPriceList(Long rmaSubscriberId, Long subscrPriceListId) {
		checkNotNull(rmaSubscriberId);
		checkNotNull(subscrPriceListId);

		SubscrPriceList subscrPriceList = subscrPriceListRepository.findOne(subscrPriceListId);
		if (subscrPriceList == null) {
			throw new PersistenceException(String.format("SubscrPriceList (id=%d) is not found", subscrPriceListId));
		}

		if (!rmaSubscriberId.equals(subscrPriceList.getSubscriberId1())) {
			throw new PersistenceException(
					String.format("SubscrPriceList (id=%d) is not belongs to RMA", subscrPriceListId));
		}

		if (subscrPriceList.getPriceListLevel() == null
				|| subscrPriceList.getPriceListLevel() != PRICE_LEVEL_SUBSCRIBER) {
			throw new PersistenceException(
					String.format("SubscrPriceList (id=%d) is wrong price level", subscrPriceListId));
		}

		if (Boolean.TRUE.equals(subscrPriceList.getIsActive()) || Boolean.TRUE.equals(subscrPriceList.getIsArchive())) {
			throw new PersistenceException(
					String.format("SubscrPriceList (id=%d) is in wrong state", subscrPriceListId));
		}

		setInctiveSubscrPriceList(subscrPriceList.getSubscriberId1(), subscrPriceList.getSubscriberId2());

		Date rmaCurrentDate = rmaSubscriberService.getSubscriberCurrentTime(rmaSubscriberId);
		subscrPriceList.setFactBeginDate(rmaCurrentDate);
		subscrPriceList.setIsActive(true);
		subscrPriceList.setIsDraft(false);

		return subscrPriceListRepository.save(subscrPriceList);

	}

}
