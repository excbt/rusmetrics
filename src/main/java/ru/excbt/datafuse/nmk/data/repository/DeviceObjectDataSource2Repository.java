package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource2;

@Repository
public interface DeviceObjectDataSource2Repository extends CrudRepository<DeviceObjectDataSource2, Long> {

}
