package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ServicePriceItem;

public interface ServicePriceItemRepository extends CrudRepository<ServicePriceItem, Long> {

	public List<ServicePriceItem> findByServicePriceListId(Long servicePriceListId);
}
