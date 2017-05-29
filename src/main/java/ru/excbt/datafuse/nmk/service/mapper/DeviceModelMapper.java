package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.DeviceModel;
import ru.excbt.datafuse.nmk.data.model.HeatRadiatorType;
import ru.excbt.datafuse.nmk.data.model.dto.DeviceModelDTO;

import java.util.Map;

/**
 * Created by kovtonyk on 26.05.2017.
 */
@Mapper(componentModel = "spring")
public interface DeviceModelMapper {


    DeviceModelDTO deviceModelToDto (DeviceModel deviceModel);


    default Map<Long, Double> stringsFromAuthorities (Map<HeatRadiatorType,Double> heatRadiatorTypeDoubleMap) {
        //return authorities.stream().map(Authority::getName)
        //    .collect(Collectors.toSet());
        return null;
    }

}
