package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataElTech;

/**
 * Repository для ContServiceDataElTech
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 15.12.2015
 *
 */
public interface ContServiceDataElTechRepository extends Repository<ContServiceDataElTech, Long> {

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetailType
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElTech d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate >= :beginDate " + " AND d.dataDate <= :endDate "
			+ " AND time_detail_type = :timeDetailType AND d.deleted = 0 ORDER BY d.dataDate ")
	public List<ContServiceDataElTech> selectByZPoint(@Param("contZPointId") Long contZPointId,
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
	@Query("SELECT d FROM ContServiceDataElTech d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate >= :beginDate " + " AND d.dataDate <= :endDate "
			+ " AND time_detail_type = :timeDetailType AND d.deleted = 0 ")
	public Page<ContServiceDataElTech> selectByZPoint(@Param("contZPointId") Long contZPointId,
			@Param("timeDetailType") String timeDetailType, @Param("beginDate") Date beginDate,
			@Param("endDate") Date endDate, Pageable pageable);

	/**
	 * @param contZPointId
	 * @param timeDetailType
	 * @param dataDate
	 * @param pageable
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElTech d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate >= :dataDate "
			+ " AND time_detail_type IN (:timeDetailType) AND d.deleted = 0 " + " ORDER BY d.dataDate ASC ")
	public List<ContServiceDataElTech> selectFirstDataByZPoint(@Param("contZPointId") long contZPointId,
			@Param("timeDetailType") String[] timeDetailType, @Param("dataDate") Date dataDate, Pageable pageable);

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetailType
	 * @param dataDate
	 * @param pageable
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElTech d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate <= :dataDate "
			+ " AND time_detail_type IN (:timeDetailType) AND d.deleted = 0 " + " ORDER BY d.dataDate DESC ")
	public List<ContServiceDataElTech> selectLastDataByZPoint(@Param("contZPointId") long contZPointId,
			@Param("timeDetailType") String[] timeDetailType, @Param("dataDate") Date dataDate, Pageable pageable);

}
