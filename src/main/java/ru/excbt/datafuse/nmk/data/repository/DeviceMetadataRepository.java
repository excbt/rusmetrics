package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;

public interface DeviceMetadataRepository extends CrudRepository<DeviceMetadata, Long> {

	@Query("SELECT dm FROM DeviceMetadata dm WHERE dm.deviceModelId = :deviceModelId "
			+ " AND dm.deviceMetadataType = :deviceMetadataType ORDER BY dm.metaOrder ASC")
	public List<DeviceMetadata> selectDeviceMetadata(@Param("deviceModelId") Long deviceModelId,
			@Param("deviceMetadataType") String deviceMetadataType);

}
