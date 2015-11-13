package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;

public interface SubscrPriceListRepository extends CrudRepository<SubscrPriceList, Long> {

	@Query("SELECT pl FROM SubscrPriceList pl WHERE pl.priceListLevel = :priceListLevel")
	public List<SubscrPriceList> findByLevel(@Param("priceListLevel") Integer priceListLevel);

	@Query("SELECT pl FROM SubscrPriceList pl WHERE pl.subscriberId1 = :subscriberId "
			+ " ORDER BY pl.priceListLevel, pl.isArchive, pl.isDraft DESC, pl.isActive, pl.factBeginDate DESC")
	public List<SubscrPriceList> findByRma(@Param("subscriberId") Long subscriberId);

	@Query("SELECT pl FROM SubscrPriceList pl WHERE pl.subscriberId2 = :subscriberId "
			+ " ORDER BY pl.priceListLevel, pl.isArchive, pl.isDraft DESC, pl.isActive, pl.factBeginDate DESC")
	public List<SubscrPriceList> findBySubscriber(@Param("subscriberId") Long subscriberId);

}
