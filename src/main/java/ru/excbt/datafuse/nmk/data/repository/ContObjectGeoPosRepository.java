/**
 * 
 */
package ru.excbt.datafuse.nmk.data.repository;

import ru.excbt.datafuse.nmk.data.model.v.ContObjectGeoPos;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContObjectIdModelRepository;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.10.2016
 * 
 */
public interface ContObjectGeoPosRepository extends ContObjectIdModelRepository<ContObjectGeoPos> {

	/**
	 * 
	 * @param contObjectIds
	 * @return
	 */
	//	@Query("SELECT p FROM #{#entityName} p WHERE p.contObjectId in (:contObjectIds)")
	//	public List<ContObjectGeoPos> selectByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);

}
