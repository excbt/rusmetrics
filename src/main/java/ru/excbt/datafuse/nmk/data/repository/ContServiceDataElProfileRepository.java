package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataElProfile;

/**
 * Repository для ContServiceDataElProfile
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 12.15.2015
 *
 */
public interface ContServiceDataElProfileRepository extends Repository<ContServiceDataElProfile, Long> {
	/**
	 * 
	 * @param contZPointId
	 * @param timeDetailType
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElProfile d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate >= :beginDate " + " AND d.dataDate <= :endDate "
			+ " AND time_detail_type = :timeDetailType AND d.deleted = 0 ORDER BY d.dataDate ")
	public List<ContServiceDataElProfile> selectByZPoint(@Param("contZPointId") Long contZPointId,
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
	@Query("SELECT d FROM ContServiceDataElProfile d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate >= :beginDate " + " AND d.dataDate <= :endDate "
			+ " AND time_detail_type = :timeDetailType AND d.deleted = 0 ")
	public Page<ContServiceDataElProfile> selectByZPoint(@Param("contZPointId") Long contZPointId,
			@Param("timeDetailType") String timeDetailType, @Param("beginDate") Date beginDate,
			@Param("endDate") Date endDate, Pageable pageable);

	/**
	 * @param contZPointId
	 * @param timeDetailType
	 * @param dataDate
	 * @param pageable
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElProfile d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate >= :dataDate "
			+ " AND time_detail_type IN (:timeDetailType) AND d.deleted = 0 " + " ORDER BY d.dataDate ASC ")
	public List<ContServiceDataElProfile> selectFirstDataByZPoint(@Param("contZPointId") Long contZPointId,
			@Param("timeDetailType") String[] timeDetailType, @Param("dataDate") Date dataDate, Pageable pageable);

	/**
	 * 
	 * @param contZPointId
	 * @param timeDetailType
	 * @param dataDate
	 * @param pageable
	 * @return
	 */
	@Query("SELECT d FROM ContServiceDataElProfile d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate <= :dataDate "
			+ " AND time_detail_type IN (:timeDetailType) AND d.deleted = 0 " + " ORDER BY d.dataDate DESC ")
	public List<ContServiceDataElProfile> selectLastDataByZPoint(@Param("contZPointId") Long contZPointId,
			@Param("timeDetailType") String[] timeDetailType, @Param("dataDate") Date dataDate, Pageable pageable);

}
