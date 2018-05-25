package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.HeatRadiatorType;
import ru.excbt.datafuse.nmk.service.dto.DeviceModelDTO;

import java.util.Map;

/**
 * Created by kovtonyk on 26.05.2017.
 */
@Mapper(componentModel = "spring")
public interface DeviceModelMapper {


    DeviceModelDTO deviceModelToDto (DeviceModel deviceModel);

    DeviceModelDTO toDto (DeviceModel deviceModel);

//    @Mapping(source = "modelName", target = "deviceModelName")
//    DeviceModelDTO2 toDto2 (DeviceModel deviceModel);

    default DeviceModel deviceModelFromId(Long id) {
        return id == null ? null : new DeviceModel().id(id);
    }

    default Map<Long, Double> stringsFromAuthorities (Map<HeatRadiatorType,Double> heatRadiatorTypeDoubleMap) {
        //return authorities.stream().map(Authority::getName)
        //    .collect(Collectors.toSet());
        return null;
    }

}
