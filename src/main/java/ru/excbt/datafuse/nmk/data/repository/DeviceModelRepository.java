package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.DeviceModel;

public interface DeviceModelRepository extends
		CrudRepository<DeviceModel, Long> {

}
