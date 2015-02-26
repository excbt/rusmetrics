package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.SubscrUser;

public interface SubscrUserRepository extends CrudRepository<SubscrUser, Long> {

	public List<SubscrUser> findByUserNameIgnoreCase(String userName);
	
	@Query("SELECT co FROM SubscrUser su INNER JOIN su.subscrOrgs so INNER JOIN so.contObjects co WHERE su.id = :id")
	public List<ContObject> findContObjects(@Param("id")long userId);
	
}
