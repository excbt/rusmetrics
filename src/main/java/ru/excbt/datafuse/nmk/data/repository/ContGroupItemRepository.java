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
	public List<ContObject> selectContGroupObjects(
			@Param("contGroupId") long contGroupId);

	@Query("SELECT co FROM Subscriber s LEFT JOIN s.contObjects co "
			+ "WHERE s.id = :subscriberId AND co.id NOT IN "
			+ "( SELECT ci.contObject.id FROM ContGroupItem ci LEFT JOIN ci.contGroup cg "
			+ "WHERE cg.id = :contGroupId ) " + "ORDER BY co.name, co.id")
	public List<ContObject> selectAvailableContGroupObjects(
			@Param("contGroupId") long contGroupId,
			@Param("subscriberId") long subscriberId);

	@Query("SELECT ci.contObjectId FROM ContGroupItem ci "
			+ "WHERE ci.contGroup.id = :contGroupId ")
	public List<Long> selectObjectIds(@Param("contGroupId") long contGroupId);

	@Query("SELECT ci.id FROM ContGroupItem ci "
			+ "WHERE ci.contGroup.id = :contGroupId AND ci.contObjectId = :objectId ")
	public List<Long> selectItemIds(@Param("contGroupId") long contGroupId,
			@Param("objectId") long objectId);

	@Query("SELECT ci.id FROM ContGroupItem ci "
			+ "WHERE ci.contGroup.id = :contGroupId ")
	public List<Long> selectItemIds(@Param("contGroupId") long contGroupId);

}