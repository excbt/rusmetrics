package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.DeviceObject;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceObjectDTO;

/**
 * Created by kovtonyk on 23.05.2017.
 */
@Mapper(componentModel = "spring")
public interface DeviceObjectMapper {

    DeviceObjectDTO deviceObjectToDeviceObjectDTO(DeviceObject deviceObject);

    DeviceObjectDTO.DeviceLoginInfoDTO deviceLoginInfoToDTO(DeviceObject.DeviceLoginInfo loginInfo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "deviceObjectLastInfo", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deviceLogin", ignore = true)
    @Mapping(target = "devicePassword", ignore = true)
    @Mapping(target = "contObject", ignore = true)
    @Mapping(target = "deviceObjectDataSources", ignore = true)
    @Mapping(target = "deviceModel", source = "deviceModelId")
    @Mapping(target = "deviceObjectName", source = "deviceObjectName")
    void updateDeviceObjectFromDto(DeviceObjectDTO dto,
                                   @MappingTarget DeviceObject deviceObject);

    void updateDeviceLoginInfoFromDto(DeviceObjectDTO.DeviceLoginInfoDTO dto,
                                      @MappingTarget DeviceObject.DeviceLoginInfo loginInfo);


    default DeviceModel deviceModelFromId(Long id) {
        if (id == null) {
            return null;
        }
        return new DeviceModel().id(id);
    }

}
