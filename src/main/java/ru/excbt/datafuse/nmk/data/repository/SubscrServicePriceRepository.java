package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrServicePrice;

public interface SubscrServicePriceRepository extends CrudRepository<SubscrServicePrice, Long> {

	public List<SubscrServicePrice> findByPackIdOrderByPriceBeginDateDescIdDesc(Long packId);

	public List<SubscrServicePrice> findByItemIdOrderByPriceBeginDateDescIdDesc(Long itemId);

	/**
	 * 
	 * @param packId
	 * @param priceDate
	 * @return
	 */
	@Query("SELECT p FROM SubscrServicePrice p WHERE p.packId = :packId " + " AND :priceDate >= p.priceBeginDate "
			+ " AND (p.priceEndDate IS NULL OR :priceDate <= p.priceEndDate) ORDER BY p.id DESC")
	public List<SubscrServicePrice> selectPackPriceByDate(@Param("packId") Long packId,
			@Param("priceDate") Date priceDate);

	/**
	 * 
	 * @param itemId
	 * @param priceDate
	 * @return
	 */
	@Query("SELECT p FROM SubscrServicePrice p WHERE p.itemId = :itemId " + " AND :priceDate >= p.priceBeginDate "
			+ " AND (p.priceEndDate IS NULL OR :priceDate <= p.priceEndDate) ORDER BY p.id DESC")
	public List<SubscrServicePrice> selectItemPriceByDate(@Param("itemId") Long itemId,
			@Param("priceDate") Date priceDate);

	public List<SubscrServicePrice> findByItemId(Long itemId);
}
