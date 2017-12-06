package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.*;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointFullVM;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;

@Mapper(componentModel = "spring", uses = {ContServiceTypeMapper.class,
    TemperatureChartMapper.class, DeviceObjectMapper.class})
public interface ContZPointMapper extends EntityMapper<ContZPointDTO, ContZPoint> {


    @Override
    @Mapping(target = "contObject", source = "contObjectId")
    @Mapping(target = "deviceObject", ignore = true)
    @Mapping(target = "contServiceType", source = "contServiceTypeKeyname")
    @Mapping(target = "rso", source = "rsoId")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)

    @Mapping(target = "isManualLoading", source = "manualLoading")
    @Mapping(target = "isManual", source = "manual")
    @Mapping(target = "isDroolsDisable", source = "droolsDisable")
    @Mapping(target = "temperatureChart", source = "temperatureChartId")
    ContZPoint toEntity (ContZPointDTO dto);

    @Override
    @Mapping(target = "rsoId", source = "rso.id")
    @Mapping(target = "manualLoading", source = "isManualLoading")
    @Mapping(target = "manual", source = "isManual")
    @Mapping(target = "droolsDisable", source = "isDroolsDisable")
    @Mapping(target = "temperatureChartId", source = "temperatureChart.id")
    @Mapping(target = "tagNames", ignore = true)
    @Mapping(target = "deviceObjectId", source = "deviceObject.id")
    ContZPointDTO toDto (ContZPoint contZPoint);

    @Mapping(target = "rsoId", source = "rso.id")
    @Mapping(target = "deviceObjectId", source = "deviceObject.id")
    @Mapping(target = "manualLoading", source = "isManualLoading")
    @Mapping(target = "manual", source = "isManual")
    @Mapping(target = "droolsDisable", source = "isDroolsDisable")
    @Mapping(target = "tagNames", ignore = true)
    @Mapping(target = "timeDetailLastDates", ignore = true)
    ContZPointFullVM toFullVM(ContZPoint contZPoint);


    @Mapping(target = "contObject", source = "contObjectId")
    @Mapping(target = "contServiceType", source = "contServiceTypeKeyname")
    @Mapping(target = "deviceObject", source = "deviceObjectId")
    @Mapping(target = "rso", source = "rsoId")
    @Mapping(target = "temperatureChart", source = "temperatureChartId")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)

    @Mapping(target = "isManualLoading", source = "manualLoading")
    @Mapping(target = "isManual", source = "manual")
    @Mapping(target = "isDroolsDisable", source = "droolsDisable")
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


    default DeviceObject deviceObjectFromId(Long id) {
        return new DeviceObject().id(id);
    }

}
