package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.domain.OrganizationType;
import ru.excbt.datafuse.nmk.service.dto.OrganizationTypeDTO;

@Mapper(componentModel = "spring")
public interface OrganizationTypeMapper extends EntityMapper<OrganizationTypeDTO, OrganizationType> {

    default OrganizationType organizationTypeFromId(Long id) {
        return id != null ? new OrganizationType().id(id) : null;
    }
}
