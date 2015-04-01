package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.ContZPoint;

public interface ContZPointRepository extends CrudRepository<ContZPoint, Long> {

	public List<ContZPoint> findByContObjectId(long contObjectId);

	public List<Long> findIdByIdAndContObject(long contZPointId,
			long contObjectId);

}
