package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectMetaVzlet;

public interface DeviceObjectMetaVzletRepository extends
		CrudRepository<DeviceObjectMetaVzlet, Long> {

	public List<DeviceObjectMetaVzlet> findByDeviceObjectId(Long deviceObjectId);

}