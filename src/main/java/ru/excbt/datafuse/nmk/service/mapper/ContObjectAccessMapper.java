package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccess;
import ru.excbt.datafuse.nmk.service.dto.ContObjectAccessDTO;

@Mapper(componentModel = "spring")
public interface ContObjectAccessMapper {

    @Mapping(source = "contObject.name", target = "contObjectName")
    @Mapping(source = "contObject.fullName", target = "contObjectFullName")
    @Mapping(source = "contObject.fullAddress", target = "contObjectFullAddress")
    @Mapping(source = "contObject.number", target = "contObjectNumber")
    ContObjectAccessDTO toDto(ContObjectAccess contObjectAccess);

}
