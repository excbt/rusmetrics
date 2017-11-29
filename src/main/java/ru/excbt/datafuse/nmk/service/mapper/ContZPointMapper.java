package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.ContObject;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.dto.ContZPointDTO;
import ru.excbt.datafuse.nmk.data.model.keyname.ContServiceType;

@Mapper(componentModel = "spring", uses = {ContServiceTypeMapper.class, TemperatureChartMapper.class})
public interface ContZPointMapper extends EntityMapper<ContZPointDTO, ContZPoint> {


    @Mapping(target = "contObject", source = "contObjectId")
    @Mapping(target = "deviceObjects", ignore = true)
    @Mapping(target = "_activeDeviceObjectId", ignore = true)
    @Mapping(target = "rso", source = "rsoOrganizationId")
    @Mapping(target = "rsoId", ignore = true)

    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    ContZPoint toEntity (ContZPointDTO dto);

    @Mapping(target = "rsoOrganizationId", source = "rso.id")
    ContZPointDTO toDto (ContZPoint contZPoint);

    default ContObject contObjectFromId(Long id) {
        return new ContObject().id(id);
    }

    default Organization organizationFromId(Long id) {
        return new Organization().id(id);
    }


}
