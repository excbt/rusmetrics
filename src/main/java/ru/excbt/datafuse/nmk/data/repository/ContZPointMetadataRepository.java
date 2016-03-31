package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.ContZPointMetadata;

public interface ContZPointMetadataRepository extends CrudRepository<ContZPointMetadata, Long> {

	/**
	 * 
	 * @param contZPointId
	 * @param deviceObjectId
	 * @return
	 */
	@Query("SELECT FROM ContZPointMetadata m WHERE m.contZPointId = :contZPointId AND m.deviceObjectId = :deviceObjectId")
	public List<ContZPointMetadata> selectZOntZPointMetadata(@Param("contZPointId") Long contZPointId,
			@Param("deviceObjectId") Long deviceObjectId);
}
