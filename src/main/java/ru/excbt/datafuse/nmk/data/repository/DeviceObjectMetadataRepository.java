package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetadata;

/**
 * Repository для DeviceObjectMetadata
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 22.01.2015
 *
 */
public interface DeviceObjectMetadataRepository extends CrudRepository<DeviceObjectMetadata, Long> {

	@Query("SELECT m FROM DeviceObjectMetadata m "
			+ " WHERE m.deviceObjectId = :deviceObjectId AND m.deviceMetadataType = :deviceMetadataType "
			+ " ORDER BY m.metaNumber NULLS FIRST, m.metaOrder ")
	public List<DeviceObjectMetadata> selectDeviceObjectMetadata(@Param("deviceObjectId") Long deviceObjectId,
			@Param("deviceMetadataType") String deviceMetadataType);

}
