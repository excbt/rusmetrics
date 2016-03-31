package ru.excbt.datafuse.nmk.data.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ru.excbt.datafuse.nmk.data.model.TemperatureChart;

public interface TemperatureChartRepository extends CrudRepository<TemperatureChart, Long> {

	/**
	 * 
	 * @return
	 */
	@Query("SELECT c FROM TemperatureChart c WHERE c.deleted = 0 AND c.isOk = TRUE "
			+ " ORDER BY c.localPlace.localPlaceName, c.rsoOrganization.organizationName, c.chartName")
	public List<TemperatureChart> selectTemperatureCharts();

	/**
	 * 
	 * @param fiasUUID
	 * @return
	 */
	@Query("SELECT c FROM TemperatureChart c WHERE c.deleted = 0 AND c.isOk = TRUE "
			+ " AND c.localPlace.fiasUuid = :fiasUUID "
			+ " ORDER BY c.localPlace.localPlaceName, c.rsoOrganization.organizationName, c.chartName")
	public List<TemperatureChart> selectTemperatureChartsByFias(@Param("fiasUUID") UUID fiasUUID);

}
