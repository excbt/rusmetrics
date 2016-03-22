package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.WeatherForecastCalc;

public interface WeatherForecastCalcRepository extends JpaRepository<WeatherForecastCalc, Long> {

}
