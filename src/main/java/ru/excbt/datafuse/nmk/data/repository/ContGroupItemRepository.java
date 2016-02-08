package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContGroupItem;
import ru.excbt.datafuse.nmk.data.model.ContObject;

/**
 * Repository для ContGroupItem
 * 
 * @author S. Kuzovoy
 * @version 1.0
 * @since 25.05.2015
 *
 */
public interface ContGroupItemRepository extends CrudRepository<ContGroupItem, Long> {

	@Query("SELECT sco.contObject FROM SubscrContObject sco WHERE sco.contObjectId IN "
			+ "( SELECT ci.contObject.id FROM ContGroupItem ci INNER JOIN ci.contGroup cg "
			+ "WHERE cg.id = :contGroupId ) AND" + " sco.subscriberId = :subscriberId AND sco.subscrEndDate IS NULL"
			+ " ORDER BY sco.contObject.name, sco.contObject.id ")
	public List<ContObject> selectContGroupObjects(@Param("contGroupId") long contGroupId,
			@Param("subscriberId") long subscriberId);

	@Query("SELECT sco.contObject FROM SubscrContObject sco "
			+ "WHERE sco.subscriberId = :subscriberId AND sco.contObjectId NOT IN "
			+ "( SELECT ci.contObject.id FROM ContGroupItem ci LEFT JOIN ci.contGroup cg "
			+ "WHERE cg.id = :contGroupId ) " + " ORDER BY sco.contObject.name, sco.contObject.id ")
	public List<ContObject> selectAvailableContGroupObjects(@Param("contGroupId") long contGroupId,
			@Param("subscriberId") long subscriberId);

	@Query("SELECT ci.contObjectId FROM ContGroupItem ci " + "WHERE ci.contGroup.id = :contGroupId ")
	public List<Long> selectObjectIds(@Param("contGroupId") long contGroupId);

	@Query("SELECT ci.id FROM ContGroupItem ci "
			+ "WHERE ci.contGroup.id = :contGroupId AND ci.contObjectId = :objectId ")
	public List<Long> selectItemIds(@Param("contGroupId") long contGroupId, @Param("objectId") long objectId);

	@Query("SELECT ci.id FROM ContGroupItem ci " + "WHERE ci.contGroup.id = :contGroupId ")
	public List<Long> selectItemIds(@Param("contGroupId") long contGroupId);

}
