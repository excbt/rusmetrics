package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.TemperatureChart;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointFullVM;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;

@Mapper(componentModel = "spring", uses = {ContServiceTypeMapper.class,
    TemperatureChartMapper.class, DeviceObjectMapper.class})
public interface ContZPointMapper extends EntityMapper<ContZPointDTO, ContZPoint> {


    @Override
    @Mapping(target = "contObject", source = "contObjectId")
    @Mapping(target = "deviceObjects", ignore = true)
    @Mapping(target = "_activeDeviceObjectId", ignore = true)
    @Mapping(target = "rso", source = "rsoId")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    ContZPoint toEntity (ContZPointDTO dto);

    @Override
    @Mapping(target = "rsoId", source = "rso.id")
    ContZPointDTO toDto (ContZPoint contZPoint);

    @Mapping(target = "rsoId", source = "rso.id")
    ContZPointFullVM toStatsVM (ContZPoint contZPoint);

    @Mapping(target = "contObject", source = "contObjectId")
    @Mapping(target = "contServiceType", source = "contServiceTypeKeyname")
    @Mapping(target = "deviceObjects", ignore = true)
    @Mapping(target = "_activeDeviceObjectId", ignore = true)
    @Mapping(target = "rso", source = "rsoId")
    @Mapping(target = "temperatureChart", source = "temperatureChartId")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    ContZPoint toEntity (ContZPointFullVM contZPointFullVM);


    default ContObject contObjectFromId(Long id) {
        return id != null ? new ContObject().id(id) : null;
    }

    default Organization organizationFromId(Long id) {
        return id != null ? new Organization().id(id) : null;
    }

    default TemperatureChart temperatureChartFromId(Long id) {
        return id != null ? new TemperatureChart().id(id) : null;
    }

    default ContServiceType contServiceTypeFromKeyname(String keyname) {
        if (keyname == null) {
            return null;
        }
        ContServiceType result = new ContServiceType();
        result.setKeyname(keyname);
        return result;
    }

}
