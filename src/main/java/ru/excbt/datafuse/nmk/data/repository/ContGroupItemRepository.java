package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContGroupItem;
import ru.excbt.datafuse.nmk.data.model.ContObject;

public interface ContGroupItemRepository extends
		CrudRepository<ContGroupItem, Long> {

	@Query("SELECT co FROM ContObject co WHERE co.id IN "
			+ "( SELECT ci.contObject.id FROM ContGroupItem ci INNER JOIN ci.contGroup cg "
			+ "WHERE cg.id = :contGroupId ) " + "ORDER BY co.name, co.id")
	public List<ContObject> selectContObjects(
			@Param("contGroupId") long contGroupId);

	@Query("SELECT co FROM Subscriber s LEFT JOIN s.contObjects co "
			+ "WHERE s.id = :subscriberId AND co.id NOT IN "
			+ "( SELECT ci.contObject.id FROM ContGroupItem ci LEFT JOIN ci.contGroup cg "
			+ "WHERE cg.id = :contGroupId ) " + "ORDER BY co.name, co.id")
	public List<ContObject> selectAvailableContObjects(
			@Param("contGroupId") long contGroupId,
			@Param("subscriberId") long subscriberId);

}
