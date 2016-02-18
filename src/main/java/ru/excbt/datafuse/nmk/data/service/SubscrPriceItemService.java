package ru.excbt.datafuse.nmk.data.service;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceItemVO;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceItemValue;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.repository.SubscrPriceItemRepository;
import ru.excbt.datafuse.nmk.data.repository.SubscrPriceItemValueRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

/**
 * Сервис для работы с элементами прайс листов абонентов
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.11.2015
 *
 */
@Service
public class SubscrPriceItemService implements SecuredRoles {

	private static final Logger logger = LoggerFactory.getLogger(SubscrPriceItemService.class);

	@Autowired
	private SubscrPriceItemRepository servicePriceItemRepository;

	@Autowired
	private SubscrPriceItemValueRepository servicePriceItemValueRepository;

	/**
	 * 
	 * @param subscrPriceListId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPriceItem> findPriceItems(Long subscrPriceListId) {
		return servicePriceItemRepository.findBySubscrPriceListId(subscrPriceListId);
	}

	/**
	 * 
	 * @param subscrPriceListId
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT, readOnly = true)
	public List<SubscrPriceItemVO> findPriceItemVOs(Long subscrPriceListId) {
		List<SubscrPriceItem> subscrPriceItems = findPriceItems(subscrPriceListId);
		List<SubscrPriceItemVO> resultList = subscrPriceItems.stream().map(i -> new SubscrPriceItemVO(i))
				.collect(Collectors.toList());
		return resultList;
	}

	/**
	 * 
	 * @param subscrPriceListId
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public void deleteSubscrPriceItems(Long subscrPriceListId) {
		List<SubscrPriceItem> items = servicePriceItemRepository.findBySubscrPriceListId(subscrPriceListId);
		servicePriceItemRepository.delete(items);
	}

	/**
	 * 
	 * @param srcSubscrPriceListId
	 * @param dstSubscrPriceList
	 * @return
	 */
	@Transactional(value = TxConst.TX_DEFAULT)
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	public List<SubscrPriceItem> copySubscrPriceItems(Long srcSubscrPriceListId, SubscrPriceList dstSubscrPriceList) {
		List<SubscrPriceItem> resultList = new ArrayList<>();
		List<SubscrPriceItem> items = servicePriceItemRepository.findBySubscrPriceListId(srcSubscrPriceListId);
		if (items.isEmpty()) {
			return resultList;
		}
		List<SubscrPriceItem> srsItems = items.stream().filter(ObjectFilters.NO_DELETED_OBJECT_PREDICATE)
				.collect(Collectors.toList());

		srsItems.forEach(i -> {
			resultList.add(copySubscrPriceItem(i));
		});

		resultList.forEach(i -> {
			i.setSubscrPriceList(dstSubscrPriceList);
		});

		servicePriceItemRepository.save(resultList);

		return resultList;
	}

	/**
	 * 
	 * @param srcSubscrPriceItem
	 * @return
	 */
	private SubscrPriceItem copySubscrPriceItem(SubscrPriceItem srcSubscrPriceItem) {
		SubscrPriceItem result = new SubscrPriceItem();
		result.setSrcPriceItemId(srcSubscrPriceItem.getId());
		result.setSubscrServicePackId(srcSubscrPriceItem.getSubscrServicePackId());
		result.setSubscrServiceItemId(srcSubscrPriceItem.getSubscrServiceItemId());
		result.setPriceOption(srcSubscrPriceItem.getPriceOption());
		result.setIsSinglePrice(srcSubscrPriceItem.getIsSinglePrice());
		result.setPriceValue(srcSubscrPriceItem.getPriceValue() == null ? null
				: srcSubscrPriceItem.getPriceValue().multiply(BigDecimal.ONE));
		return result;
	}

