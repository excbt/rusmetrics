package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.HeatRadiatorType;
import ru.excbt.datafuse.nmk.data.model.dto.HeatRadiatorTypeDto;

/**
 * Created by kovtonyk on 26.05.2017.
 */
@Mapper(componentModel = "spring")
public interface HeatRadiatorTypeMapper {

    HeatRadiatorTypeDto heatRadiatorTypeToDto (HeatRadiatorType heatRadiatorType);

}
