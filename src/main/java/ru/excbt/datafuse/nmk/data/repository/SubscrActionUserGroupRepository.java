package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrActionGroup;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUser;
import ru.excbt.datafuse.nmk.data.model.SubscrActionUserGroup;

public interface SubscrActionUserGroupRepository extends
		CrudRepository<SubscrActionUserGroup, Long> {

	/**
	 * 
	 * @param subscriberId
	 * @param subscrActionGroupId
	 * @return
	 */
	@Query("SELECT ug.subscrActionUser FROM SubscrActionUserGroup ug "
			+ " WHERE ug.subscrActionGroup.id = :subscrActionGroupId")
	public List<SubscrActionUser> selectUsersByGroup(
			@Param("subscrActionGroupId") long subscrActionGroupId);

	/**
	 * 
	 * @param subscriberId
	 * @param subscrActionGroupId
	 * @return
	 */
	@Query("SELECT ug.subscrActionUser.id FROM SubscrActionUserGroup ug "
			+ " WHERE ug.subscrActionGroup.id = :subscrActionGroupId")
	public List<Long> selectUserIdsByGroup(
			@Param("subscrActionGroupId") long subscrActionGroupId);

	/**
	 * 
	 * @param subscriberId
	 * @param subscrActionUserId
	 * @return
	 */
	@Query("SELECT ug.subscrActionGroup FROM SubscrActionUserGroup ug "
			+ " WHERE ug.subscrActionUser.id = :subscrActionUserId")
	public List<SubscrActionGroup> selectGroupsByUser(
			@Param("subscrActionUserId") long subscrActionUserId);

	/**
	 * 
	 * @param subscriberId
	 * @param subscrActionUserId
	 * @return
	 */
	@Query("SELECT ug.subscrActionGroup.id FROM SubscrActionUserGroup ug "
			+ " WHERE ug.subscrActionUser.id = :subscrActionUserId")
	public List<Long> selectGroupIdsByUser(
			@Param("subscrActionUserId") long subscrActionUserId);

	/**
	 * 
	 * @param subscriberId
	 * @param subscrActionUserId
	 * @return
	 */
	@Query("SELECT ug.id FROM SubscrActionUserGroup ug "
			+ " WHERE ug.subscrActionUser.id = :subscrActionUserId AND "
			+ " ug.subscrActionGroup.id = :subscrActionGroupId")
	public List<Long> selectSubscrActionUserGroupIds(
			@Param("subscrActionUserId") long subscrActionUserId,
			@Param("subscrActionGroupId") long subscrActionGroupId);

}
