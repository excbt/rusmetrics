package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;

@Mapper(componentModel = "spring", uses = {ContManagementMapper.class})
public interface ContZPointMapper {

    ContZPointDTO toDto (ContZPoint contZPoint);

}
