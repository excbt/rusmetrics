package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.keyname.DeviceDataType;

public interface DeviceDataTypeRepository extends CrudRepository<DeviceDataType, String> {

}
