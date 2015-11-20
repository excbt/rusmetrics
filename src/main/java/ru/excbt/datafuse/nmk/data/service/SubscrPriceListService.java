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

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceItemVO;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.repository.SubscrPriceListRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrPriceListService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrPriceListService.class);

	private final static String PRICE_LIST_PREFIX = "Копия ";

	public final static int PRICE_LEVEL_NMC = 0;
	public final static int PRICE_LEVEL_RMA = 1;
	public final static int PRICE_LEVEL_SUBSCRIBER = 2;
	public final static String DEFAULT_PRICE_LIST_KEYNAME = "DEFAULT";

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
	public List<SubscrPriceList> selectRootPriceLists() {
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
	public SubscrPriceList selectRootPriceLists(String priceListKeyname) {
		List<SubscrPriceList> list = subscrPriceListRepository.selectByLevel(0);

		Optional<SubscrPriceList> preResult = list.stream()
				.filter(i -> priceListKeyname == null || priceListKeyname.equalsIgnoreCase(i.getPriceListKeyname()))
				.findFirst();

		return preResult.isPresent() ? preResult.get() : null;
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
	@Secured({ ROLE_ADMIN })
	public void deleteSubscrPriceList(SubscrPriceList subscrPriceList) {
		checkNotNull(subscrPriceList);
		checkArgument(!subscrPriceList.isNew());
		checkArgument(subscrPriceList.getPriceListLevel() != PRICE_LEVEL_NMC);

		subscrPriceItemService.deleteSubscrPriceItems(subscrPriceList.getId());
		subscrPriceListRepository.delete(subscrPriceList);
	}

	/**
	 * 
	 * @param subscrPriceList
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public void softDeleteSubscrPriceList(SubscrPriceList subscrPriceList) {
		checkNotNull(subscrPriceList);
		checkArgument(!subscrPriceList.isNew());

		if (subscrPriceList.getPriceListLevel() != PRICE_LEVEL_SUBSCRIBER) {
			throw new PersistenceException(
					String.format("Delete SubscrPriceList(id=%d) is not accepted", subscrPriceList.getId()));
		}

		if (Boolean.TRUE.equals(subscrPriceList.getIsActive())) {
			throw new PersistenceException(
					String.format("Delete Active SubscrPriceList(id=%d) is not accepted", subscrPriceList.getId()));
		}

		subscrPriceList.setDeleted(1);

		subscrPriceListRepository.save(subscrPriceList);
	}

	/**
	 * 
	 * @param subscrPriceList
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public void softDeleteRmaPriceList(SubscrPriceList subscrPriceList) {
		checkNotNull(subscrPriceList);
		checkArgument(!subscrPriceList.isNew());

		if (subscrPriceList.getPriceListLevel() != PRICE_LEVEL_RMA) {
			throw new PersistenceException(
					String.format("Delete SubscrPriceList(id=%d) is not accepted", subscrPriceList.getId()));
		}

		if (Boolean.TRUE.equals(subscrPriceList.getIsActive())) {
			throw new PersistenceException(
					String.format("Delete Active SubscrPriceList(id=%d) is not accepted", subscrPriceList.getId()));
		}

		subscrPriceList.setDeleted(1);

		logger.info("version222:{}", subscrPriceList.getVersion());

		subscrPriceListRepository.save(subscrPriceList);
	}

	/**
	 * 
	 * @param subscrPriceList
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public void softDeleteRootPriceList(SubscrPriceList subscrPriceList) {
		checkNotNull(subscrPriceList);
		checkArgument(!subscrPriceList.isNew());

		if (subscrPriceList.getPriceListLevel() != PRICE_LEVEL_NMC) {
			throw new PersistenceException(
					String.format("Delete SubscrPriceList(id=%d) is not accepted", subscrPriceList.getId()));
		}

		if ("DEFAULT".equals(subscrPriceList.getPriceListKeyname())) {
			throw new PersistenceException(
					String.format("Delete DEFAULT SubscrPriceList(id=%d) is not accepted", subscrPriceList.getId()));
		}

		subscrPriceList.setDeleted(1);

		subscrPriceListRepository.save(subscrPriceList);
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
				//.filter(i -> i.getPriceListType() != null && i.getPriceListType().equals(priceListType))
				.filter(i -> (subscriberId == null) || (priceListType == 1 && i.getSubscriberId() == null)
						|| (priceListType == 2 && i.getSubscriberId() != null
								&& i.getSubscriberId().equals(subscriberId)))
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
	public List<SubscrPriceList> selectSubscriberPriceLists(Long rmaSubscriberId, Long subscriberId) {
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
	 * 
	 * @param srcPriceListId
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN })
	public SubscrPriceList createRmaPriceList(Long srcPriceListId, Subscriber rmaSubscriber) {
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

		newPriceList.setPriceListType(calcPriceListType(rmaSubscriber, srcPriceList.getSubscriber()));
		newPriceList.setIsMaster(true);
		newPriceList.setIsDraft(false);
		newPriceList.setIsActive(false);

		subscrPriceListRepository.save(newPriceList);

		subscrPriceItemService.copySubscrPriceItems(srcPriceListId, newPriceList);

		return newPriceList;
	}

	/**
	 * 
	 * @param srcPriceListId
	 * @param rmaSubscriberIds
	 * @param subscriber
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN })
	public void createRmaPriceLists(Long srcPriceListId, List<Long> rmaSubscriberIds, List<Long> activeIds) {
		checkNotNull(srcPriceListId);
		checkNotNull(rmaSubscriberIds);

		for (Long id : rmaSubscriberIds) {
			SubscrPriceList createdPriceList = createRmaPriceList(srcPriceListId, subscriberService.findOne(id));
			boolean isActive = activeIds != null && activeIds.contains(id);
			LocalDate startDate = subscriberService.getSubscriberCurrentDateJoda(id);
			if (isActive) {
				activateRmaPriceList(createdPriceList.getId(), startDate);
			}
		}
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
	public SubscrPriceList createSubscrPriceList(Long srcPriceListId, Subscriber subscriber) {
		checkNotNull(srcPriceListId);
		checkNotNull(subscriber);
		SubscrPriceList srcPriceList = subscrPriceListRepository.findOne(srcPriceListId);
		if (srcPriceList == null) {
			throw new PersistenceException(String.format("SubscrPriceList (id=%d) is not found", srcPriceListId));
		}

		if (srcPriceList.getIsArchive()) {
			throw new PersistenceException(String
					.format("Archive SubscrPriceList (id=%d) is not allowed to assing to Subscribers", srcPriceListId));
		}

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

		SubscrPriceList newPriceList = copyPriceList_L1(srcPriceList);
		newPriceList.setSubscriber(subscriber);
		newPriceList.setPriceListLevel(PRICE_LEVEL_SUBSCRIBER);
		newPriceList.setIsArchive(false);
		newPriceList.setIsDraft(true);
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

		for (Long id : subscriberIds) {
			SubscrPriceList createdPriceList = createSubscrPriceList(srcPriceListId, subscriberService.findOne(id));
			boolean isActive = activeIds != null && activeIds.contains(id);
			LocalDate startDate = subscriberService.getSubscriberCurrentDateJoda(id);
			if (isActive) {
				activateSubscrPriceList(createdPriceList.getId(), startDate);
			}
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

		checkArgument(Boolean.FALSE.equals(subscrPriceList.getIsActive()));
		checkArgument(Boolean.FALSE.equals(subscrPriceList.getIsArchive()));
		checkArgument(Boolean.TRUE.equals(subscrPriceList.getIsDraft()));

		if (subscrPriceList.getPriceListLevel().intValue() == 0
				&& DEFAULT_PRICE_LIST_KEYNAME.equals(subscrPriceList.getPriceListKeyname())) {
			throw new PersistenceException(
					String.format("Edit DEFAULT price list (id=%d) is not allowed", subscrPriceList.getId()));
		}

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
	public int deactiveOtherSubscrPriceLists(SubscrPriceList subscrPriceList) {
		checkNotNull(subscrPriceList);

		Long rmaSubscriberId = subscrPriceList.getRmaSubscriber().getId();
		Long subscriberId = subscrPriceList.getSubscriber() != null ? subscrPriceList.getSubscriber().getId() : null;
		return deactiveOtherSubscrPriceLists(rmaSubscriberId, subscriberId);
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public int deactiveOtherSubscrPriceLists(Long rmaSubscriberId, Long subscriberId) {
		checkNotNull(rmaSubscriberId);
		checkNotNull(subscriberId);

		List<SubscrPriceList> subscrPriceLists = selectSubscriberPriceLists(rmaSubscriberId, subscriberId);
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
	 * @param subscrPriceList
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public int deactiveOtherRmaPriceLists(SubscrPriceList subscrPriceList) {
		Long rmaSubscriberId = subscrPriceList.getRmaSubscriber().getId();
		Long subscriberId = subscrPriceList.getSubscriber() != null ? subscrPriceList.getSubscriber().getId() : null;

		return deactiveOtherRmaPriceLists(rmaSubscriberId, subscriberId);
	}

	/**
	 * 
	 * @param subscriberIds
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public int deactiveOtherRmaPriceLists(Long rmaSubscriberId, Long subscriberId) {
		checkNotNull(rmaSubscriberId);

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
	public SubscrPriceList activateSubscrPriceList(Long subscrPriceListId, LocalDate startDate) {
		checkNotNull(subscrPriceListId);
		checkNotNull(startDate);

		SubscrPriceList subscrPriceList = subscrPriceListRepository.findOne(subscrPriceListId);
		if (subscrPriceList == null) {
			throw new PersistenceException(String.format("SubscrPriceList (id=%d) is not found", subscrPriceListId));
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

		deactiveOtherSubscrPriceLists(subscrPriceList);

		int count = selectSubscrActiveCount(subscrPriceList.getSubscriber());
		checkState(count == 0);

		Date rmaCurrentDate = startDate.toDate();
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
	public SubscrPriceList activateRmaPriceList(Long subscrPriceListId, LocalDate startDate) {
		checkNotNull(startDate);
		checkNotNull(subscrPriceListId);

		SubscrPriceList subscrPriceList = subscrPriceListRepository.findOne(subscrPriceListId);
		if (subscrPriceList == null) {
			throw new PersistenceException(String.format("SubscrPriceList (id=%d) is not found", subscrPriceListId));
		}

		if (Boolean.TRUE.equals(subscrPriceList.getIsActive()) || Boolean.TRUE.equals(subscrPriceList.getIsArchive())) {
			throw new PersistenceException(
					String.format("SubscrPriceList (id=%d) is in wrong state", subscrPriceListId));
		}

		deactiveOtherRmaPriceLists(subscrPriceList);

		Date rmaCurrentDate = startDate.toDate();
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
	 * @param rmaSubscriberId
	 * @param subscriberId
	 * @return
	 */
	private int calcPriceListType(Long rmaSubscriberId, Long subscriberId) {
		return (rmaSubscriberId != null && subscriberId != null) ? 2 : 1;
	}

	/**
	 * 
	 * @param rmaSubscriber
	 * @param subscriber
	 * @return
	 */
	private int calcPriceListType(Subscriber rmaSubscriber, Subscriber subscriber) {
		checkNotNull(rmaSubscriber);
		checkNotNull(rmaSubscriber.getId());
		return subscriber != null ? calcPriceListType(rmaSubscriber.getId(), subscriber.getId()) : 1;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public int selectSubscrActiveCount(Long subscriberId) {
		Long result = subscrPriceListRepository.selectActiveCountBySubscriber(subscriberId);
		return result != null ? result.intValue() : 0;
	}

	/**
	 * 
	 * @param subscriber
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public int selectSubscrActiveCount(Subscriber subscriber) {
		checkNotNull(subscriber);
		Long result = subscrPriceListRepository.selectActiveCountBySubscriber(subscriber.getId());
		return result != null ? result.intValue() : 0;
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrPriceItemVO> selectActiveSubscrPriceListItemVOs(Long subscriberId) {
		List<SubscrPriceList> subscrPriceLists = subscrPriceListRepository.selectActiveBySubscriber(subscriberId);
		if (subscrPriceLists.size() == 0) {
			return new ArrayList<>();
		}
		SubscrPriceList pl = subscrPriceLists.get(0);

		List<SubscrPriceItemVO> result = subscrPriceItemService.findPriceItemVOs(pl.getId());
		result.forEach(i -> {
			i.setCurrency(pl.getPriceListCurrency());
		});

		return result;
	}

	/**
	 * 
	 * @param rmaSubscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrPriceItemVO> selectActiveRmaPriceListItemVOs(Long rmaSubscriberId) {
		List<SubscrPriceList> subscrPriceLists = subscrPriceListRepository.selectActiveByRmaSubscriber(rmaSubscriberId);
		if (subscrPriceLists.size() == 0) {
			return new ArrayList<>();
		}
		SubscrPriceList pl = subscrPriceLists.get(0);

		List<SubscrPriceItemVO> result = subscrPriceItemService.findPriceItemVOs(pl.getId());
		result.forEach(i -> {
			i.setCurrency(pl.getPriceListCurrency());
		});
		return result;
	}

}
