package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataElCons;

/**
 * Repository для ContServiceDataElCons
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2015
 *
 */
public interface ContServiceDataElConsRepository extends CrudRepository<ContServiceDataElCons, Long> {

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetailType
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElCons d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate >= :beginDate " + " AND d.dataDate <= :endDate "
			+ " AND time_detail_type = :timeDetailType ORDER BY d.dataDate ")
	public List<ContServiceDataElCons> selectByZPoint(@Param("contZPointId") Long contZPointId,
			@Param("timeDetailType") String timeDetailType, @Param("beginDate") Date beginDate,
			@Param("endDate") Date endDate);

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetailType
	 * @param beginDate
	 * @param endDate
	 * @param pageable
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElCons d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate >= :beginDate " + " AND d.dataDate <= :endDate "
			+ " AND time_detail_type = :timeDetailType ")
	public Page<ContServiceDataElCons> selectByZPoint(@Param("contZPointId") Long contZPointId,
			@Param("timeDetailType") String timeDetailType, @Param("beginDate") Date beginDate,
			@Param("endDate") Date endDate, Pageable pageable);

	/**
	 * @param contZPointId
	 * @param timeDetailType
	 * @param dataDate
	 * @param pageable
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElCons d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate >= :dataDate "
			+ " AND time_detail_type IN (:timeDetailType) " + " ORDER BY d.dataDate ASC ")
	public List<ContServiceDataElCons> selectFirstDataByZPoint(@Param("contZPointId") long contZPointId,
			@Param("timeDetailType") String[] timeDetailType, @Param("dataDate") Date dataDate, Pageable pageable);

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetailType
	 * @param dataDate
	 * @param pageable
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElCons d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate <= :dataDate "
			+ " AND time_detail_type IN (:timeDetailType) " + " ORDER BY d.dataDate DESC ")
	public List<ContServiceDataElCons> selectLastDataByZPoint(@Param("contZPointId") long contZPointId,
			@Param("timeDetailType") String[] timeDetailType, @Param("dataDate") Date dataDate, Pageable pageable);

	/**
	 * 
	 * @param contZPointId
	 * @param pageable
	 * @return
	 */
	@Query("SELECT 1 FROM ContServiceDataElCons d " + " WHERE d.contZPointId = :contZPointId ")
	public List<Long> selectExistsAnyDataByZPoint(@Param("contZPointId") long contZPointId, Pageable pageable);

	/**
	 * No needed change order by.
	 * 
	 * @param contZPointId
	 * @param fromDateTime
	 * @param pageable
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElCons d " + " WHERE d.contZPointId = :contZPointId AND "
			+ " d.dataDate >= :fromDateTime" + " ORDER BY d.dataDate desc")
	public List<ContServiceDataElCons> selectLastDataByZPoint(@Param("contZPointId") long contZPointId,
			@Param("fromDateTime") Date fromDateTime, Pageable pageable);

	/**
	 * 
	 * @param contZPointId
	 * @param pageable
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElCons d " + " WHERE d.contZPointId = :contZPointId "
			+ " ORDER BY d.dataDate desc")
	public List<ContServiceDataElCons> selectLastDataByZPoint(@Param("contZPointId") long contZPointId,
			Pageable pageable);

}
