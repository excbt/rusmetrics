package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.NodeDirectoryParam;

public interface NodeDirectoryParamRepository extends CrudRepository<NodeDirectoryParam, Long> {

	@Query("SELECT p FROM NodeDirectoryParam p WHERE p.directory.id = :id")
	public List<?> selectNodeDirectoryParams(@Param("id")long directoryId);
}
