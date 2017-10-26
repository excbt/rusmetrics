package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.ContEvent;
import ru.excbt.datafuse.nmk.data.model.dto.ContEventDTO;

@Mapper(componentModel = "spring", uses = {ContEventTypeMapper.class})
public interface ContEventMapper extends EntityMapper <ContEventDTO, ContEvent> {

}
