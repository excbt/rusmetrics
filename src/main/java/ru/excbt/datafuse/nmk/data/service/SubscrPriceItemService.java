package ru.excbt.datafuse.nmk.data.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.excbt.datafuse.nmk.config.jpa.TxConst;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceItem;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceItemVO;
import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;
import ru.excbt.datafuse.nmk.data.model.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.repository.SubscrPriceItemRepository;
import ru.excbt.datafuse.nmk.security.SecuredRoles;

@Service
public class SubscrPriceItemService implements SecuredRoles {

	@Autowired
	private SubscrPriceItemRepository servicePriceItemRepository;

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

	@Transactional(value = TxConst.TX_DEFAULT)
	public List<SubscrPriceItem> updateSubscrPriceItems(SubscrPriceList subscrPriceList,
			List<SubscrPriceItemVO> subscrPriceItemVOs) {
		return null;
	}

	/**
	 * 
	 * @param subscrPriceItems
	 * @return
	 */
	public List<SubscrPriceItemVO> makeSubscrPriceItemVOs(List<SubscrPriceItem> subscrPriceItems) {
		if (subscrPriceItems == null) {
			return null;
		}
		if (subscrPriceItems.size() == 0) {
			return new ArrayList<>();
		}
		return subscrPriceItems.stream().map(i -> new SubscrPriceItemVO(i)).collect(Collectors.toList());

	}

}
