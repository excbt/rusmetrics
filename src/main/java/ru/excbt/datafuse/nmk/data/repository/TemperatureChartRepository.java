package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.TemperatureChart;

public interface TemperatureChartRepository extends CrudRepository<TemperatureChart, Long> {

}
