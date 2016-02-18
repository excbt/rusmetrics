package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.UDirectory;

/**
 * Repository для UDirectory
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 13.03.2015
 *
 */
public interface UDirectoryRepository extends CrudRepository<UDirectory, Long> {

	@Query("SELECT d FROM UDirectory d INNER JOIN d.subscriber s WHERE s.id = :id")
	public List<UDirectory> selectBySubscriber(@Param("id") long id);

	@Query("SELECT d.id FROM UDirectory d INNER JOIN d.subscriber s WHERE s.id = :subscriberId")
	public List<Long> selectDirectoryIds(@Param("subscriberId") long subscriberId);

	@Query("SELECT d.id FROM UDirectory d INNER JOIN d.subscriber s WHERE s.id = :subscriber and d.id = :directoryId")
	public List<Long> selectAvailableId(@Param("subscriber") long subscriber, @Param("directoryId") long directoryId);

}
