package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.repository.SubscrPriceListRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrPriceListService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrPriceListService.class);

	private final static String PRICE_LIST_PREFIX = "Черновик ";

	private final static int PRICE_LEVEL_NMC = 0;
	private final static int PRICE_LEVEL_RMA = 1;

	@Autowired
	private SubscrPriceListRepository subscrPriceListRepository;

	@Autowired
	private SubscriberService subscriberService;

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

		Long rmaSubscriberId = subscriberIds[0];

		SubscrPriceList srcPriceList = subscrPriceListRepository.findOne(srcServicePriceListId);
		if (srcPriceList == null) {
			throw new PersistenceException(
					String.format("ServicePriceList (id=%d) is not found", srcServicePriceListId));
		}

		if (srcPriceList.getPriceListLevel() != PRICE_LEVEL_NMC) {
			throw new UnsupportedOperationException();
		}

		SubscrPriceList newPriceList = copyServicePriceList(srcPriceList);
		newPriceList.setSrcPriceListId(srcServicePriceListId);
		newPriceList.setPriceListLevel(PRICE_LEVEL_RMA);
		newPriceList.setSubscriberId1(rmaSubscriberId);

		if (subscriberIds.length > 1) {
			newPriceList.setSubscriberId2(subscriberIds[1]);
		}
		if (subscriberIds.length > 2) {
			newPriceList.setSubscriberId3(subscriberIds[2]);
		}
		newPriceList.setPriceListType(subscriberIds.length);
		newPriceList.setIsMaster(true);
		newPriceList.setIsDraft(false);
		newPriceList.setIsActive(isActive);

		if (isActive) {
			Date rmaCurrentDate = subscriberService.getSubscriberCurrentTime(rmaSubscriberId);
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

	private SubscrPriceList copyServicePriceList(SubscrPriceList srcServicePriceList) {
		checkNotNull(srcServicePriceList);
		checkArgument(!srcServicePriceList.isNew());
		SubscrPriceList newPriceList = new SubscrPriceList();
		newPriceList.setPriceListName(srcServicePriceList.getPriceListName());
		newPriceList.setPriceListOption(srcServicePriceList.getPriceListOption());
		newPriceList.setPlanBeginDate(srcServicePriceList.getPlanBeginDate());
		newPriceList.setPlanEndDate(srcServicePriceList.getPlanEndDate());
		newPriceList.setIsActive(false);
		return newPriceList;
	}

	/**
	 * 
	 * @param subscrPriceList
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public void deleteSubcrPriceList(SubscrPriceList subscrPriceList) {
		checkNotNull(subscrPriceList);
		checkArgument(!subscrPriceList.isNew());
		checkArgument(subscrPriceList.getPriceListLevel() != PRICE_LEVEL_NMC);
	}

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPriceList> findRmaPriceLists(Long subscriberId) {
		List<SubscrPriceList> preResult = subscrPriceListRepository.findByRma(subscriberId);

		List<SubscrPriceList> result = ObjectFilters.deletedFilter(preResult);

		return result;
	}

}
