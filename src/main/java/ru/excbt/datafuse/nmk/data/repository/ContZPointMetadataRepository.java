package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
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
	@Query("SELECT m FROM ContZPointMetadata m WHERE m.contZPointId = :contZPointId AND m.deviceObjectId = :deviceObjectId")
	public List<ContZPointMetadata> selectContZPointMetadata(@Param("contZPointId") Long contZPointId,
			@Param("deviceObjectId") Long deviceObjectId);

	@Modifying
	@Query("delete ContZPointMetadata m where m.contZPointId = :contZPointId AND m.deviceObjectId <> :deviceObjectId")
	public int deleteOtherContZPointMetadata(@Param("contZPointId") Long contZPointId,
			@Param("deviceObjectId") Long deviceObjectId);

}
