package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataSource;

public interface DeviceObjectDataSourceRepository extends CrudRepository<DeviceObjectDataSource, Long> {

	@Query("SELECT ds FROM DeviceObjectDataSource ds WHERE ds.deviceObjectId = :deviceObjectId AND ds.isActive = true")
	public List<DeviceObjectDataSource> selectActiveDataSource(@Param("deviceObjectId") Long deviceObjectId);

	@Query("SELECT ds FROM DeviceObjectDataSource ds WHERE ds.deviceObjectId = :deviceObjectId AND ds.subscrDataSourceId = :subscrDataSourceId")
	public List<DeviceObjectDataSource> selectDeviceObjectDataSource(@Param("deviceObjectId") Long deviceObjectId,
			@Param("subscrDataSourceId") Long subscrDataSourceId);
}
