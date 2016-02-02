package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceItemValue;

/**
 * Repository для SubscrPriceItemValue
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 17.11.2015
 *
 */
public interface SubscrPriceItemValueRepository extends CrudRepository<SubscrPriceItemValue, Long> {

	@Query("SELECT o FROM SubscrPriceItemValue o WHERE o.subcrPriceItemId = :subscrPriceItemId")
	public List<SubscrPriceItemValue> selectSubscrPriceItemValue(@Param("subscrPriceItemId") Long subscrPriceItemId);
}
