package ru.excbt.datafuse.nmk.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.WeatherForecastCalc;

public interface WeatherForecastCalcRepository extends JpaRepository<WeatherForecastCalc, Long> {

	@Query("SELECT fc FROM WeatherForecastCalc fc WHERE fc.weatherPlaceId = :weatherPlaceId "
			+ " AND fc.forecastDate = :forecastDate AND fc.weatherForecastType = :weatherForecastType AND "
			+ " fc.calcType = :calcType ")
	public List<WeatherForecastCalc> selectForecastCalc(@Param("weatherPlaceId") Long weatherPlaceId,
			@Param("forecastDate") Date forecastDate, @Param("weatherForecastType") String weatherForecastType,
			@Param("calcType") String calcType);
}
