package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceItem;

public interface SubscrPriceItemRepository extends CrudRepository<SubscrPriceItem, Long> {

	public List<SubscrPriceItem> findBySubscrPriceListId(Long subscrPriceListId);
}
