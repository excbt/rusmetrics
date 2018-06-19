package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.keyname.ContGroupType;
import ru.excbt.datafuse.nmk.service.dto.ContGroupTypeDTO;

@Mapper(componentModel = "spring", uses = {})
public interface ContGroupTypeMapper {

    ContGroupTypeDTO toDto(ContGroupType contGroupType);

}
