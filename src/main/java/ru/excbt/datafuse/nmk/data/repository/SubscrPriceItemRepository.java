package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceItem;

/**
 * Repository для SubscrPriceItem
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 11.11.2015
 *
 */
public interface SubscrPriceItemRepository extends CrudRepository<SubscrPriceItem, Long> {

	public List<SubscrPriceItem> findBySubscrPriceListId(Long subscrPriceListId);
}
