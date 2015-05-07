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
			+ " WHERE ug.subscrActionGroup.subscriber.id = :subscriberId AND "
			+ " ug.subscrActionGroup.id = :subscrActionGroupId")
	public List<SubscrActionUser> selectGroupUsers(
			@Param("subscriberId") long subscriberId,
			@Param("subscrActionGroupId") long subscrActionGroupId);

	/**
	 * 
	 * @param subscriberId
	 * @param subscrActionUserId
	 * @return
	 */
	@Query("SELECT ug.subscrActionGroup FROM SubscrActionUserGroup ug "
			+ " WHERE ug.subscrActionUser.subscriber.id = :subscriberId AND "
			+ " ug.subscrActionUser.id = :subscrActionUserId")
	public List<SubscrActionGroup> selectUserGroups(
			@Param("subscriberId") long subscriberId,
			@Param("subscrActionUserId") long subscrActionUserId);


}
