package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectLoadingSettings;

/**
 * Repository для DeviceObjectLoadingSettings
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.01.2015
 *
 */
public interface DeviceObjectLoadingSettingsRepository extends CrudRepository<DeviceObjectLoadingSettings, Long> {

	public List<DeviceObjectLoadingSettings> findByDeviceObjectId(Long deviceObjectId);

}
