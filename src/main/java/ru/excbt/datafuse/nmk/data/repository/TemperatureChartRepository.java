package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ru.excbt.datafuse.nmk.data.model.TemperatureChart;

public interface TemperatureChartRepository extends CrudRepository<TemperatureChart, Long> {

	/**
	 * 
	 * @return
	 */
	@Query("SELECT c FROM TemperatureChart c WHERE c.deleted = 0 "
			+ " ORDER BY c.localPlace.localPlaceName, c.rsoOrganization.organizationName, c.chartName")
	public List<TemperatureChart> selectTemperatureCharts();

}
