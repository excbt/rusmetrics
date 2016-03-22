package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.LocalPlaceTemperatureSst;

public interface LocalPlaceTemperatureSstRepository extends CrudRepository<LocalPlaceTemperatureSst, Long> {

	@Query("SELECT sst FROM LocalPlaceTemperatureSst sst WHERE sst.localPlaceId = :localPlaceId AND sst.deleted = 0 "
			+ " AND sst.sstDate BETWEEN :beginDate AND :endDate " + " ORDER BY sst.sstDate ")
	public List<LocalPlaceTemperatureSst> selectByLocalPlace(@Param("localPlaceId") Long localPlaceId,
			@Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

}
