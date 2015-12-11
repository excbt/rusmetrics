package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceItemValue;

public interface SubscrPriceItemValueRepository extends CrudRepository<SubscrPriceItemValue, Long> {

	@Query("SELECT o FROM SubscrPriceItemValue o WHERE o.subcrPriceItemId = :subscrPriceItemId")
	public List<SubscrPriceItemValue> selectSubscrPriceItemValue(@Param("subscrPriceItemId") Long subscrPriceItemId);
}
