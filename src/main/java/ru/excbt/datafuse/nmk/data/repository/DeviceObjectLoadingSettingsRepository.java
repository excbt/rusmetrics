package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectLoadingSettings;

public interface DeviceObjectLoadingSettingsRepository extends CrudRepository<DeviceObjectLoadingSettings, Long> {

	public List<DeviceObjectLoadingSettings> findByDeviceObjectId(Long deviceObjectId);

}
