package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.DeviceObjectPkeWarn;

public interface DeviceObjectPkeWarnRepository
		extends PagingAndSortingRepository<DeviceObjectPkeWarn, Long>, JpaSpecificationExecutor<DeviceObjectPkeWarn> {

	/**
	 * 
	 * @param deviceObjectId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@Query("SELECT w FROM DeviceObjectPkeWarn w WHERE w.deviceObjectId = :deviceObjectId AND "
			+ "((w.warnStartDate BETWEEN :beginDate AND :endDate) OR (w.warnEndDate BETWEEN :beginDate AND :endDate)) ")
	public List<DeviceObjectPkeWarn> selectDeviceObjectWarn(@Param("deviceObjectId") Long deviceObjectId,
			@Param("beginDate") Date beginDate, @Param("endDate") Date endDate, Pageable pageable);

}
