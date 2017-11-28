package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.ContZPoint;
import ru.excbt.datafuse.nmk.data.model.SubscrContZPointStPlan;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlan;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrContZPointStPlanDTO;

@Mapper(componentModel = "spring", uses = {})
public interface SubscrContZPointStPlanMapper extends EntityMapper<SubscrContZPointStPlanDTO, SubscrContZPointStPlan> {


    @Mapping(target = "contZPoint", source = "contZPointId")
    @Mapping(target = "subscrStPlan", source = "subscrStPlanId")
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    SubscrContZPointStPlan toEntity(SubscrContZPointStPlanDTO dto);

    @Mapping(target = "contZPointId", source = "contZPoint.id")
    @Mapping(target = "subscrStPlanId", source = "subscrStPlan.id")
    SubscrContZPointStPlanDTO toDto(SubscrContZPointStPlan entity);

    default ContZPoint toContZPoint(Long id) {
        return new ContZPoint().id(id);
    }

    default SubscrStPlan toSubscrStPlan(Long id) {
        return new SubscrStPlan().id(id);
    }

}
