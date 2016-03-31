package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.excbt.datafuse.nmk.data.model.WeatherForecast;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {

}
