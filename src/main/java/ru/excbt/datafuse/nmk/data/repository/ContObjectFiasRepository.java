package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObjectFias;

/**
 * Repository для ContObjectFias
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.07.2015
 *
 */
public interface ContObjectFiasRepository extends CrudRepository<ContObjectFias, Long> {

	public List<ContObjectFias> findByContObjectId(Long contObjectId);

	/**
	 * 
	 * @param contObjectIds
	 * @return
	 */
	@Query("SELECT f FROM ContObjectFias f WHERE f.contObjectId in (:contObjectIds)")
	public List<ContObjectFias> selectByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);
}
