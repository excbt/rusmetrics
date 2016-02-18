package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectLoadingLog;

/**
 * Repository для DeviceObjectLoadingLog
 * 
 * @author A.Kovtonyuk
 * @version 1.0
 * @since 27.01.2015
 *
 */
public interface DeviceObjectLoadingLogRepository extends CrudRepository<DeviceObjectLoadingLog, Long> {

	public List<DeviceObjectLoadingLog> findByDeviceObjectId(Long deviceObjectId);
}
