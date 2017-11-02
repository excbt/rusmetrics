package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.dto.ContEventCategoryDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.ContEventCategory;

@Mapper(componentModel = "spring", uses = {ContEventTypeMapper.class})
public interface ContEventCategoryMapper extends EntityMapper<ContEventCategoryDTO, ContEventCategory> {

}
