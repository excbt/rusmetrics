package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;

public interface SubscrObjectTreeRepository extends CrudRepository<SubscrObjectTree, Long> {

	/**
	 * 
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT t FROM SubscrObjectTree t WHERE t.rmaSubscriberId = :rmaSubscriberId AND t.parentId IS NULL "
			+ " ORDER BY t.objectTreeType, t.objectName ")
	List<SubscrObjectTree> selectSubscrObjectTree(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 * 
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT t.id, t.objectTreeType, t.objectName FROM SubscrObjectTree t "
			+ " WHERE t.rmaSubscriberId = :rmaSubscriberId AND t.parentId IS NULL AND t.deleted = 0 "
			+ " ORDER BY t.objectTreeType, t.objectName ")
	List<Object[]> selectSubscrObjectTreeShort(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 * 
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Query("SELECT t.rmaSubscriberId FROM SubscrObjectTree t WHERE t.id = :subscrObjectTreeId")
	public List<Long> selectRmaSubscriberIds(@Param("subscrObjectTreeId") Long subscrObjectTreeId);

	/**
	 * 
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Query("SELECT t.isLinkDeny FROM SubscrObjectTree t WHERE t.id = :subscrObjectTreeId")
	public List<Boolean> selectIsLinkDeny(@Param("subscrObjectTreeId") Long subscrObjectTreeId);

}
