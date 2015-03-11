package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.NodeDirectory;

public interface NodeDirectoryRepository extends CrudRepository<NodeDirectory, Long> {
	@Query("SELECT d FROM NodeDirectory d WHERE d.parentId is null")
	public List<NodeDirectory> selectAll();

	@Query("SELECT d FROM SubscrOrg so INNER JOIN so.nodeDirectories d WHERE so.id = :id")
	public List<NodeDirectory> selectBySubscrOrg(@Param("id") long id);
	
	@Query("SELECT d.id FROM SubscrOrg so INNER JOIN so.nodeDirectories d WHERE so.id = :id")
	public List<Long> selectIdsBySubscrOrg(@Param("id") long id);

	
}
