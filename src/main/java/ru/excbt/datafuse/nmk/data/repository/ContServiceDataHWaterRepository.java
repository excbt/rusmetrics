package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;

public interface ContServiceDataHWaterRepository extends
		PagingAndSortingRepository<ContServiceDataHWater, Long> {

	@Query("SELECT d FROM ContServiceDataHWater d "
			+ " WHERE d.contZPoint.id = :contZPointId AND time_detail_type = :timeDetailType "
			+ " ORDER BY d.dataDate ")
	public List<ContServiceDataHWater> selectByZPoint(
			@Param("contZPointId") long contZPointId,
			@Param("timeDetailType") String timeDetailType);

	@Query("SELECT d FROM ContServiceDataHWater d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate >= :beginDate "
			+ " AND d.dataDate <= :endDate "
			+ " AND time_detail_type = :timeDetailType ORDER BY d.dataDate ")
	public List<ContServiceDataHWater> selectByZPoint(
			@Param("contZPointId") long contZPointId,
			@Param("timeDetailType") String timeDetailType,
			@Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

	@Query("SELECT d FROM ContServiceDataHWater d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate >= :beginDate "
			+ " AND d.dataDate <= :endDate "
			+ " AND time_detail_type = :timeDetailType ORDER BY d.dataDate desc ")
	public Page<ContServiceDataHWater> selectByZPoint(
			@Param("contZPointId") long contZPointId,
			@Param("timeDetailType") String timeDetailType,
			@Param("beginDate") Date beginDate, @Param("endDate") Date endDate,
			Pageable pageable);

	@Query("SELECT d FROM ContServiceDataHWater d "
			+ " WHERE d.contZPointId = :contZPointId "
			+ " ORDER BY d.dataDate desc")
	public List<ContServiceDataHWater> selectLastDataByZPoint(
			@Param("contZPointId") long contZPointId, Pageable pageable);

	@Query("SELECT d FROM ContServiceDataHWater d "
			+ " WHERE d.contZPointId = :contZPointId ")
	public List<ContServiceDataHWater> selectAnyDataByZPoint(
			@Param("contZPointId") long contZPointId, Pageable pageable);

	@Query("SELECT 1 FROM ContServiceDataHWater d "
			+ " WHERE d.contZPointId = :contZPointId ")
	public List<Long> selectExistsAnyDataByZPoint(
			@Param("contZPointId") long contZPointId, Pageable pageable);
	
	@Query("SELECT d FROM ContServiceDataHWater d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate <= :dataDate "
			+ " AND time_detail_type = :timeDetailType "
			+ " ORDER BY d.dataDate desc ")
	public List<ContServiceDataHWater> selectLastDataByZPoint(
			@Param("contZPointId") long contZPointId,
			@Param("timeDetailType") String timeDetailType,
			@Param("dataDate") Date dataDate,
			Pageable pageable);

	@Query("SELECT d FROM ContServiceDataHWater d "
			+ " WHERE d.contZPoint.id = :contZPointId AND d.dataDate <= :dataDate "
			+ " AND time_detail_type IN (:timeDetailType) "
			+ " ORDER BY d.dataDate DESC ")
	public List<ContServiceDataHWater> selectLastDataByZPoint(
			@Param("contZPointId") long contZPointId,
			@Param("timeDetailType") String[] timeDetailType,
			@Param("dataDate") Date dataDate,
			Pageable pageable);
	
	
}
