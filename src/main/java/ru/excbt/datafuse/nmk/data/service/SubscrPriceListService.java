package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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

	public final static int PRICE_LEVEL_NMC = 0;
	public final static int PRICE_LEVEL_RMA = 1;
	public final static int PRICE_LEVEL_SUBSCRIBER = 2;

	public static final Predicate<? super SubscrPriceList> PRICE_DRAFT_PREDICATE = (i) -> Boolean.TRUE
			.equals(i.getIsDraft());

	@Autowired
	private SubscrPriceListRepository subscrPriceListRepository;

	@Autowired
	private RmaSubscriberService rmaSubscriberService;

	@Autowired
	private SubscrPriceItemService subscrPriceItemService;

	@Autowired
	private SubscriberService subscriberService;

	/**
	 * 
	 * @param level
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPriceList> findRootPriceLists() {
		List<SubscrPriceList> preResult = subscrPriceListRepository.selectByLevel(0);
		List<SubscrPriceList> result = preResult.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * 
	 * @param priceListOption
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrPriceList findRootPriceLists(String priceListKeyname) {
		List<SubscrPriceList> list = subscrPriceListRepository.selectByLevel(0);

		list.forEach(i -> {
			logger.info("Price List Name :{}. Active: {}", i.getPriceListName(), i.getIsActive());
		});

		Optional<SubscrPriceList> preResult = list.stream()
				.filter(i -> priceListKeyname == null || priceListKeyname.equalsIgnoreCase(i.getPriceListKeyname()))
				.findFirst();

		return preResult.isPresent() ? preResult.get() : null;
	}

	/**
	 * 
	 * @param srcPriceListId
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public SubscrPriceList createRmaPriceList(Long srcPriceListId, Subscriber rmaSubscriber, Subscriber subscriber) {
		checkNotNull(srcPriceListId);
		checkNotNull(rmaSubscriber);

		if (rmaSubscriber == null || !Boolean.TRUE.equals(rmaSubscriber.getIsRma())) {
			throw new PersistenceException(String.format("Invalid Rma Subscriber Id (%d)", rmaSubscriber.getId()));
		}

		SubscrPriceList srcPriceList = subscrPriceListRepository.findOne(srcPriceListId);
		if (srcPriceList == null) {
			throw new PersistenceException(String.format("ServicePriceList (id=%d) is not found", srcPriceListId));
		}

		if (srcPriceList.getPriceListLevel() != PRICE_LEVEL_NMC) {
			throw new UnsupportedOperationException();
		}

		SubscrPriceList newPriceList = copyPriceList_L1(srcPriceList);
		newPriceList.setSrcPriceListId(srcPriceListId);
		newPriceList.setPriceListLevel(PRICE_LEVEL_RMA);
		newPriceList.setRmaSubscriber(rmaSubscriber);

		newPriceList.setSubscriber(subscriber);

		newPriceList.setPriceListType(calcPriceListType(rmaSubscriber, subscriber));
		newPriceList.setIsMaster(true);
		newPriceList.setIsDraft(false);
		newPriceList.setIsActive(false);

		subscrPriceListRepository.save(newPriceList);

		subscrPriceItemService.copySubscrPriceItems(srcPriceListId, newPriceList);

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
		newPriceList.setPriceOption(srcServicePriceList.getPriceOption());
		newPriceList.setPriceListCurrency(srcServicePriceList.getPriceListCurrency());
		newPriceList.setPlanBeginDate(srcServicePriceList.getPlanBeginDate());
		newPriceList.setPlanEndDate(srcServicePriceList.getPlanEndDate());
		newPriceList.setIsActive(false);
		newPriceList.setRmaSubscriber(srcServicePriceList.getRmaSubscriber());
		newPriceList.setSubscriber(srcServicePriceList.getSubscriber());
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

		subscrPriceItemService.deleteSubscrPriceItems(subscrPriceList.getId());
		subscrPriceListRepository.delete(subscrPriceList);
	}

	/**
	 * 
	 * @param subscriberIds
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public void deleteActivePriceList(Long rmaSubscriberId, Long subscriberId) {
		List<SubscrPriceList> activePriceLists = selectActiveRmaPriceList(rmaSubscriberId, subscriberId);
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param subscriberIds
	 */
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
	public List<SubscrPriceList> selectActiveRmaPriceList(Long rmaSubscriberId, Long subscriberId) {

		List<SubscrPriceList> activePriceLists = selectRmaPriceLists(rmaSubscriberId, subscriberId).stream()
				.filter(ObjectFilters.ACTIVE_OBJECT_PREDICATE).collect(Collectors.toList());

		return activePriceLists;
	}

	/**
	 * 
	 * @param subscriberIds
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPriceList> selectDraftRmaPriceLists(Long rmaSubscriberId, Long subscriberId) {

		List<SubscrPriceList> rmaPriceLists = selectRmaPriceLists(rmaSubscriberId, subscriberId);
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
	public List<SubscrPriceList> selectRmaPriceLists(Long rmaSubscriberId, Long subscriberId) {

		List<SubscrPriceList> rmaPriceLists = subscrPriceListRepository.selectByRma(rmaSubscriberId);

		final int priceListType = calcPriceListType(rmaSubscriberId, subscriberId);

		List<SubscrPriceList> resultPriceLists = rmaPriceLists.stream()
				.filter(i -> i.getPriceListLevel() == PRICE_LEVEL_RMA).filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.filter(i -> i.getPriceListType() != null && i.getPriceListType().equals(priceListType))
				.filter(i -> (priceListType == 1 && i.getSubscriberId() == null)
						|| (i.getSubscriberId() != null && i.getSubscriberId().equals(subscriberId)))
				.collect(Collectors.toList());

		return resultPriceLists;
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPriceList> selectRmaPriceLists(Long rmaSubscriberId) {
		return selectRmaPriceLists(rmaSubscriberId, null);
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

		List<SubscrPriceList> rmaPriceLists = subscrPriceListRepository.selectByRma(rmaSubscriberId);
		List<SubscrPriceList> resultPriceLists = rmaPriceLists.stream()
				.filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.filter(i -> i.getPriceListLevel() == PRICE_LEVEL_SUBSCRIBER)
				.filter(i -> i.getSubscriberId() != null && i.getSubscriberId().equals(subscriberId))
				.collect(Collectors.toList());

		return resultPriceLists;
	}

	/**
	 * Creates PriceList on the same level, as srcPriceListId
	 * 
	 * @param srcPriceListId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public SubscrPriceList createAnyDraftPriceList(Long srcPriceListId) {
		checkNotNull(srcPriceListId);
		SubscrPriceList srcPriceList = subscrPriceListRepository.findOne(srcPriceListId);
		if (srcPriceList == null) {
			throw new PersistenceException(String.format("SubscrPriceList (id=%d) is not found", srcPriceListId));
		}
		SubscrPriceList newPriceList = copyPriceList_L1(srcPriceList);
		newPriceList.setPriceListLevel(srcPriceList.getPriceListLevel());
		newPriceList.setIsActive(false);
		newPriceList.setIsArchive(false);
		newPriceList.setIsDraft(true);
		newPriceList.setPriceListType(srcPriceList.getPriceListType());
		newPriceList.setSrcPriceListId(srcPriceListId);
		newPriceList.setPriceListName(PRICE_LIST_PREFIX + srcPriceList.getPriceListName());

		if (Boolean.TRUE.equals(srcPriceList.getIsMaster())
				&& srcPriceList.getPriceListLevel().intValue() != PRICE_LEVEL_NMC) {
			newPriceList.setMasterPriceListId(srcPriceList.getId());
		}

		if (srcPriceList.getPriceListLevel().intValue() == PRICE_LEVEL_NMC) {
			newPriceList.setIsMaster(true);
			newPriceList.setMasterPriceListId(null);
		}

		subscrPriceListRepository.save(newPriceList);
		subscrPriceItemService.copySubscrPriceItems(srcPriceListId, newPriceList);
		return newPriceList;
	}

	/**
	 * Creates PriceList on the Subscriber Level
	 * 
	 * @param srcPriceListId
	 * @param subscriberId
	 * @param isActive
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public SubscrPriceList createSubscrPriceList(Long srcPriceListId, Subscriber subscriber, boolean isActive) {
		checkNotNull(srcPriceListId);
		checkNotNull(subscriber);
		SubscrPriceList srcPriceList = subscrPriceListRepository.findOne(srcPriceListId);
		if (srcPriceList == null) {
			throw new PersistenceException(String.format("SubscrPriceList (id=%d) is not found", srcPriceListId));
		}

		Long rmaSubscriberId = srcPriceList.getRmaSubscriberId();

		List<Long> rmaSubscribersIdList = rmaSubscriberService
				.selectRmaSubscriberIds(srcPriceList.getRmaSubscriberId());
		if (!rmaSubscribersIdList.contains(subscriber.getId())) {
			throw new PersistenceException(String.format("Subscriber (id=%d) is not set to rma (id=%d)",
					subscriber.getId(), srcPriceList.getRmaSubscriberId()));
		}

		if (srcPriceList.getSubscriberId() != null && !srcPriceList.getSubscriberId().equals(subscriber.getId())) {
			throw new PersistenceException(
					String.format("SubscrPriceList is not compatabile with Subscriber (id=%d)", subscriber.getId()));
		}

		if (isActive) {
			setInctiveSubscrActivePriceList(rmaSubscriberId, subscriber.getId());
		}

		SubscrPriceList newPriceList = copyPriceList_L1(srcPriceList);
		newPriceList.setSubscriber(subscriber);
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
		subscrPriceItemService.copySubscrPriceItems(srcPriceListId, newPriceList);
		return newPriceList;
	}

	/**
	 * Creates PriceList on the Subscriber Level
	 * 
	 * @param srcPriceListId
	 * @param subscriberIds
	 * @param activeIds
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public void createSubscrPriceLists(Long srcPriceListId, List<Long> subscriberIds, List<Long> activeIds) {
		checkNotNull(srcPriceListId);
		checkNotNull(subscriberIds);

		List<Long> activeIdList = activeIds != null ? new ArrayList<>(activeIds) : new ArrayList<>();
		for (Long id : subscriberIds) {
			boolean isActive = activeIdList.contains(id);
			createSubscrPriceList(srcPriceListId, subscriberService.findOneSubscriber(id), isActive);
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
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public SubscrPriceList updateOne(SubscrPriceList subscrPriceList) {
		checkNotNull(subscrPriceList);
		checkArgument(!subscrPriceList.isNew());
		// checkArgument(subscrPriceList.getPriceListLevel() !=
		// PRICE_LEVEL_NMC);
		checkArgument(Boolean.FALSE.equals(subscrPriceList.getIsActive()));
		checkArgument(Boolean.FALSE.equals(subscrPriceList.getIsArchive()));
		checkArgument(Boolean.TRUE.equals(subscrPriceList.getIsDraft()));

		Subscriber s1 = initSubscriber(subscrPriceList.getRmaSubscriberId());
		Subscriber s2 = initSubscriber(subscrPriceList.getSubscriberId());
		subscrPriceList.setRmaSubscriber(s1);
		subscrPriceList.setSubscriber(s2);

		return subscrPriceListRepository.save(subscrPriceList);
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param subscriberId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public int setInctiveSubscrActivePriceList(Long rmaSubscriberId, Long subscriberId) {
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

	/**
	 * 
	 * @param subscriberIds
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public int setInactiveRmaActivePriceList(Long rmaSubscriberId, Long subscriberId) {

		List<SubscrPriceList> activePriceLists = selectActiveRmaPriceList(rmaSubscriberId, subscriberId);

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

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param subscrPriceListId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public SubscrPriceList setActiveSubscrPriceList(Long subscrPriceListId, Subscriber rmaSubscriber) {
		checkNotNull(rmaSubscriber);
		checkNotNull(subscrPriceListId);

		SubscrPriceList subscrPriceList = subscrPriceListRepository.findOne(subscrPriceListId);
		if (subscrPriceList == null) {
			throw new PersistenceException(String.format("SubscrPriceList (id=%d) is not found", subscrPriceListId));
		}

		if (!rmaSubscriber.getId().equals(subscrPriceList.getRmaSubscriberId())) {
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

		setInctiveSubscrActivePriceList(subscrPriceList.getRmaSubscriberId(), subscrPriceList.getSubscriberId());

		Date rmaCurrentDate = rmaSubscriberService.getSubscriberCurrentTime(rmaSubscriber.getId());
		subscrPriceList.setFactBeginDate(rmaCurrentDate);
		subscrPriceList.setIsActive(true);
		subscrPriceList.setIsDraft(false);

		return subscrPriceListRepository.save(subscrPriceList);

	}

	/**
	 * 
	 * @param subscrPriceListId
	 * @param rmaSubscriber
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN })
	public SubscrPriceList setActiveRmaPriceList(Long subscrPriceListId, Subscriber rmaSubscriber) {
		checkNotNull(rmaSubscriber);
		checkNotNull(subscrPriceListId);

		SubscrPriceList subscrPriceList = subscrPriceListRepository.findOne(subscrPriceListId);
		if (subscrPriceList == null) {
			throw new PersistenceException(String.format("SubscrPriceList (id=%d) is not found", subscrPriceListId));
		}

		if (Boolean.TRUE.equals(subscrPriceList.getIsActive()) || Boolean.TRUE.equals(subscrPriceList.getIsArchive())) {
			throw new PersistenceException(
					String.format("SubscrPriceList (id=%d) is in wrong state", subscrPriceListId));
		}

		setInactiveRmaActivePriceList(subscrPriceList.getRmaSubscriberId(), subscrPriceList.getSubscriberId());

		Date rmaCurrentDate = rmaSubscriberService.getSubscriberCurrentTime(rmaSubscriber.getId());
		subscrPriceList.setFactBeginDate(rmaCurrentDate);
		subscrPriceList.setIsActive(true);
		subscrPriceList.setIsDraft(false);

		return subscrPriceListRepository.save(subscrPriceList);

	}

	/**
	 * 
	 * @param subscrPriceListId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public SubscrPriceList findOne(Long subscrPriceListId) {
		return subscrPriceListRepository.findOne(subscrPriceListId);
	}

	/**
	 * 
	 * @param subscrPriceList
	 * @return
	 */
	private Long[] createSubscriberIds(SubscrPriceList subscrPriceList) {

		Long[] resut = null;

		if (resut == null && subscrPriceList.getSubscriberId() != null) {
			resut = new Long[] { subscrPriceList.getRmaSubscriberId(), subscrPriceList.getSubscriberId() };
		}

		if (resut == null && subscrPriceList.getRmaSubscriberId() != null) {
			resut = new Long[] { subscrPriceList.getRmaSubscriberId() };
		}

		checkSubscriberIds(resut);

		return resut;
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param subscriberId
	 * @return
	 */
	private int calcPriceListType(Long rmaSubscriberId, Long subscriberId) {
		return (rmaSubscriberId != null && subscriberId != null) ? 2 : 1;
	}

	private int calcPriceListType(Subscriber rmaSubscriber, Subscriber subscriber) {
		checkNotNull(rmaSubscriber);
		checkNotNull(rmaSubscriber.getId());
		return subscriber != null ? calcPriceListType(rmaSubscriber.getId(), subscriber.getId()) : 1;
	}

	/**
	 * 
	 * @param subscriberId1
	 * @param subscriberId2
	 * @return
	 */
	private Long[] createSubscriberIds(Long subscriberId1, Long subscriberId2) {
		if (subscriberId2 != null) {
			return new Long[] { subscriberId1, subscriberId2 };
		}
		return new Long[] { subscriberId1 };
	}

}
