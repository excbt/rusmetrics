package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrPriceList;

/**
 * Repository для SubscrPriceList
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 11.11.2015
 *
 */
public interface SubscrPriceListRepository extends CrudRepository<SubscrPriceList, Long> {

	/**
	 * 
	 * @param priceListLevel
	 * @return
	 */
	@Query("SELECT pl FROM SubscrPriceList pl WHERE pl.priceListLevel = :priceListLevel"
			+ " ORDER BY pl.priceListKeyname NULLS LAST, pl.priceListLevel, pl.isMaster DESC, pl.isArchive, pl.isDraft DESC, pl.isActive, "
			+ " COALESCE(pl.factBeginDate, pl.planBeginDate) DESC, pl.priceListName ")
	public List<SubscrPriceList> selectByLevel(@Param("priceListLevel") Integer priceListLevel);

	/**
	 * 
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT pl FROM SubscrPriceList pl WHERE pl.rmaSubscriberId = :rmaSubscriberId "
			+ " ORDER BY pl.priceListLevel, pl.isMaster DESC, pl.isArchive, pl.isDraft DESC, pl.isActive, "
			+ " COALESCE(pl.factBeginDate, pl.planBeginDate) DESC, pl.priceListName ")
	public List<SubscrPriceList> selectByRma(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT pl FROM SubscrPriceList pl WHERE pl.subscriberId = :subscriberId "
			+ " ORDER BY pl.priceListLevel, pl.isMaster DESC, pl.isArchive, pl.isDraft DESC, pl.isActive, "
			+ " COALESCE(pl.factBeginDate, pl.planBeginDate) DESC, pl.priceListName ")
	public List<SubscrPriceList> selectBySubscriber(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT count(*) FROM SubscrPriceList pl WHERE pl.subscriberId = :subscriberId AND pl.isActive = true AND pl.priceListLevel = 2 ")
	public Long selectActiveCountBySubscriber(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT pl FROM SubscrPriceList pl "
			+ " WHERE pl.subscriberId = :subscriberId AND pl.isActive = true AND pl.priceListLevel = 2 ")
	public List<SubscrPriceList> selectActiveBySubscriber(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT pl FROM SubscrPriceList pl "
			+ " WHERE pl.rmaSubscriberId = :rmaSubscriberId AND pl.isActive = true AND pl.priceListLevel = 1 "
			+ " AND pl.subscriberId IS NULL")
	public List<SubscrPriceList> selectActiveByRmaSubscriber(@Param("rmaSubscriberId") Long rmaSubscriberId);

}
