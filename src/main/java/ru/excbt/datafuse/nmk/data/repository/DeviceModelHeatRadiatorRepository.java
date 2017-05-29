package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.excbt.datafuse.nmk.data.model.DeviceModelHeatRadiator;
import ru.excbt.datafuse.nmk.data.model.DeviceModelHeatRadiatorId;

import java.util.List;

/**
 * Created by kovtonyk on 29.05.2017.
 */
public interface DeviceModelHeatRadiatorRepository extends JpaRepository<DeviceModelHeatRadiator, DeviceModelHeatRadiatorId> {

    @Query ("SELECT d FROM DeviceModelHeatRadiator d WHERE d.deviceModelHeatRadiatorId.deviceModel.id = :deviceModelId")
    List<DeviceModelHeatRadiator> findByDeviceModel (@Param("deviceModelId") Long deviceModelId);

}
