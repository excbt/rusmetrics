package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;

public interface SubscrPriceListRepository extends CrudRepository<SubscrPriceList, Long> {

	@Query("SELECT pl FROM SubscrPriceList pl WHERE pl.priceListLevel = :priceListLevel")
	public List<SubscrPriceList> findByLevel(@Param("priceListLevel") Integer priceListLevel);

	@Query("SELECT pl FROM SubscrPriceList pl WHERE pl.subscriberId1 = :subscriberId")
	public List<SubscrPriceList> findByRma(@Param("subscriberId") Long subscriberId);

}
