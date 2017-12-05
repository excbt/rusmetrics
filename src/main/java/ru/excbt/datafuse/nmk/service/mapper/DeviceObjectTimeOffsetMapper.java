package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.V_DeviceObjectTimeOffset;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectTimeOffsetDTO;

@Mapper(componentModel = "spring")
public interface DeviceObjectTimeOffsetMapper {

    @Mapping(target = "deviceObjectId", source = "deviceObject.id")
    DeviceObjectTimeOffsetDTO toDto(V_DeviceObjectTimeOffset entity);

}
