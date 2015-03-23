package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;

public interface ContServiceDataHWaterRepository extends
		CrudRepository<ContServiceDataHWater, Long> {

	@Query("SELECT d FROM ContServiceDataHWater d WHERE d.contZPoint.id = :contZPointId "
			+ "ORDER BY d.dataDate")
	public List<ContServiceDataHWater> selectByZPoint(
			@Param("contZPointId") long contZPointId);

	@Query("SELECT d FROM ContServiceDataHWater d "
			+ "WHERE d.contZPoint.id = :contZPointId and d.dataDate >= :beginDate and d.dataDate <= :endDate "
			+ "ORDER BY d.dataDate")
	public List<ContServiceDataHWater> selectByZPoint(
			@Param("contZPointId") long contZPointId,
			@Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

}
