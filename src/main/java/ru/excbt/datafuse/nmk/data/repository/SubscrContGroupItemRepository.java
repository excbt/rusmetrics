package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrContGroupItem;

/**
 * Repository для ContGroupItem
 * 
 * @author S. Kuzovoy
 * @version 1.0
 * @since 25.05.2015
 *
 */
public interface SubscrContGroupItemRepository extends CrudRepository<SubscrContGroupItem, Long> {

	/**
	 * 
	 * @param subscriberId
	 * @param contGroupId
	 * @return
	 */
	@Query("SELECT sco.contObject FROM SubscrContObject sco WHERE sco.contObjectId IN "
			+ "( SELECT ci.contObject.id FROM SubscrContGroupItem ci INNER JOIN ci.contGroup cg "
			+ "WHERE cg.id = :contGroupId ) AND" + " sco.subscriberId = :subscriberId AND sco.subscrEndDate IS NULL"
			+ " ORDER BY sco.contObject.name, sco.contObject.id ")
	public List<ContObject> selectContGroupObjects(@Param("subscriberId") long subscriberId,
			@Param("contGroupId") long contGroupId);

	/**
	 * 
	 * @param subscriberId
	 * @param contGroupId
	 * @return
	 */
	@Query("SELECT sco.contObject FROM SubscrContObject sco "
			+ "WHERE sco.subscriberId = :subscriberId AND sco.contObjectId NOT IN "
			+ "( SELECT ci.contObject.id FROM SubscrContGroupItem ci LEFT JOIN ci.contGroup cg "
			+ "WHERE cg.id = :contGroupId ) " + " ORDER BY sco.contObject.name, sco.contObject.id ")
	public List<ContObject> selectAvailableContGroupObjects(@Param("subscriberId") long subscriberId,
			@Param("contGroupId") long contGroupId);

	/**
	 * 
	 * @param contGroupId
	 * @return
	 */
	@Query("SELECT ci.contObjectId FROM SubscrContGroupItem ci " + "WHERE ci.contGroup.id = :contGroupId ")
	public List<Long> selectObjectIds(@Param("contGroupId") long contGroupId);

	/**
	 * 
	 * @param contGroupId
	 * @param objectId
	 * @return
	 */
	@Query("SELECT ci.id FROM SubscrContGroupItem ci "
			+ "WHERE ci.contGroup.id = :contGroupId AND ci.contObjectId = :objectId ")
	public List<Long> selectItemIds(@Param("contGroupId") long contGroupId, @Param("objectId") long objectId);

	/**
	 * 
	 * @param contGroupId
	 * @return
	 */
	@Query("SELECT ci.id FROM SubscrContGroupItem ci " + "WHERE ci.contGroup.id = :contGroupId ")
	public List<Long> selectItemIds(@Param("contGroupId") long contGroupId);

}
