package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;

public interface ContServiceDataHWaterRepository extends
		CrudRepository<ContServiceDataHWater, Long> {

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
			+ " WHERE d.contZPoint.id = :contZPointId "
			+ " ORDER BY d.dataDate desc")
	public List<ContServiceDataHWater> selectLastDetailByZPoint(
			@Param("contZPointId") long contZPointId, Pageable pageable);
	
	
}
