package ru.excbt.datafuse.nmk.data.repository;

import ru.excbt.datafuse.nmk.data.model.ContObject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.repository.support.ContObjectMeterPeriod;

import javax.persistence.Tuple;

import java.util.List;
import java.util.Optional;

/**
 * Repository для ContObject
 *
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 25.02.2015
 *
 */
public interface ContObjectRepository extends JpaRepository<ContObject, Long> {

	List<ContObject> findByFullNameLikeIgnoreCase(String str);

	//@Query("select s from Store s join s.map m where ?1 in (VALUE(m))"
	@Query("SELECT co.id as id, KEY(m) as key, VALUE(m) as meterPeriodSetting FROM ContObject co JOIN co.meterPeriodSettings m WHERE co.id in (:contObjectIds)")
	List<ContObjectMeterPeriod> findMeterPeriodSettings(@Param("contObjectIds") List<Long> contObjectIds);

}
