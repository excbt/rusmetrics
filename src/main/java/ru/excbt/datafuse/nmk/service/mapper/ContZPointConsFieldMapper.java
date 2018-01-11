package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.ContZPointConsField;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointConsFieldDTO;

@Mapper(componentModel = "spring", uses = {ContZPointMapper.class})
public interface ContZPointConsFieldMapper {

    //@Mapping(target = "contZPointId", source = "contZPoint.id")
    ContZPointConsFieldDTO toDto(ContZPointConsField entity);

    //@Mapping(target = "contZPoint", source = "contZPointId")
    ContZPointConsField toEntity(ContZPointConsFieldDTO dto);

}
