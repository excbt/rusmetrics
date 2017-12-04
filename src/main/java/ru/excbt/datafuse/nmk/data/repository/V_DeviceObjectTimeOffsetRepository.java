package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.V_DeviceObjectTimeOffset;

public interface V_DeviceObjectTimeOffsetRepository extends JpaRepository<V_DeviceObjectTimeOffset, Long> {

	/**
	 *
	 * @param deviceObjectIds
	 * @return
	 */
	@Query("SELECT d FROM V_DeviceObjectTimeOffset d WHERE d.deviceObjectId IN (:deviceObjectIds)")
	public List<V_DeviceObjectTimeOffset> selectDeviceObjectTimeOffsetList(
			@Param("deviceObjectIds") List<Long> deviceObjectIds);

}
