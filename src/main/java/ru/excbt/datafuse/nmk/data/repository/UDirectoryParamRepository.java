package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.UDirectoryParam;

/**
 * Repository для UDirectoryParam
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.03.2015
 *
 */
public interface UDirectoryParamRepository extends CrudRepository<UDirectoryParam, Long> {

	@Query("SELECT p FROM UDirectoryParam p INNER JOIN p.directory d WHERE d.id = :id")
	public List<UDirectoryParam> selectDirectoryParams(@Param("id") long directoryId);
}
