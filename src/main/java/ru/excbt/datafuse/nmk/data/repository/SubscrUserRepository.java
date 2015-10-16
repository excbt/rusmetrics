package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrRole;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;

public interface SubscrUserRepository extends CrudRepository<SubscrUser, Long> {

	public List<SubscrUser> findByUserNameIgnoreCase(String userName);

	@Query("SELECT u.subscrRoles FROM SubscrUser u WHERE u.id = :subscrUserId ")
	public List<SubscrRole> selectSubscrRoles(@Param("subscrUserId") long subscrUserId);

	public List<SubscrUser> findBySubscriberId(Long subscriberId);
}
