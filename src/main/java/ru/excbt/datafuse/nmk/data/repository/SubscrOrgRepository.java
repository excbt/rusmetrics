package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.SubscrOrg;

public interface SubscrOrgRepository extends CrudRepository<SubscrOrg, Long> {

	@Query("SELECT so FROM SubscrOrg so WHERE so.organization.id = :id")
	public List<SubscrOrg> selectByOrganizationId(@Param("id") long id);
	
	@Query("SELECT so FROM SubscrUser su INNER JOIN su.subscrOrgs so WHERE su.id = :subscrUserId")
	public List<SubscrOrg> selectByUserId(@Param("subscrUserId") long subscrUserId);
	
}
