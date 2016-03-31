package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.TemperatureChartItem;

public interface TemperatureChartItemRepository extends CrudRepository<TemperatureChartItem, Long> {

	@Query("SELECT i FROM TemperatureChartItem i WHERE i.temperatureChartId = :temperatureChartId "
			+ " ORDER BY t_Ambience DESC NULLS LAST")
	public List<TemperatureChartItem> selectTemperatureChartItems(@Param("temperatureChartId") Long temperatureChartId);

}
