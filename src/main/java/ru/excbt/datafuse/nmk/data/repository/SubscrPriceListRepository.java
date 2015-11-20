package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;

public interface SubscrPriceListRepository extends CrudRepository<SubscrPriceList, Long> {

	/**
	 * 
	 * @param priceListLevel
	 * @return
	 */
	@Query("SELECT pl FROM SubscrPriceList pl WHERE pl.priceListLevel = :priceListLevel"
			+ " ORDER BY pl.priceListLevel, pl.priceListKeyname, pl.isMaster, pl.isArchive, pl.isDraft DESC, pl.isActive, pl.factBeginDate DESC")
	public List<SubscrPriceList> selectByLevel(@Param("priceListLevel") Integer priceListLevel);

	/**
	 * 
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT pl FROM SubscrPriceList pl WHERE pl.rmaSubscriberId = :rmaSubscriberId "
			+ " ORDER BY pl.priceListLevel, pl.isMaster, pl.isArchive, pl.isDraft DESC, pl.isActive, pl.factBeginDate DESC")
	public List<SubscrPriceList> selectByRma(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT pl FROM SubscrPriceList pl WHERE pl.subscriberId = :subscriberId "
			+ " ORDER BY pl.priceListLevel, pl.isMaster, pl.isArchive, pl.isDraft DESC, pl.isActive, pl.factBeginDate DESC")
	public List<SubscrPriceList> selectBySubscriber(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT count(*) FROM SubscrPriceList pl WHERE pl.subscriberId = :subscriberId AND pl.isActive = true AND pl.priceListLevel = 2 ")
	public Long selectActiveCountBySubscriber(@Param("subscriberId") Long subscriberId);

}
