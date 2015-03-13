package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;

public interface UDirectoryNodeRepository extends CrudRepository<UDirectoryNode, Long> {
//	@Query("SELECT d FROM UDirectory d WHERE d.parentId is null")
//	public List<UDirectoryNode> selectAll();

	
}
