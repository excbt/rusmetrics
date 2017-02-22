package ru.excbt.datafuse.nmk.data.repository;

import ru.excbt.datafuse.nmk.data.model.ContObject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.Tuple;

import java.util.List;

/**
 * Repository для ContObject
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.02.2015
 *
 */
public interface ContObjectRepository extends JpaRepository<ContObject, Long> {

	public List<ContObject> findByFullNameLikeIgnoreCase(String str);
	
	
	//@Query("select s from Store s join s.map m where ?1 in (VALUE(m))"
	@Query("SELECT co.id, KEY(m), VALUE(m) FROM ContObject co JOIN co.meterPeriodSettings m WHERE co.id in (:contObjectIds)")
	public List<Tuple> findMeterPeriodSettings(@Param("contObjectIds") List<Long> contObjectIds);

}
