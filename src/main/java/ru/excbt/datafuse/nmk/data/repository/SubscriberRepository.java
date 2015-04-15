package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.Subscriber;

public interface SubscriberRepository extends CrudRepository<Subscriber, Long> {

	@Query("SELECT s FROM Subscriber s WHERE s.organization.id = :id")
	public List<Subscriber> selectByOrganizationId(@Param("id") long id);

	@Query("SELECT s FROM SubscrUser su INNER JOIN su.subscriber s WHERE su.id = :subscrUserId")
	public List<Subscriber> selectByUserId(
			@Param("subscrUserId") long subscrUserId);

	@Query("SELECT org FROM Subscriber s INNER JOIN s.rsoOrganizations org WHERE s.id = :subscriberId")
	public Iterable<Organization> selectRsoOrganizations(
			@Param("subscriberId") long subscriberId);

	@Query("SELECT co FROM Subscriber s INNER JOIN s.contObjects co WHERE s.id = :id")
	public List<ContObject> selectContObjects(@Param("id") long subscriberId);

	@Query("SELECT co.id FROM Subscriber s INNER JOIN s.contObjects co "
			+ "WHERE s.id = :id AND co.id = :contObjectId")
	public List<Long> selectContObjectId(@Param("id") long subscriberId,
			@Param("contObjectId") long contObjectId);

}
