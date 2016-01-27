package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectLoadingLog;

public interface DeviceObjectLoadingLogRepository extends CrudRepository<DeviceObjectLoadingLog, Long> {

	public List<DeviceObjectLoadingLog> findByDeviceObjectId(Long deviceObjectId);
}
