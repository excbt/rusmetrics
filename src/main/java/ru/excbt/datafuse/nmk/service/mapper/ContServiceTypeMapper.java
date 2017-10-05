package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.dto.ContServiceTypeDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;

@Mapper(componentModel = "spring")
public interface ContServiceTypeMapper  {

    ContServiceTypeDTO toDto(ContServiceType entity);

}
