package ru.excbt.datafuse.nmk.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.excbt.datafuse.nmk.data.model.Subscriber;
import ru.excbt.datafuse.nmk.service.vm.SubscriberAccessStatsVM;

@Mapper(componentModel = "spring")
public interface SubscriberAccessStatsMapper {

    @Mapping(source = "info", target = "subscriberInfo")
    @Mapping(source = "comment", target = "subscriberComment")
    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "organization.inn", target = "organizationInn")
    @Mapping(source = "organization.organizationName", target = "organizationName")
    SubscriberAccessStatsVM toVM (Subscriber subscriber);

}
