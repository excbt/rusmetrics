package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.service.dto.OrganizationDTO;

/**
 * Created by kovtonyk on 13.07.2017.
 */
@Mapper(componentModel = "spring")

public interface OrganizationMapper {

    @Mapping(target = "organizationTypeId", source = "organizationType.id")
    @Mapping(target = "organizationTypeName", source = "organizationType.typeName")
    OrganizationDTO toDTO(Organization organization);

    Organization.OrganizationInfo toShortInfo(Organization organization);


    /**
     *
     * @param id
     * @return
     */
    default Organization organizationFromId(Long id) {
        return id != null ? new Organization().id(id) : null;
    }

}
