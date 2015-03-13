package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.UDirectory;

public interface UDirectoryRepository extends CrudRepository<UDirectory, Long> {

	@Query("SELECT d FROM SubscrOrg so INNER JOIN so.directories d WHERE so.id = :id")
	public List<UDirectory> selectBySubscrOrg(@Param("id") long id);
	
	@Query("SELECT d.id FROM SubscrOrg so INNER JOIN so.directories d WHERE so.id = :id")
	public List<Long> selectIdsBySubscrOrg(@Param("id") long id);

}
