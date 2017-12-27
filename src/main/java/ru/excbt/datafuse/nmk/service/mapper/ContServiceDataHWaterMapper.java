package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.ContServiceDataHWater;
import ru.excbt.datafuse.nmk.service.dto.ContServiceDataHWaterDTO;

@Mapper(componentModel = "spring")
public interface ContServiceDataHWaterMapper extends EntityMapper<ContServiceDataHWaterDTO, ContServiceDataHWater> {
}
