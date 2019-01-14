package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.TemperatureChart;
import ru.excbt.datafuse.nmk.data.model.dto.TemperatureChartDTO;

@Mapper(componentModel = "spring", uses = {OrganizationMapper.class, LocalPlaceMapper.class})
public interface TemperatureChartMapper extends EntityMapper<TemperatureChartDTO, TemperatureChart> {

    /**
     *
     * @param id
     * @return
     */
    default TemperatureChart temperatureChartFromId(Long id) {
        return id != null ? new TemperatureChart().id(id) : null;
    }

}
