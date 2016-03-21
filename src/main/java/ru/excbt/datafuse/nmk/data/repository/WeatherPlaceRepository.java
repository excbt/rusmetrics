package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.WeatherPlace;

public interface WeatherPlaceRepository extends JpaRepository<WeatherPlace, Long> {

	@Query("SELECT wp FROM WeatherPlace wp WHERE (wp.isDisabled = null OR wp.isDisabled = FALSE) AND wp.deleted = 0 "
			+ " AND wp.fiasUuid = :fiasUUID " + " ORDER BY wp.placeName NULLS LAST")
	public List<WeatherPlace> selectWeatherPlacesByFias(@Param("fiasUUID") UUID fiasUUID);

}
