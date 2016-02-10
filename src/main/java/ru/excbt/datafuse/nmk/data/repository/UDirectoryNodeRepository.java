package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.UDirectoryNode;

/**
 * Repository для UDirectoryNode
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.03.2015
 *
 */
public interface UDirectoryNodeRepository extends CrudRepository<UDirectoryNode, Long> {

}
