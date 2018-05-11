package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.keyname.TimezoneDef;
import ru.excbt.datafuse.nmk.service.dto.TimezoneDefDTO;

@Mapper(componentModel = "spring")
public interface TimezoneDefMapper {
    TimezoneDefDTO toDTO(TimezoneDef timezoneDef);
}
