package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.LocalPlace;
import ru.excbt.datafuse.nmk.data.model.Organization;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlan;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrStPlanDTO;

@Mapper(componentModel = "spring", uses = {SubscrStPlanChartMapper.class})
public interface SubscrStPlanMapper extends EntityMapper<SubscrStPlanDTO, SubscrStPlan> {

    @Mapping(source = "rsoOrganization.id", target = "rsoOrganizationId")
    @Mapping(source = "localPlace.id", target = "localPlaceId")
    SubscrStPlanDTO toDto(SubscrStPlan entity);


    @Mapping(source = "rsoOrganizationId", target = "rsoOrganization")
    @Mapping(source = "localPlaceId", target = "localPlace")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    //@Mapping(target = "planCharts", ignore = true)
    SubscrStPlan toEntity(SubscrStPlanDTO dto);


    /**
     *
     * @param id
     * @return
     */
    default Organization organizationFromId(Long id) {
        if (id == null) {
            return null;
        }
        return new Organization().id(id);
    }

    /**
     *
     * @param id
     * @return
     */
    default LocalPlace localPlaceFromId(Long id) {
        if (id == null) {
            return null;
        }
        return new LocalPlace().id(id);
    }

}
