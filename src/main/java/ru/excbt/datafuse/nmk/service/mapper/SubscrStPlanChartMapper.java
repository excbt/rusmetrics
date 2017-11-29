package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import ru.excbt.datafuse.nmk.data.model.SubscrStPlanChart;
import ru.excbt.datafuse.nmk.data.model.dto.SubscrStPlanChartDTO;

@Mapper(componentModel = "spring", uses = {})
public interface SubscrStPlanChartMapper extends EntityMapper<SubscrStPlanChartDTO, SubscrStPlanChart> {

}
