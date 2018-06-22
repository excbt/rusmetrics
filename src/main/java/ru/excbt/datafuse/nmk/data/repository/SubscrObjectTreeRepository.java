package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrObjectTree;

public interface SubscrObjectTreeRepository extends JpaRepository<SubscrObjectTree, Long>, QuerydslPredicateExecutor<SubscrObjectTree> {

	/**
	 *
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT t FROM SubscrObjectTree t WHERE t.rmaSubscriberId = :rmaSubscriberId AND t.parentId IS NULL "
			+ " ORDER BY t.objectTreeType, t.objectName ")
	List<SubscrObjectTree> selectRmaSubscrObjectTree(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 *
	 * @param rmaSubscriberId
	 * @return
	 */
	@Query("SELECT t.id, t.subscriberId, t.rmaSubscriberId, t.objectTreeType, t.objectName FROM SubscrObjectTree t "
			+ " WHERE t.rmaSubscriberId = :rmaSubscriberId AND t.parentId IS NULL AND t.deleted = 0 "
			+ " ORDER BY t.objectTreeType, t.objectName ")
	List<Object[]> selectRmaSubscrObjectTreeShort(@Param("rmaSubscriberId") Long rmaSubscriberId);

	@Query("SELECT t.id, t.subscriberId, t.rmaSubscriberId, t.objectTreeType, t.objectName, t.isActive FROM SubscrObjectTree t "
			+ " WHERE (t.rmaSubscriberId = :rmaSubscriberId or t.subscriberId = :rmaSubscriberId )AND t.parentId IS NULL AND t.deleted = 0 "
			+ " ORDER BY t.objectTreeType, t.objectName ")
	List<Object[]> selectRmaSubscrObjectTreeShort2(@Param("rmaSubscriberId") Long rmaSubscriberId);

	/**
	 *
	 * @param subscriberId
	 * @return
	 */
	@Query("SELECT t.id, t.subscriberId, t.rmaSubscriberId, t.objectTreeType, t.objectName, t.isActive FROM SubscrObjectTree t "
			+ " WHERE t.subscriberId = :subscriberId AND t.parentId IS NULL AND t.deleted = 0 "
			+ " ORDER BY t.objectTreeType, t.objectName ")
	List<Object[]> selectSubscrObjectTreeShort(@Param("subscriberId") Long subscriberId);

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
	@Query("SELECT t.subscriberId FROM SubscrObjectTree t WHERE t.id = :subscrObjectTreeId")
	public List<Long> selectSubscriberIds(@Param("subscrObjectTreeId") Long subscrObjectTreeId);

	/**
	 *
	 * @param subscrObjectTreeId
	 * @return
	 */
	@Query("SELECT t.isLinkDeny FROM SubscrObjectTree t WHERE t.id = :subscrObjectTreeId")
	public List<Boolean> selectIsLinkDeny(@Param("subscrObjectTreeId") Long subscrObjectTreeId);

}
