package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.DeviceMetadata;

public interface DeviceMetadataRepository extends CrudRepository<DeviceMetadata, Long> {

	public List<DeviceMetadata> findByDeviceModelIdOrderByMetaOrderAsc(Long deviceModelId);
}
