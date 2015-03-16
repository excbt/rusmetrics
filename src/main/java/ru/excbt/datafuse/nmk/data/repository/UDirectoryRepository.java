package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.UDirectory;

public interface UDirectoryRepository extends CrudRepository<UDirectory, Long> {

	@Query("SELECT d FROM SubscrOrg so INNER JOIN so.directories d WHERE so.id = :id")
	public List<UDirectory> selectBySubscrOrg(@Param("id") long id);
	
	@Query("SELECT d.id FROM SubscrOrg so INNER JOIN so.directories d WHERE so.id = :subscrOrgId")
	public List<Long> selectDirectoryIds(@Param("subscrOrgId") long subscrOrgId);

	@Query("SELECT d.id FROM SubscrOrg so INNER JOIN so.directories d WHERE so.id = :subscrOrgId and d.id = :directoryId")
	public List<Long> selectAvailableId(@Param("subscrOrgId") long subscrOrgId, @Param("directoryId") long directoryId);

}
