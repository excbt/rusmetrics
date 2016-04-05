package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContObjectNodeTree;

public interface ContObjectNodeTreeRepository extends CrudRepository<ContObjectNodeTree, Long> {

	@Query("SELECT nt FROM ContObjectNodeTree nt WHERE nt.contObjectId = :contObjectId")
	List<ContObjectNodeTree> selectByContObject(@Param("contObjectId") Long contObjectId);

}
