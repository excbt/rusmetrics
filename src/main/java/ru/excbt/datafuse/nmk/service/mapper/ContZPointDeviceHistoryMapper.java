package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.ContZPointDeviceHistory;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDeviceHistoryDTO;

@Mapper(componentModel = "spring", uses = { DeviceObjectMapper.class})
public interface ContZPointDeviceHistoryMapper extends EntityMapper<ContZPointDeviceHistoryDTO, ContZPointDeviceHistory> {

    @Mapping(target = "contZPointId", source = "contZPoint.id")
    @Mapping(target = "deviceObject", source = "deviceObject")
    ContZPointDeviceHistoryDTO toDto(ContZPointDeviceHistory entity);

}