	/**
	 * 
	 * @param subscrPriceList
	 * @param subscrPriceItemVOs
	 * @return
	 */
	@Secured({ ROLE_ADMIN, ROLE_RMA_SUBSCRIBER_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrPriceItem> updateSubscrPriceItemValues(SubscrPriceList subscrPriceList,
			List<SubscrPriceItemVO> subscrPriceItemVOs, LocalDate localDate) {
		checkNotNull(subscrPriceList);
		checkNotNull(localDate);

		List<SubscrPriceItem> currentPriceItems = servicePriceItemRepository
				.findBySubscrPriceListId(subscrPriceList.getId());

		Map<Long, SubscrPriceItemVO> voList = new HashMap<>();
		subscrPriceItemVOs.forEach(i -> {
			voList.put(i.getId(), i);
		});

		List<SubscrPriceItem> modifiedPriceItems = currentPriceItems.stream().filter(i -> voList.containsKey(i.getId()))
				.collect(Collectors.toList());

		List<SubscrPriceItemValue> priceItemValues = new ArrayList<>();

		modifiedPriceItems.forEach(i -> {
			SubscrPriceItemVO newValue = voList.get(i.getId());
			if (newValue != null && newValue.getPriceValue() != null
					&& !newValue.getPriceValue().equals(i.getPriceValue())) {
				i.setPriceValue(newValue != null ? newValue.getPriceValue() : null);
				SubscrPriceItemValue itemValue = new SubscrPriceItemValue();
				itemValue.setSubcrPriceItem(i);
				itemValue.setPriceValue(newValue.getPriceValue());
				itemValue.setValueBeginDate(localDate.toDate());
				priceItemValues.add(itemValue);
			}
		});

		Set<Long> modifiedPriceItemIds = modifiedPriceItems.stream().map(i -> i.getId()).collect(Collectors.toSet());

		updatePriceListValues(modifiedPriceItemIds, localDate);

		servicePriceItemRepository.save(modifiedPriceItems);
		servicePriceItemValueRepository.save(priceItemValues);

		return modifiedPriceItems;
	}

	/**
	 * 
	 * @param subscrPriceList
	 * @param subscrPriceItemVOs
	 * @param localDate
	 * @return
	 */
	@Secured({ ROLE_ADMIN })
	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrPriceItem> updateRmaPriceItemValues(SubscrPriceList subscrPriceList,
			List<SubscrPriceItemVO> subscrPriceItemVOs, LocalDate localDate) {
		checkNotNull(subscrPriceList);
		checkNotNull(localDate);

		List<SubscrPriceItem> currentPriceItems = servicePriceItemRepository
				.findBySubscrPriceListId(subscrPriceList.getId());

		Map<Long, SubscrPriceItemVO> voList = new HashMap<>();
		subscrPriceItemVOs.forEach(i -> {
			voList.put(i.getId(), i);
		});

		List<SubscrPriceItem> modifiedPriceItems = currentPriceItems.stream().filter(i -> voList.containsKey(i.getId()))
				.collect(Collectors.toList());

		List<SubscrPriceItem> newPriceItems = subscrPriceItemVOs.stream().filter(i -> i.getId() == null).map(i -> {
			SubscrPriceItem item = new SubscrPriceItem();
			item.setSubscrPriceList(subscrPriceList);
			item.setSubscrServiceItemId(i.getItemId());
			item.setSubscrServicePackId(i.getPackId());
			item.setPriceValue(i.getPriceValue());
			return item;
		}).collect(Collectors.toList());

		List<SubscrPriceItemValue> priceItemValues = new ArrayList<>();

		modifiedPriceItems.forEach(i -> {
			SubscrPriceItemVO newValue = voList.get(i.getId());
			if (newValue != null && newValue.getPriceValue() != null
					&& !newValue.getPriceValue().equals(i.getPriceValue())) {
				i.setPriceValue(newValue != null ? newValue.getPriceValue() : null);
				SubscrPriceItemValue itemValue = new SubscrPriceItemValue();
				itemValue.setSubcrPriceItem(i);
				itemValue.setPriceValue(newValue.getPriceValue());
				itemValue.setValueBeginDate(localDate.toDate());
				priceItemValues.add(itemValue);
			}
		});

		newPriceItems.forEach(i -> {
			SubscrPriceItemValue itemValue = new SubscrPriceItemValue();
			itemValue.setSubcrPriceItem(i);
			itemValue.setPriceValue(i.getPriceValue());
			itemValue.setValueBeginDate(localDate.toDate());
			priceItemValues.add(itemValue);
		});

		Set<Long> modifiedPriceItemIds = modifiedPriceItems.stream().map(i -> i.getId()).collect(Collectors.toSet());

		updatePriceListValues(modifiedPriceItemIds, localDate);

		servicePriceItemRepository.save(newPriceItems);
		servicePriceItemRepository.save(modifiedPriceItems);
		servicePriceItemValueRepository.save(priceItemValues);

		return modifiedPriceItems;
	}

	/**
	 * 
	 * @param subscrPriceItems
	 * @return
	 */
	public List<SubscrPriceItemVO> convertSubscrPriceItemVOs(List<SubscrPriceItem> subscrPriceItems) {
		if (subscrPriceItems == null) {
			return null;
		}
		if (subscrPriceItems.size() == 0) {
			return new ArrayList<>();
		}
		return subscrPriceItems.stream().map(i -> new SubscrPriceItemVO(i)).collect(Collectors.toList());

	}

	/**
	 * 
	 * @param modifiedPriceItemIds
	 * @param endDate
	 */
	private void updatePriceListValues(Collection<Long> modifiedPriceItemIds, LocalDate endDate) {

		List<SubscrPriceItemValue> allCurrentValues = new ArrayList<>();

		modifiedPriceItemIds.forEach(i -> {
			List<SubscrPriceItemValue> currentValues = servicePriceItemValueRepository.selectSubscrPriceItemValue(i);
			currentValues.forEach(cv -> {
				cv.setValueEndDate(endDate.toDate());
			});
			allCurrentValues.addAll(allCurrentValues);
		});

		servicePriceItemValueRepository.save(allCurrentValues);

	}

}
