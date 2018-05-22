package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.ContObjectAccess;
import ru.excbt.datafuse.nmk.service.dto.ContObjectAccessDTO;
import ru.excbt.datafuse.nmk.service.vm.ContObjectAccessVM;

@Mapper(componentModel = "spring")
public interface ContObjectAccessMapper {

    @Mapping(source = "contObject.name", target = "contObjectName")
    @Mapping(source = "contObject.fullName", target = "contObjectFullName")
    @Mapping(source = "contObject.fullAddress", target = "contObjectFullAddress")
    @Mapping(source = "contObject.number", target = "contObjectNumber")
    ContObjectAccessDTO toDto(ContObjectAccess contObjectAccess);

    @Mapping(source = "contObject.name", target = "contObjectName")
    @Mapping(source = "contObject.fullName", target = "contObjectFullName")
    @Mapping(source = "contObject.fullAddress", target = "contObjectFullAddress")
    @Mapping(source = "contObject.number", target = "contObjectNumber")
    @Mapping(target = "allContZPointCnt", ignore = true)
    @Mapping(target = "accessContZPointCnt", ignore = true)
    @Mapping(target = "accessEnabled", ignore = true)
    @Mapping(source = "grantTz", target = "grantTz")
    @Mapping(source = "revokeTz", target = "revokeTz")
    ContObjectAccessVM toVM(ContObjectAccess contObjectAccess);

}
