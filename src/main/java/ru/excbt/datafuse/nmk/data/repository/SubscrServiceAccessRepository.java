package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrServiceAccess;
import ru.excbt.datafuse.nmk.data.model.keyname.SubscrServicePermission;

/**
 * Repository для SubscrServiceAccess
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.09.2015
 *
 */
public interface SubscrServiceAccessRepository extends CrudRepository<SubscrServiceAccess, Long> {

	List<SubscrServiceAccess> findBySubscriberIdAndPackId(Long subscriberId, Long packId);

	/**
	 * 
	 * @param subscriberId
	 * @param packId
	 * @return
	 */
	@Query(value = "SELECT sa FROM SubscrServiceAccess sa WHERE sa.subscriberId = :subscriberId "
			+ " AND :accessDate >= accessStartDate AND (accessEndDate IS NULL OR :accessDate <= accessEndDate) "
			+ " AND :packId = sa.packId ORDER BY packId NULLS FIRST")
	List<SubscrServiceAccess> selectBySubscriberIdAndPackId(@Param("subscriberId") Long subscriberId,
			@Param("packId") Long packId, @Param("accessDate") Date accessDate);

	/**
	 * 
	 * @param subscriberId
	 * @param accessDate
	 * @return
	 */
	@Query(value = "SELECT sa FROM SubscrServiceAccess sa WHERE sa.subscriberId = :subscriberId "
			+ " AND :accessDate >= accessStartDate AND (accessEndDate IS NULL OR :accessDate <= accessEndDate) "
			+ " ORDER BY sa.accessStartDate DESC, sa.id DESC")
	List<SubscrServiceAccess> selectBySubscriberId(@Param("subscriberId") Long subscriberId,
			@Param("accessDate") Date accessDate);

	@Query(value = "SELECT sa FROM SubscrServiceAccess sa WHERE sa.subscriberId = :subscriberId "
			+ " ORDER BY sa.accessStartDate DESC, sa.id DESC")
	List<SubscrServiceAccess> selectBySubscriberId(@Param("subscriberId") Long subscriberId);

	/**
	 * 
	 * @param subscriberId
	 * @param accessDate
	 * @return
	 */
	@Query("SELECT DISTINCT i.servicePermissions FROM SubscrServiceItem i "
			+ " WHERE i.id IN (SELECT sa.itemId FROM SubscrServiceAccess sa "
			+ " WHERE sa.subscriberId = :subscriberId AND "
			+ " :accessDate >= accessStartDate AND accessEndDate IS NULL )")
	public List<SubscrServicePermission> selectSubscriberPermissions(@Param("subscriberId") Long subscriberId,
			@Param("accessDate") Date accessDate);

}
