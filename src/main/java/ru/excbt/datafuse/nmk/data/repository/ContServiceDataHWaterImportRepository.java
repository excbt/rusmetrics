/**
 * 
 */
package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWaterImport;

/**
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since dd.12.2016
 * 
 */
public interface ContServiceDataHWaterImportRepository extends CrudRepository<ContServiceDataHWaterImport, Long> {

	@Modifying
	@Procedure(name = "importData")
	public void processImport(@Param("par_session_uuid") String sessionUUID);

}
