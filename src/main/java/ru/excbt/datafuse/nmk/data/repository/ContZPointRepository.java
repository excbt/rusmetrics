package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;

public interface ContZPointRepository extends CrudRepository<ContZPoint, Long> {

	public List<ContZPoint> findByContObjectId(long contObjectId);

	public List<ContZPoint> findByIdAndContObject(long contZPointId,
			long contObjectId);

	@Query("SELECT zp.id FROM ContZPoint zp WHERE zp.contObject.id = :contObjectId ")
	public List<Long> selectContZPointIds(
			@Param("contObjectId") long contObjectId);

}
