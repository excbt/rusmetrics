package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.StPlanTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlan;

@Mapper(componentModel = "spring", uses = {})
public interface StPlanTemplateMapper {


    @Mapping(target = "stPlanTemplateKey", source = "keyname")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subscriberId", ignore = true)
    @Mapping(target = "settingMode", ignore = true)
    @Mapping(target = "localPlace", ignore = true)
    @Mapping(target = "rsoOrganization", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    SubscrStPlan planTemplateToSubscrStPlan(StPlanTemplate stPlanTemplate);

}
