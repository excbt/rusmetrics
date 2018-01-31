package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.domain.ContZPointConsumption;
import ru.excbt.datafuse.nmk.service.ContZPointConsumptionDTO;

@Mapper(componentModel = "spring", uses = {ContZPointMapper.class})
public interface ContZPointConsumptionMapper {

    ContZPointConsumptionDTO toDto(ContZPointConsumption consumption);

}
