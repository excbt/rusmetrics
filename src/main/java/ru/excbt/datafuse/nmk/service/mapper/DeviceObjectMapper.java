package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.excbt.datafuse.nmk.data.filters.ObjectFilters;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by kovtonyk on 23.05.2017.
 */
@Mapper(componentModel = "spring", uses = { DeviceObjectTimeOffsetMapper.class,
                                            HeatRadiatorTypeMapper.class,
                                            DeviceModelMapper.class,
                                            ContObjectMapper.class,
                                            DeviceObjectDataSourceMapper.class})
public interface DeviceObjectMapper extends EntityMapper<DeviceObjectDTO, DeviceObject> {

//    DeviceObjectDTO deviceObjectToDeviceObjectDTO(DeviceObject deviceObject);

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


    @Mapping(target = "deviceModelId", source = "deviceModel.id")
    @Mapping(target = "contObjectFullName", source = "contObject.fullName")
    @Mapping(target = "contObjectName", source = "contObject.name")
    @Mapping(target = "contObjectId", source = "contObject.id")
    @Mapping(target = "heatRadiatorTypeId", source = "heatRadiatorType.id")
    @Mapping(target = "editDataSourceInfo", source = "deviceObjectDataSources")
    @Mapping(target = "subscrDataSourceId", source = "editDataSourceInfo.subscrDataSourceId")
    @Mapping(target = "activeDataSource", source = "deviceObjectDataSources")
    DeviceObjectFullVM toFullVM(DeviceObject deviceObject);

    @Mapping(target = "deviceModel", source = "deviceModelId")
    @Mapping(target = "contObject", source = "contObjectId")
    @Mapping(target = "heatRadiatorType", source = "heatRadiatorTypeId")
    DeviceObject toEntity(DeviceObjectDTO dto);


    @Mapping(target = "contObjectId", source = "contObject.id")
    @Mapping(target = "deviceModelId", source = "deviceModel.id")
    @Mapping(target = "deviceModelName", source = "deviceModel.modelName")
    @Mapping(target = "verificationDate", source = "verificationDate")
    @Mapping(target = "number", source = "number")
    DeviceObjectShortInfoDTO toShortInfoDTO(DeviceObject deviceObject);


    @Mapping(target = "contObjectId", source = "contObject.id")
    @Mapping(target = "deviceModelId", source = "deviceModel.id")
    DeviceObjectDTO toDto (DeviceObject deviceObject);


    /**
     *
     * @param deviceObjectDataSources
     * @return
     */
    default DeviceObjectDataSource fromDeviceObjectDataSources(List<DeviceObjectDataSource> deviceObjectDataSources) {
        if (deviceObjectDataSources == null) {
            return null;
        }
        Optional<DeviceObjectDataSource> dataSource = ObjectFilters.activeFilter(deviceObjectDataSources.stream())
            .findFirst();
        DeviceObjectDataSource result = dataSource.isPresent() ? dataSource.get() : null;
        return result;
    }

    /**
     *
     * @param deviceObjectDataSources
     * @return
     */
    default EditDataSourceDTO fromDeviceObjectDataSourcesEdit(List<DeviceObjectDataSource> deviceObjectDataSources) {
        if (deviceObjectDataSources == null) {
            return null;
        }
        Optional<DeviceObjectDataSource> dataSource = ObjectFilters.activeFilter(deviceObjectDataSources.stream())
            .findFirst();
        DeviceObjectDataSource result = dataSource.isPresent() ? dataSource.get() : null;
        return result != null ? new EditDataSourceDTO(result) : new EditDataSourceDTO();
    }


    /**
     *
     * @param id
     * @return
     */
    default DeviceObject deviceObjectFromId(Long id) {
        return id == null ? null : new DeviceObject().id(id);
    }


//    default HeatRadiatorType heatRadiatorTypeFromId(Long id) {
//        return id == null ? null : new HeatRadiatorType().id(id);
//    }

//    default ContObject contObjectFromId(Long id) {
//        return id == null ? null : new ContObject().id(id);
//    }
//
//    default DeviceModel deviceModelFromId(Long id) {
//        return id == null ? null : new DeviceModel().id(id);
//    }
//
//
}
