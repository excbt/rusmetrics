/**
 * 
 */
package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 * 
 */
public interface ContObjectGeoPosRepository extends CrudRepository<ContObjectGeoPos, Long> {

	/**
	 * 
	 * @param contObjectIds
	 * @return
	 */
	@Query("SELECT p FROM ContObjectGeoPos p WHERE p.contObjectId in (:contObjectIds)")
	public List<ContObjectGeoPos> selectByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);

}
