package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectDataJson;

public interface DeviceObjectDataJsonRepository extends
		PagingAndSortingRepository<DeviceObjectDataJson, Long> {

	@Query("SELECT d FROM DeviceObjectDataJson d "
			+ " WHERE d.deviceObject.id = :deviceObjectId "
			+ " ORDER BY d.deviceDate ")
	public List<DeviceObjectDataJson> selectByDeviceObject(
			@Param("deviceObjectId") long deviceObjectId, Pageable pageable);
}
