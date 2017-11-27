package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.StPlanChartTemplate;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlanChart;

@Mapper(componentModel = "spring", uses = {})
public interface StPlanChartTemplateMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subscrStPlan", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    SubscrStPlanChart planTemplateToSubscrStPlan(StPlanChartTemplate stPlanChartTemplate);

}
