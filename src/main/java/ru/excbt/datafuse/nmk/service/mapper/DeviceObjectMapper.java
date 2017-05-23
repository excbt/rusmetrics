package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;

/**
 * Created by kovtonyk on 23.05.2017.
 */
@Mapper(componentModel = "spring")
public interface DeviceObjectMapper {

    DeviceObjectDTO deviceObjectToDeviceObjectDTO(DeviceObject deviceObject);

    DeviceObjectDTO.DeviceLoginInfoDTO deviceLoginInfoToDTO(DeviceObject.DeviceLoginInfo loginInfo);

    void updateDeviceObjectFromDto(DeviceObjectDTO dto,
                                   @MappingTarget DeviceObject deviceObject);

    void updateDeviceLoginInfoFromDto(DeviceObjectDTO.DeviceLoginInfoDTO dto,
                                      @MappingTarget DeviceObject.DeviceLoginInfo loginInfo);

}
