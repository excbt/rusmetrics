package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.dto.OrganizationDTO;

/**
 * Created by kovtonyk on 13.07.2017.
 */
@Mapper(componentModel = "spring")
public interface OrganizationMapper {

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
