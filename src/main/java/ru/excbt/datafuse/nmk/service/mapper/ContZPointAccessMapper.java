package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.ContZPointAccess;
import ru.excbt.datafuse.nmk.service.dto.ContZPointAccessDTO;
import ru.excbt.datafuse.nmk.service.vm.ContZPointAccessVM;

@Mapper(componentModel = "spring")
public interface ContZPointAccessMapper {

    @Mapping(source = "contZPoint.contServiceTypeKeyname", target = "contServiceTypeKeyname")
    @Mapping(source = "contZPoint.tsNumber", target = "contZPointTsNumber")
    @Mapping(source = "contZPoint.customServiceName", target = "contZPointCustomServiceName")
    ContZPointAccessDTO toDto(ContZPointAccess contZPointAccess);

    @Mapping(source = "contZPoint.contServiceTypeKeyname", target = "contServiceTypeKeyname")
    @Mapping(source = "contZPoint.tsNumber", target = "contZPointTsNumber")
    @Mapping(source = "contZPoint.customServiceName", target = "contZPointCustomServiceName")
    @Mapping(target = "accessEnabled", ignore = true)
    @Mapping(source = "grantTz", target = "grantTz")
    @Mapping(source = "revokeTz", target = "revokeTz")
    ContZPointAccessVM toVM(ContZPointAccess contZPointAccess);

}
