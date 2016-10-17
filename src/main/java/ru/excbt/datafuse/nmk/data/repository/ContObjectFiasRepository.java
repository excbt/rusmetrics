package ru.excbt.datafuse.nmk.data.repository;

import ru.excbt.datafuse.nmk.data.model.ContObjectFias;
import ru.excbt.datafuse.nmk.data.repository.keyname.ContObjectIdModelRepository;

/**
 * Repository для ContObjectFias
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 20.07.2015
 *
 */
public interface ContObjectFiasRepository extends ContObjectIdModelRepository<ContObjectFias> {

	//public List<ContObjectFias> findByContObjectId(Long contObjectId);

	/**
	 * 
	 * @param contObjectIds
	 * @return
	 */
	//	@Query("SELECT f FROM #{#entityName} f WHERE f.contObjectId in (:contObjectIds)")
	//	public List<ContObjectFias> selectByContObjectIds(@Param("contObjectIds") List<Long> contObjectIds);
}
