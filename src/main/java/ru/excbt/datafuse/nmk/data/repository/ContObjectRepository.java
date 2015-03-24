package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;

public interface ContObjectRepository extends JpaRepository<ContObject, Long> {

	@Query("SELECT co FROM ContObject co WHERE co.id = :id")
	public List<ContObject> selectByUserName(@Param("id") long id);
	
	@Query("SELECT co FROM SubscrUser su INNER JOIN su.subscrRoles sr INNER JOIN sr.contObjects co WHERE su.id = :id")
	public List<ContObject> selectSubscrContObjects(@Param("id")long userId);
	

	public List<ContObject> findByFullNameLikeIgnoreCase(String str);
}
