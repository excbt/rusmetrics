package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.ContEventType;
import ru.excbt.datafuse.nmk.data.model.dto.ContEventTypeDTO;

@Mapper(componentModel = "spring", uses = {})
public interface ContEventTypeMapper extends EntityMapper <ContEventTypeDTO, ContEventType> {

}
