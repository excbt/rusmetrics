package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrRole;

public interface SubscrRoleRepository extends CrudRepository<SubscrRole, Long> {

	@Query("SELECT so FROM SubscrRole so WHERE so.organization.id = :id")
	public List<SubscrRole> selectByOrganizationId(@Param("id") long id);
	
	@Query("SELECT so FROM SubscrUser su INNER JOIN su.subscrRoles so WHERE su.id = :subscrUserId")
	public List<SubscrRole> selectByUserId(@Param("subscrUserId") long subscrUserId);
	
}
