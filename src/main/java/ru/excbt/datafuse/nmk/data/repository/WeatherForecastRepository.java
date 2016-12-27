package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.WeatherForecast;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {

	@Query("SELECT f FROM WeatherForecast f WHERE f.weatherPlaceId = :weatherPlaceId AND f.forecastDateTime <= :currentDateTime ORDER BY f.forecastDateTime DESC")
	public List<WeatherForecast> selectLastForecast(@Param("weatherPlaceId") Long weatherPlaceId,
			@Param("currentDateTime") Date currentDateTime, Pageable pageable);

}
