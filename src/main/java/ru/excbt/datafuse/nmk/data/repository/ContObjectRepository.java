package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObject;

public interface ContObjectRepository extends JpaRepository<ContObject, Long> {

	@Query("SELECT co FROM Subscriber s INNER JOIN s.contObjects co WHERE s.id = :id")
	public List<ContObject> selectSubscrContObjects(@Param("id")long subscriberId);
	
	public List<ContObject> findByFullNameLikeIgnoreCase(String str);
}
