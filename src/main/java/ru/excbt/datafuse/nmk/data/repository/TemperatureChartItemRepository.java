package ru.excbt.datafuse.nmk.data.repository;

import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.TemperatureChartItem;

public interface TemperatureChartItemRepository extends CrudRepository<TemperatureChartItem, Long> {

}
